package cm.aptoide.pt.search.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Pair;
import cm.aptoide.pt.R;
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
import java.util.concurrent.TimeUnit;
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
  private final PublishRelay<Pair<SearchApp, android.view.View>> onOpenPopupMenuClickRelay;
  private int offset = 0;
  private boolean reachedBottom = false;

  public SearchPresenter(SearchView view, SearchAnalytics analytics, SearchNavigator navigator,
      CrashReport crashReport, Scheduler viewScheduler, SearchManager searchManager,
      PublishRelay<MinimalAd> onAdClickRelay, PublishRelay<SearchApp> onItemViewClickRelay,
      PublishRelay<Pair<SearchApp, android.view.View>> onOpenPopupMenuClickRelay) {
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
    firstDataLoad();
    loadAds();
    handleClickFollowedStoresSearchButton();
    handleClickEverywhereSearchButton();
    handleClickToOpenAppViewFromItem();
    handleClickToOpenAppViewFromAdd();
    handleClickToOpenPopupMenu();
    handleClickOnNoResultsImage();
    handleAnyListReachedBottom();
  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
  }

  private void handleAnyListReachedBottom() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> Observable.merge(view.allStoresResultReachedBottom(),
            view.followedStoresResultReachedBottom()))
        .debounce(250, TimeUnit.MILLISECONDS)
        .map(__ -> view.getViewModel())
        .filter(viewModel -> !hasReachedBottom())
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.showLoadingMore())
        .flatMap(viewModel -> loadData(viewModel.getCurrentQuery(), viewModel.getStoreName(),
            viewModel.isOnlyTrustedApps(), getOffset()).onErrorResumeNext(err -> {
          crashReport.log(err);
          return Observable.just(null);
        }))
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.hideLoadingMore())
        .filter(data -> data != null)
        .doOnNext(data -> incrementOffsetAndCheckIfReachedBottom(getItemCount(data)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void loadAds() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> searchManager.getAdsForQuery(view.getViewModel()
            .getCurrentQuery()))
        .observeOn(viewScheduler)
        .doOnNext(ad -> {
          if (ad == null) {
            view.setFollowedStoresAdsEmpty();
            view.setAllStoresAdsEmpty();
          } else {
            view.setAllStoresAdsResult(ad);
            view.setFollowedStoresAdsResult(ad);
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickToOpenAppViewFromItem() {
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
        .flatMap(pair -> {
          final SearchApp data = pair.first;
          final boolean hasVersions = data.hasVersions();
          final String appName = data.getName();
          final String appIcon = data.getIcon();
          final String packageName = data.getPackageName();
          final String storeName = data.getStore()
              .getName();

          //FIXME which theme should be used?
          // final String theme = view.getDefaultTheme()
          final String theme = data.getStore()
              .getAppearance()
              .getTheme();

          return view.showPopup(hasVersions, pair.second)
              .doOnNext(optionId -> {
                if (optionId == R.id.versions) {
                  navigator.goToOtherVersions(appName, appIcon, packageName);
                } else if (optionId == R.id.go_to_store) {
                  navigator.goToStoreFragment(storeName, theme);
                }
              });
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

  private void handleClickFollowedStoresSearchButton() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> view.clickFollowedStoresSearchButton())
        .doOnNext(__ -> view.showFollowedStoresResult())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickEverywhereSearchButton() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> view.clickEverywhereSearchButton())
        .doOnNext(__ -> view.showAllStoresResult())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private Observable<ListSearchApps> loadData(String query, String storeName,
      boolean onlyTrustedApps, int offset) {
    if (storeName != null && !storeName.trim()
        .equals("")) {
      return loadDataForSpecificStore(query, storeName, offset).doOnNext(
          __ -> view.setLayoutWithoutTabs());
    }
    // search every store. followed and not followed
    return Observable.merge(loadDataForAllFollowedStores(query, onlyTrustedApps, offset),
        loadDataForAllNonFollowedStores(query, onlyTrustedApps, offset));
  }

  @NonNull private Observable<ListSearchApps> loadDataForAllNonFollowedStores(String query,
      boolean onlyTrustedApps, int offset) {
    return searchManager.searchInNonFollowedStores(query, onlyTrustedApps, offset)
        .observeOn(viewScheduler)
        .doOnNext(data -> view.addAllStoresResult(data.getDataList()
            .getList()));
  }

  @NonNull private Observable<ListSearchApps> loadDataForAllFollowedStores(String query,
      boolean onlyTrustedApps, int offset) {
    return searchManager.searchInFollowedStores(query, onlyTrustedApps, offset)
        .observeOn(viewScheduler)
        .doOnNext(data -> view.addFollowedStoresResult(data.getDataList()
            .getList()));
  }

  @NonNull
  private Observable<ListSearchApps> loadDataForSpecificStore(String query, String storeName,
      int offset) {
    return searchManager.searchInStore(query, storeName, offset)
        .observeOn(viewScheduler)
        .doOnNext(data -> view.addFollowedStoresResult(data.getDataList()
            .getList()));
  }

  private void firstDataLoad() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .map(__ -> view.getViewModel())
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.showLoading())
        .doOnNext(viewModel -> analytics.search(viewModel.getCurrentQuery()))
        .flatMap(viewModel -> loadData(viewModel.getCurrentQuery(), viewModel.getStoreName(),
            viewModel.isOnlyTrustedApps(), getOffset()).onErrorResumeNext(err -> {
          crashReport.log(err);
          return Observable.just(null);
        })
            .observeOn(viewScheduler)
            .doOnNext(__2 -> view.hideLoading())
            .doOnNext(data -> {
              if (data == null || getItemCount(data) == 0) {
                view.showNoResultsImage();
                analytics.searchNoResults(viewModel.getCurrentQuery());
              } else {
                view.showResultsLayout();
                incrementOffsetAndCheckIfReachedBottom(getItemCount(data));
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

  private int getItemCount(ListSearchApps listSearchApps) {
    DataList<SearchApp> dataList = listSearchApps.getDataList();
    return (dataList != null && dataList.getList() != null) ? dataList.getList()
        .size() : 0;
  }

  private int getOffset() {
    return offset;
  }

  private boolean hasReachedBottom() {
    return reachedBottom;
  }

  private void incrementOffsetAndCheckIfReachedBottom(int offset) {
    this.offset += offset;
    if (offset == 0) {
      reachedBottom = true;
    }
  }
}
