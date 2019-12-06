package cm.aptoide.pt.home.apps;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.bottomNavigation.BottomNavigationActivity;
import cm.aptoide.pt.bottomNavigation.BottomNavigationItem;
import cm.aptoide.pt.home.apps.list.AppsController;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import cm.aptoide.pt.view.rx.RxAlertDialog;
import com.airbnb.epoxy.EpoxyRecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;
import com.jakewharton.rxbinding.view.RxView;
import javax.inject.Inject;
import rx.Observable;
import rx.Single;
import rx.subjects.PublishSubject;

import static cm.aptoide.pt.utils.GenericDialogs.EResponse.YES;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public class AppsFragment extends NavigationTrackFragment implements AppsFragmentView {

  private static final BottomNavigationItem BOTTOM_NAVIGATION_ITEM = BottomNavigationItem.APPS;

  @Inject AppsPresenter appsPresenter;
  private RxAlertDialog ignoreUpdateDialog;
  private ImageView userAvatar;
  private ProgressBar progressBar;
  private BottomNavigationActivity bottomNavigationActivity;
  private SwipeRefreshLayout swipeRefreshLayout;
  private PublishSubject<Void> appcUpgradesSectionLoaded;

  private EpoxyRecyclerView appsRecyclerView;
  private AppsController appsController;

  public static AppsFragment newInstance() {
    return new AppsFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    appcUpgradesSectionLoaded = PublishSubject.create();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (bottomNavigationActivity != null) {
      bottomNavigationActivity.requestFocus(BOTTOM_NAVIGATION_ITEM);
    }
    appsRecyclerView = view.findViewById(R.id.fragment_apps_recycler_view);
    setupRecyclerView();

    swipeRefreshLayout = view.findViewById(R.id.fragment_apps_swipe_container);
    swipeRefreshLayout.setColorSchemeResources(R.color.default_progress_bar_color,
        R.color.default_color, R.color.default_progress_bar_color, R.color.default_color);
    progressBar = view.findViewById(R.id.progress_bar);
    progressBar.setVisibility(View.VISIBLE);
    buildIgnoreUpdatesDialog();
    userAvatar = view.findViewById(R.id.user_actionbar_icon);

    attachPresenter(appsPresenter);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  private void setupRecyclerView() {
    appsController = new AppsController();
    appsRecyclerView.setController(appsController);
    appsController.getAdapter()
        .registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
          @Override public void onItemRangeInserted(int positionStart, int itemCount) {
            if (positionStart == 0) {
              appsRecyclerView.scrollToPosition(0);
            }
          }
        });
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity instanceof BottomNavigationActivity) {
      bottomNavigationActivity = ((BottomNavigationActivity) activity);
    }
  }

  private void buildIgnoreUpdatesDialog() {
    ignoreUpdateDialog =
        new RxAlertDialog.Builder(getContext()).setTitle(R.string.apps_title_ignore_updates)
            .setPositiveButton(R.string.apps_button_ignore_updates_yes)
            .setNegativeButton(R.string.apps_button_ignore_updates_no)
            .build();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_apps, container, false);
  }

  @Override public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    appsController.onRestoreInstanceState(savedInstanceState);
  }

  @Override public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    appsController.onSaveInstanceState(outState);
  }

  @Override public Observable<App> installApp() {
    return appsController.getAppEventListener()
        .filter(appClick -> appClick.getClickType() == AppClick.ClickType.INSTALL_CLICK)
        .map(AppClick::getApp);
  }

  @Override public Observable<App> cancelDownload() {
    return appsController.getAppEventListener()
        .filter(appClick -> appClick.getClickType() == AppClick.ClickType.CANCEL_CLICK)
        .map(AppClick::getApp);
  }

  @Override public Observable<App> resumeDownload() {
    return appsController.getAppEventListener()
        .filter(appClick -> appClick.getClickType() == AppClick.ClickType.RESUME_CLICK)
        .map(AppClick::getApp);
  }

  @Override public Observable<App> pauseDownload() {
    return appsController.getAppEventListener()
        .filter(appClick -> appClick.getClickType() == AppClick.ClickType.PAUSE_CLICK)
        .map(AppClick::getApp);
  }

  @Override public Observable<App> startDownloadInAppview() {
    return appsController.getAppEventListener()
        .filter(appClick -> appClick.getClickType() == AppClick.ClickType.APPC_ACTION_CLICK)
        .map(AppClick::getApp);
  }

  @Override public Observable<App> startDownload() {
    return appsController.getAppEventListener()
        .filter(appClick -> appClick.getClickType() == AppClick.ClickType.DOWNLOAD_ACTION_CLICK)
        .map(AppClick::getApp);
  }

  @Override public Observable<Boolean> showRootWarning() {
    return GenericDialogs.createGenericYesNoCancelMessage(getContext(), "",
        AptoideUtils.StringU.getFormattedString(R.string.root_access_dialog, getResources()))
        .map(response -> (response.equals(YES)));
  }

  @Override public Observable<Void> updateAll() {
    return appsController.getUpdateAllEvent();
  }

  @Override public Observable<App> updateLongClick() {
    return appsController.getAppEventListener()
        .filter(appClick -> appClick.getClickType() == AppClick.ClickType.CARD_LONG_CLICK)
        .map(AppClick::getApp);
  }

  @Override public void showIgnoreUpdate() {
    ignoreUpdateDialog.show();
  }

  @Override public Single<RxAlertDialog.Result> showIgnoreUpdateDialog() {
    return ignoreUpdateDialog.showWithResult();
  }

  @Override public Observable<Void> ignoreUpdate() {
    return ignoreUpdateDialog.positiveClicks()
        .map(__ -> null);
  }

  @Override public void showUnknownErrorMessage() {
    Snackbar.make(this.getView(), R.string.unknown_error, Snackbar.LENGTH_SHORT)
        .show();
  }

  @Override public Observable<App> cardClick() {
    return appsController.getAppEventListener()
        .filter(appClick -> appClick.getClickType() == AppClick.ClickType.CARD_CLICK)
        .map(AppClick::getApp);
  }

  @Override public void setUserImage(String userAvatarUrl) {
    ImageLoader.with(getContext())
        .loadWithShadowCircleTransformWithPlaceholder(userAvatarUrl, userAvatar,
            R.drawable.ic_account_circle);
  }

  @Override public void showAvatar() {
    userAvatar.setVisibility(View.VISIBLE);
  }

  @Override public Observable<Void> imageClick() {
    return RxView.clicks(userAvatar);
  }

  @UiThread @Override public void scrollToTop() {
    LinearLayoutManager layoutManager = ((LinearLayoutManager) appsRecyclerView.getLayoutManager());
    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
    if (lastVisibleItemPosition > 10) {
      appsRecyclerView.scrollToPosition(10);
    }
    appsRecyclerView.smoothScrollToPosition(0);
  }

  @Override public Observable<Void> refreshApps() {
    return RxSwipeRefreshLayout.refreshes(swipeRefreshLayout);
  }

  @Override public void hidePullToRefresh() {
    if (swipeRefreshLayout.isRefreshing()) {
      swipeRefreshLayout.setRefreshing(false);
    }
  }

  @Override public void setDefaultUserImage() {
    ImageLoader.with(getContext())
        .loadUsingCircleTransform(R.drawable.ic_account_circle, userAvatar);
  }

  @Override public void showModel(AppsModel model) {
    hideLoadingProgressBar();
    appsController.setData(model.getUpdates(), model.getInstalled(), model.getMigrations(),
        model.getDownloads());
  }

  @Override public Observable<Void> onLoadAppcUpgradesSection() {
    return appcUpgradesSectionLoaded;
  }

  private void hideLoadingProgressBar() {
    progressBar.setVisibility(View.GONE);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    progressBar = null;
    swipeRefreshLayout = null;
    ignoreUpdateDialog = null;
    appsRecyclerView = null;
    userAvatar = null;
  }

  @Override public void onDetach() {
    bottomNavigationActivity = null;
    super.onDetach();
  }
}
