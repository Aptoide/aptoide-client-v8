package cm.aptoide.pt.home.apps;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

import static cm.aptoide.pt.utils.GenericDialogs.EResponse.YES;

public class SeeMoreAppcFragment extends NavigationTrackFragment implements SeeMoreAppcView {

  @Inject SeeMoreAppcPresenter seeMoreAppcPresenter;

  private SwipeRefreshLayout swipeRefreshLayout;

  private RecyclerView appcAppsRecyclerView;
  private AppcAppsAdapter appcAppsAdapter;
  private PublishSubject<AppClick> appItemClicks;
  private Toolbar toolbar;

  public static Fragment newInstance() {
    return new SeeMoreAppcFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    appItemClicks = PublishSubject.create();
    setHasOptionsMenu(true);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    appcAppsRecyclerView = view.findViewById(R.id.appc_apps_recycler_view);
    appcAppsRecyclerView.setNestedScrollingEnabled(false);

    appcAppsAdapter = new AppcAppsAdapter(new ArrayList<>(), appItemClicks);
    appcAppsRecyclerView.setAdapter(appcAppsAdapter);
    appcAppsRecyclerView.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    appcAppsRecyclerView.setItemAnimator(null);

    swipeRefreshLayout = view.findViewById(R.id.fragment_apps_swipe_container);
    swipeRefreshLayout.setColorSchemeResources(R.color.default_progress_bar_color,
        R.color.default_color, R.color.default_progress_bar_color, R.color.default_color);

    toolbar = view.findViewById(R.id.toolbar);
    toolbar.setTitle(getString(R.string.promo_update2appc_appcard_short));
    AppCompatActivity appCompatActivity = ((AppCompatActivity) getActivity());
    appCompatActivity.setSupportActionBar(toolbar);
    ActionBar actionBar = appCompatActivity.getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    attachPresenter(seeMoreAppcPresenter);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_apps_see_more_appc, container, false);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    swipeRefreshLayout = null;
    appcAppsRecyclerView = null;
    appcAppsAdapter = null;
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_empty, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void showAppcUpgradesList(List<App> list) {
    if (list != null && !list.isEmpty()) {
      appcAppsAdapter.setAvailableUpgradesList(list);
    }
  }

  @Override public Observable<Void> refreshApps() {
    return RxSwipeRefreshLayout.refreshes(swipeRefreshLayout);
  }

  @Override public void hidePullToRefresh() {
    if (swipeRefreshLayout.isRefreshing()) {
      swipeRefreshLayout.setRefreshing(false);
    }
  }

  @Override public Observable<App> upgradeAppcApp() {
    return appItemClicks.filter(
        appClick -> appClick.getClickType() == AppClick.ClickType.APPC_UPGRADE_APP)
        .map(appClick -> appClick.getApp());
  }

  @Override public Observable<App> resumeAppcUpgrade() {
    return appItemClicks.filter(
        appClick -> appClick.getClickType() == AppClick.ClickType.APPC_UPGRADE_RESUME)
        .map(appClick -> appClick.getApp());
  }

  @Override public Observable<App> retryAppcUpgrade() {
    return appItemClicks.filter(
        appClick -> appClick.getClickType() == AppClick.ClickType.APPC_UPGRADE_RETRY)
        .map(appClick -> appClick.getApp());
  }

  @Override public Observable<App> cancelAppcUpgrade() {
    return appItemClicks.filter(
        appClick -> appClick.getClickType() == AppClick.ClickType.APPC_UPGRADE_CANCEL)
        .map(appClick -> appClick.getApp());
  }

  @Override public Observable<App> pauseAppcUpgrade() {
    return appItemClicks.filter(
        appClick -> appClick.getClickType() == AppClick.ClickType.APPC_UPGRADE_PAUSE)
        .map(appClick -> appClick.getApp());
  }

  @Override public Observable<Boolean> showRootWarning() {
    return GenericDialogs.createGenericYesNoCancelMessage(getContext(), "",
        AptoideUtils.StringU.getFormattedString(R.string.root_access_dialog, getResources()))
        .map(response -> (response.equals(YES)));
  }

  @Override public void setAppcStandbyState(App app) {
    appcAppsAdapter.setAppStandby(app);
  }

  @Override public void removeAppcCanceledAppDownload(App app) {
    appcAppsAdapter.removeCanceledAppDownload(app);
  }

  @Override public void setAppcPausingDownloadState(App app) {
    appcAppsAdapter.setAppOnPausing(app);
  }

  @Override public void showAppcUpgradesDownloadList(List<App> updatesDownloadList) {
    if (updatesDownloadList != null && !updatesDownloadList.isEmpty()) {
      appcAppsAdapter.addApps(updatesDownloadList);
    }
  }
}
