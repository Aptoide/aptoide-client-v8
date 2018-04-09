package cm.aptoide.pt.home.apps;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.home.BottomNavigationActivity;
import cm.aptoide.pt.home.BottomNavigationItem;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.updates.UpdatesAnalytics;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import cm.aptoide.pt.view.rx.RxAlertDialog;
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;
import com.jakewharton.rxbinding.view.RxView;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static cm.aptoide.pt.utils.GenericDialogs.EResponse.YES;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public class AppsFragment extends NavigationTrackFragment implements AppsFragmentView {

  private static final BottomNavigationItem BOTTOM_NAVIGATION_ITEM = BottomNavigationItem.APPS;
  @Inject DownloadAnalytics downloadAnalytics;
  @Inject InstallAnalytics installAnalytics;
  @Inject UpdatesAnalytics updatesAnalytics;
  @Inject AptoideAccountManager accountManager;
  @Inject AppsNavigator appsNavigator;
  private RecyclerView recyclerView;
  private AppsAdapter adapter;
  private PublishSubject<AppClick> appItemClicks;
  private PublishSubject<Void> updateAll;
  private RxAlertDialog ignoreUpdateDialog;
  private ImageView userAvatar;
  private ProgressBar progressBar;
  private BottomNavigationActivity bottomNavigationActivity;
  private SwipeRefreshLayout swipeRefreshLayout;
  private boolean showDownloads;
  private boolean showUpdates;
  private boolean showInstalled;

  public static AppsFragment newInstance() {
    return new AppsFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    appItemClicks = PublishSubject.create();
    updateAll = PublishSubject.create();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (bottomNavigationActivity != null) {
      bottomNavigationActivity.requestFocus(BOTTOM_NAVIGATION_ITEM);
    }
    recyclerView = (RecyclerView) view.findViewById(R.id.fragment_apps_recycler_view);
    adapter =
        new AppsAdapter(new ArrayList<>(), new AppsCardViewHolderFactory(appItemClicks, updateAll));
    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_apps_swipe_container);
    swipeRefreshLayout.setColorSchemeResources(R.color.default_progress_bar_color,
        R.color.default_color, R.color.default_progress_bar_color, R.color.default_color);
    progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
    progressBar.setVisibility(View.VISIBLE);
    setupRecyclerView();
    buildIgnoreUpdatesDialog();
    userAvatar = (ImageView) view.findViewById(R.id.user_actionbar_icon);

    attachPresenter(new AppsPresenter(this, new AppsManager(new UpdatesManager(
        RepositoryFactory.getUpdateRepository(getContext(),
            ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences())),
        ((AptoideApplication) getContext().getApplicationContext()).getInstallManager(),
        new AppMapper(), downloadAnalytics, installAnalytics, updatesAnalytics,
        getContext().getPackageManager(), getContext(), new DownloadFactory(
        ((AptoideApplication) getContext().getApplicationContext()).getMarketName())),
        AndroidSchedulers.mainThread(), Schedulers.computation(), CrashReport.getInstance(),
        new PermissionManager(), ((PermissionService) getContext()), accountManager,
        appsNavigator));
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity instanceof BottomNavigationActivity) {
      bottomNavigationActivity = ((BottomNavigationActivity) activity);
    }
  }

  @Override public void onDestroy() {
    updateAll = null;
    appItemClicks = null;
    super.onDestroy();
  }

  private void buildIgnoreUpdatesDialog() {
    ignoreUpdateDialog =
        new RxAlertDialog.Builder(getContext()).setTitle(R.string.apps_title_ignore_updates)
            .setPositiveButton(R.string.apps_button_ignore_updates_yes)
            .setNegativeButton(R.string.apps_button_ignore_updates_no)
            .build();
  }

  private void setupRecyclerView() {
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
    if (animator instanceof SimpleItemAnimator) {
      ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
    }
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_apps, container, false);
  }

  @Override public void showUpdatesList(List<App> list) {
    if (list != null && !list.isEmpty()) {
      adapter.addUpdateAppsList(list);
    }
    showUpdates = true;
    if (canShowListOfApps() && recyclerView.getVisibility() != View.VISIBLE) {
      hideLoadingProgressBar();
      recyclerView.setVisibility(View.VISIBLE);
      recyclerView.smoothScrollToPosition(0);
    }
  }

  @Override public void showInstalledApps(List<App> installedApps) {
    if (installedApps != null && !installedApps.isEmpty()) {
      adapter.addInstalledAppsList(installedApps);
    }
    showInstalled = true;
    if (canShowListOfApps() && recyclerView.getVisibility() != View.VISIBLE) {
      hideLoadingProgressBar();
      recyclerView.setVisibility(View.VISIBLE);
      recyclerView.smoothScrollToPosition(0);
    }
  }

  @Override public void showDownloadsList(List<App> list) {
    if (list != null && !list.isEmpty()) {
      adapter.addDownloadAppsList(list);
    }
    showDownloads = true;
    if (canShowListOfApps() && recyclerView.getVisibility() != View.VISIBLE) {
      hideLoadingProgressBar();
      recyclerView.setVisibility(View.VISIBLE);
      recyclerView.smoothScrollToPosition(0);
    }
  }

  @Override public Observable<App> retryDownload() {
    return appItemClicks.filter(
        appClick -> appClick.getClickType() == AppClick.ClickType.RETRY_DOWNLOAD)
        .map(appClick -> appClick.getApp());
  }

  @Override public Observable<App> installApp() {
    return appItemClicks.filter(
        appClick -> appClick.getClickType() == AppClick.ClickType.INSTALL_APP)
        .map(appClick -> appClick.getApp());
  }

  @Override public Observable<App> cancelDownload() {
    return appItemClicks.filter(
        appClick -> appClick.getClickType() == AppClick.ClickType.CANCEL_DOWNLOAD)
        .map(appClick -> appClick.getApp());
  }

  @Override public Observable<App> resumeDownload() {
    return appItemClicks.filter(
        appClick -> appClick.getClickType() == AppClick.ClickType.RESUME_DOWNLOAD)
        .map(appClick -> appClick.getApp());
  }

  @Override public Observable<App> pauseDownload() {
    return appItemClicks.filter(
        appClick -> appClick.getClickType() == AppClick.ClickType.PAUSE_DOWNLOAD)
        .map(appClick -> appClick.getApp());
  }

  @Override public Observable<App> retryUpdate() {
    return appItemClicks.filter(
        appClick -> appClick.getClickType() == AppClick.ClickType.RETRY_UPDATE)
        .map(appClick -> appClick.getApp());
  }

  @Override public Observable<App> updateApp() {
    return appItemClicks.filter(
        appClick -> appClick.getClickType() == AppClick.ClickType.UPDATE_APP)
        .map(appClick -> appClick.getApp());
  }

  @Override public Observable<App> pauseUpdate() {
    return appItemClicks.filter(
        appClick -> appClick.getClickType() == AppClick.ClickType.PAUSE_UPDATE)
        .map(appClick -> appClick.getApp());
  }

  @Override public Observable<App> cancelUpdate() {
    return appItemClicks.filter(
        appClick -> appClick.getClickType() == AppClick.ClickType.CANCEL_UPDATE)
        .map(appClick -> appClick.getApp());
  }

  @Override public Observable<App> resumeUpdate() {
    return appItemClicks.filter(
        appClick -> appClick.getClickType() == AppClick.ClickType.RESUME_UPDATE)
        .map(appClick -> appClick.getApp());
  }

  @Override public Observable<Boolean> showRootWarning() {
    return GenericDialogs.createGenericYesNoCancelMessage(getContext(), "",
        AptoideUtils.StringU.getFormattedString(R.string.root_access_dialog, getResources()))
        .map(response -> (response.equals(YES)));
  }

  @Override public void showUpdatesDownloadList(List<App> updatesDownloadList) {
    adapter.addUpdateAppsList(updatesDownloadList);
    showUpdates = true;
    if (canShowListOfApps() && recyclerView.getVisibility() != View.VISIBLE) {
      hideLoadingProgressBar();
      recyclerView.setVisibility(View.VISIBLE);
      recyclerView.smoothScrollToPosition(0);
    }
  }

  @Override public Observable<Void> updateAll() {
    return updateAll;
  }

  @Override public Observable<App> updateLongClick() {
    return appItemClicks.filter(
        appClick -> appClick.getClickType() == AppClick.ClickType.UPDATE_CARD_LONG_CLICK)
        .map(appClick -> appClick.getApp());
  }

  @Override public void showIgnoreUpdate() {
    ignoreUpdateDialog.show();
  }

  @Override public Observable<Void> ignoreUpdate() {
    return ignoreUpdateDialog.positiveClicks()
        .map(__ -> null);
  }

  @Override public void showUnknownErrorMessage() {
    Snackbar.make(this.getView(), R.string.unknown_error, Snackbar.LENGTH_SHORT)
        .show();
  }

  @Override public void removeExcludedUpdates(List<App> excludedUpdatesList) {
    adapter.removeUpdatesList(excludedUpdatesList);
  }

  @Override public Observable<App> updateClick() {
    return appItemClicks.filter(
        appClick -> appClick.getClickType() == AppClick.ClickType.UPDATE_CARD_CLICK)
        .map(appClick -> appClick.getApp());
  }

  @Override public void setUserImage(String userAvatarUrl) {
    ImageLoader.with(getContext())
        .loadWithCircleTransformAndPlaceHolder(userAvatarUrl, userAvatar,
            R.drawable.my_account_placeholder);
  }

  @Override public void showAvatar() {
    userAvatar.setVisibility(View.VISIBLE);
  }

  @Override public Observable<Void> imageClick() {
    return RxView.clicks(userAvatar);
  }

  @Override public void removeInstalledDownloads(List<App> installedDownloadsList) {
    adapter.removeInstalledDownloads(installedDownloadsList);
  }

  @UiThread @Override public void scrollToTop() {
    LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
    if (lastVisibleItemPosition > 10) {
      recyclerView.scrollToPosition(10);
    }
    recyclerView.smoothScrollToPosition(0);
  }

  @Override public void removeInstalledUpdates(List<App> installedUpdatesList) {
    adapter.removeUpdatesList(installedUpdatesList);
  }

  @Override public Observable<Void> refreshApps() {
    return RxSwipeRefreshLayout.refreshes(swipeRefreshLayout);
  }

  @Override public void hidePullToRefresh() {
    if (swipeRefreshLayout.isRefreshing()) {
      swipeRefreshLayout.setRefreshing(false);
    }
  }

  private boolean canShowListOfApps() {
    return showDownloads && showUpdates && showInstalled;
  }

  private void hideLoadingProgressBar() {
    progressBar.setVisibility(View.GONE);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    progressBar = null;
    swipeRefreshLayout = null;
    ignoreUpdateDialog = null;
    recyclerView = null;
    adapter = null;
    userAvatar = null;
  }

  @Override public void onDetach() {
    bottomNavigationActivity = null;
    super.onDetach();
  }
}
