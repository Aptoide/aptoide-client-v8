package cm.aptoide.pt.view.search;

import android.os.Bundle;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.DataList;
import cm.aptoide.pt.dataprovider.model.v7.ListSearchApps;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.search.SearchAnalytics;
import cm.aptoide.pt.search.SearchManager;
import rx.Observable;
import rx.Scheduler;

public class SearchPresenter implements Presenter {
  private final SearchView view;
  private final SearchAnalytics searchAnalytics;
  private final CrashReport crashReport;
  private final Scheduler ioScheduler;
  private final Scheduler mainThreadScheduler;
  private final SearchManager searchManager;
  private final SearchResultAdapter adapter;

  public SearchPresenter(SearchView view, SearchAnalytics searchAnalytics, CrashReport crashReport,
      Scheduler ioScheduler, Scheduler mainThreadScheduler, SearchManager searchManager,
      SearchResultAdapter adapter) {
    this.view = view;
    this.searchAnalytics = searchAnalytics;
    this.crashReport = crashReport;
    this.ioScheduler = ioScheduler;
    this.mainThreadScheduler = mainThreadScheduler;
    this.searchManager = searchManager;
    this.adapter = adapter;
  }

  @Override public void present() {
    search();
    onFollowedStoresSearchButtonClicked();
    onEverywhereSearchButtonClick();
  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
  }

  private void onFollowedStoresSearchButtonClicked() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(mainThreadScheduler)
        .flatMap(__ -> view.clickFollowedStoresSearchButton())
        .doOnNext(__ -> adapter.showResultsForSearchFollowedStores())
        .doOnNext(__ -> view.setSubscribedSearchButtonHighlighted())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void onEverywhereSearchButtonClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(mainThreadScheduler)
        .flatMap(__ -> view.clickEverywhereSearchButton())
        .doOnNext(__ -> adapter.showResultsForSearchEverywhere())
        .doOnNext(__ -> view.setEverywhereSearchButtonHighlighted())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  // TODO: 22/9/2017 sithengineer break this method even more
  private void search() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(mainThreadScheduler)
        .doOnNext(__ -> view.showLoading())
        .observeOn(ioScheduler)
        .map(__ -> view.getViewModel())
        .doOnNext(viewModel -> searchAnalytics.search(viewModel.getCurrentQuery()))
        .flatMap(viewModel -> executeSearchRequests(viewModel.getCurrentQuery(),
            viewModel.getStoreName(), viewModel.isOnlyTrustedApps()).observeOn(mainThreadScheduler)
            .doOnNext(__2 -> view.hideLoading())
            .doOnNext(result -> {
              if (!hasResults(result)) {
                view.showNoResultsImage();
                searchAnalytics.searchNoResults(viewModel.getCurrentQuery());
              }
            })
            .doOnError(err -> crashReport.log(err)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private boolean hasResults(ListSearchApps listSearchApps) {
    DataList<ListSearchApps.SearchAppsApp> dataList = listSearchApps.getDataList();
    return dataList != null
        && dataList.getList() != null
        && dataList.getList()
        .size() > 0;
  }

  // TODO: 22/9/2017 sithengineer break this method even more
  private Observable<ListSearchApps> executeSearchRequests(String query, String storeName,
      boolean onlyTrustedApps) {

    if (storeName != null) {
      return searchManager.searchInStore(query, storeName)
          .doOnNext(result -> adapter.addResultForSearchFollowedStores(result));
    }

    return Observable.merge(searchManager.searchInFollowedStores(query, onlyTrustedApps)
            .doOnNext(result -> adapter.addResultForSearchFollowedStores(result)),
        searchManager.searchInNonSubscribedStores(query, onlyTrustedApps)
            .doOnNext(result -> adapter.addResultForSearchEverywhere(result)));
  }
}
