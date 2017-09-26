package cm.aptoide.pt.view.search;

import android.os.Bundle;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.model.v7.DataList;
import cm.aptoide.pt.dataprovider.model.v7.search.ListSearchApps;
import cm.aptoide.pt.dataprovider.model.v7.search.SearchApp;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.search.SearchAnalytics;
import cm.aptoide.pt.search.SearchManager;
import cm.aptoide.pt.search.SearchNavigator;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;
import rx.Scheduler;

public class SearchPresenter implements Presenter {
  private final SearchView view;
  private final SearchAnalytics analytics;
  private final SearchNavigator navigator;
  private final CrashReport crashReport;
  private final Scheduler viewScheduler;
  private final SearchManager searchManager;
  private final PublishRelay<MinimalAd> onAdClickRelay;
  private final PublishRelay<SearchApp> onItemViewClickRelay;
  private final PublishRelay<SearchApp> onOpenPopupMenuClickRelay;

  public SearchPresenter(SearchView view, SearchAnalytics analytics, SearchNavigator navigator,
      CrashReport crashReport, Scheduler viewScheduler, SearchManager searchManager,
      PublishRelay<MinimalAd> onAdClickRelay, PublishRelay<SearchApp> onItemViewClickRelay,
      PublishRelay<SearchApp> onOpenPopupMenuClickRelay) {
    this.view = view;
    this.analytics = analytics;
    this.navigator = navigator;
    this.crashReport = crashReport;
    this.viewScheduler = viewScheduler;
    this.searchManager = searchManager;
    this.onAdClickRelay = onAdClickRelay;
    this.onItemViewClickRelay = onItemViewClickRelay;
    this.onOpenPopupMenuClickRelay = onOpenPopupMenuClickRelay;
  }

  @Override public void present() {
    search();
    onFollowedStoresSearchButtonClicked();
    onEverywhereSearchButtonClick();
    handleClickToOpenAppViewFromItemClick();
    handleClickToOpenAppViewFromAdd();
    handleClickToOpenPopupMenu();
    handleClickOnNoResultsImage();
    //handleOpenStore();
    //handleOtherVersionsClick();
  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
  }

  private void handleClickToOpenAppViewFromItemClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> onItemViewClickRelay)
        .doOnNext(data -> openAppView(data))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickToOpenAppViewFromAdd() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> onAdClickRelay)
        .doOnNext(data -> {
          analytics.searchAppClick(view.getViewModel()
              .getCurrentQuery(), data.getPackageName());
          navigator.goToAppView(data);
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickToOpenPopupMenu() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> onOpenPopupMenuClickRelay)
        .doOnNext(data -> {
          final boolean hasVersions = data.hasVersions();
          final String appName = data.getName();
          final String appIcon = data.getIcon();
          final String packageName = data.getPackageName();
          final String storeName = data.getStore()
              .getName();

          //FIXME which theme should be used?
          // final String theme = aptoideApplication.getDefaultTheme()
          final String theme = data.getStore()
              .getAppearance()
              .getTheme();

          view.showPopup(hasVersions, appName, appIcon, packageName, storeName, theme);
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnNoResultsImage() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> view.clickNoResultsSearchButton())
        .filter(query -> query.length() > 1)
        .doOnNext(query -> navigator.goToSearchFragment(query, view.getViewModel()
            .getStoreName()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  /*
  private void handleOpenStore() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> onOpenStoreClick)
        .doOnNext(data -> {
          //FIXME which theme should be used?
          // final String theme = aptoideApplication.getDefaultTheme()
          final String theme = data.getStore()
              .getAppearance()
              .getTheme();
          navigator.goToStoreFragment(data.getStore()
              .getName(), theme);
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }
  */

  /*
  private void handleOtherVersionsClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> onOtherVersionsClick)
        .doOnNext(data -> navigator.goToOtherVersions(data.getName(), data.getIcon(),
            data.getPackageName()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }
  */

  private void openAppView(SearchApp searchApp) {
    final String packageName = searchApp.getPackageName();
    final long appId = searchApp.getId();
    final String storeName = searchApp.getStore()
        .getName();

    // FIXME which theme should be used?
    //final String storeTheme = aptoideApplication.getDefaultTheme();
    final String storeTheme = searchApp.getStore()
        .getAppearance()
        .getTheme();

    analytics.searchAppClick(view.getViewModel()
        .getCurrentQuery(), packageName);
    navigator.goToAppView(appId, packageName, storeTheme, storeName);
  }

  private void onFollowedStoresSearchButtonClicked() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> view.clickFollowedStoresSearchButton())
        .doOnNext(__ -> view.showFollowedStoresResult())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void onEverywhereSearchButtonClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> view.clickEverywhereSearchButton())
        .doOnNext(__ -> view.showAllStoresResult())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private Observable<ListSearchApps> executeSearchRequests(String query, String storeName,
      boolean onlyTrustedApps) {

    if (storeName != null && !storeName.trim()
        .equals("")) {
      return searchManager.searchInStore(query, storeName)
          .observeOn(viewScheduler)
          .doOnNext(result -> view.changeFollowedStoresButtonVisibility(hasResults(result)))
          .doOnNext(result -> view.addFollowedStoresResult(result));
    }

    return Observable.merge(searchManager.searchInFollowedStores(query, onlyTrustedApps)
            .observeOn(viewScheduler)
            .doOnNext(result -> view.changeFollowedStoresButtonVisibility(hasResults(result)))
            .doOnNext(result -> view.addFollowedStoresResult(result)),
        searchManager.searchInNonSubscribedStores(query, onlyTrustedApps)
            .observeOn(viewScheduler)
            .doOnNext(result -> view.changeAllStoresButtonVisibility(hasResults(result)))
            .doOnNext(result -> view.addAllStoresResult(result)));
  }

  private void search() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.showLoading())
        .map(__ -> view.getViewModel())
        .doOnNext(viewModel -> analytics.search(viewModel.getCurrentQuery()))
        .flatMap(viewModel -> executeSearchRequests(viewModel.getCurrentQuery(),
            viewModel.getStoreName(), viewModel.isOnlyTrustedApps()).onErrorResumeNext(err -> {
          crashReport.log(err);
          return Observable.just(null);
        })
            .observeOn(viewScheduler)
            .doOnNext(__2 -> view.hideLoading())
            .doOnNext(result -> {
              if (result == null || !hasResults(result)) {
                view.showNoResultsImage();
                analytics.searchNoResults(viewModel.getCurrentQuery());
              } else {
                view.showResultsLayout();
                if (viewModel.isAllStoresSelected()) {
                  view.showAllStoresResult();
                } else {
                  view.showFollowedStoresResult();
                }
              }
            }))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private boolean hasResults(ListSearchApps listSearchApps) {
    DataList<SearchApp> dataList = listSearchApps.getDataList();
    return dataList != null
        && dataList.getList() != null
        && dataList.getList()
        .size() > 0;
  }
}
