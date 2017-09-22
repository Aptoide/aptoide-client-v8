package cm.aptoide.pt.view.search;

import android.content.SharedPreferences;
import android.os.Bundle;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.DataList;
import cm.aptoide.pt.dataprovider.model.v7.ListSearchApps;
import cm.aptoide.pt.dataprovider.util.HashMapNotNull;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.ListSearchAppsRequest;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.search.SearchAnalytics;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;

public class SearchPresenter implements Presenter {
  private final SearchView view;
  private final SharedPreferences sharedPreferences;
  private final TokenInvalidator tokenInvalidator;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final SearchAnalytics searchAnalytics;
  private final HashMapNotNull<String, List<String>> subscribedStores;
  private final CrashReport crashReport;
  private final Scheduler ioScheduler;
  private final Scheduler mainThreadScheduler;

  public SearchPresenter(SearchView view, SharedPreferences sharedPreferences,
      TokenInvalidator tokenInvalidator, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory, SearchAnalytics searchAnalytics,
      HashMapNotNull<String, List<String>> subscribedStores, CrashReport crashReport,
      Scheduler ioScheduler, Scheduler mainThreadScheduler) {
    this.view = view;
    this.sharedPreferences = sharedPreferences;
    this.tokenInvalidator = tokenInvalidator;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.searchAnalytics = searchAnalytics;
    this.subscribedStores = subscribedStores;
    this.crashReport = crashReport;
    this.ioScheduler = ioScheduler;
    this.mainThreadScheduler = mainThreadScheduler;
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
        //.doOnNext(__ -> adapter.showResultsForSearchEverywhere())
        .doOnNext(__ -> view.setEverywhereSearchButtonHighlighted())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void search(String query, String storeName, boolean onlyTrustedApps) {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(mainThreadScheduler)
        .doOnNext(__ -> view.showLoading())
        .observeOn(ioScheduler)
        .doOnNext(__ -> searchAnalytics.search(query))
        .flatMap(__ -> executeSearchRequests(query, storeName, onlyTrustedApps))
        .observeOn(mainThreadScheduler)
        .doOnNext(listSearchApps -> {
          List<ListSearchApps.SearchAppsApp> list = listSearchApps.getDataList()
              .getList();
          if (list != null && hasMoreResults(listSearchApps)) {
            hasSubscribedResults = true;
            handleFinishLoading();
          } else {
            hasSubscribedResults = false;
            handleFinishLoading();
          }
        }, e -> finishLoading())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private Observable<ListSearchApps> executeSearchRequests(String query, String storeName,
      boolean onlyTrustedApps) {

    if (storeName != null) {
      return searchInStore(query, storeName);
    }

    return Observable.merge(searchInSubscribedStores(query, onlyTrustedApps),
        searchInNonSubscribedStores(query, onlyTrustedApps));
  }

  private Observable<ListSearchApps> searchInNonSubscribedStores(String query, boolean onlyTrustedApps) {
    return ListSearchAppsRequest.of(query, false, onlyTrustedApps, subscribedStores, bodyInterceptor,
        httpClient, converterFactory, tokenInvalidator, sharedPreferences)
        .observe();
  }

  private Observable<ListSearchApps> searchInSubscribedStores(String query, boolean onlyTrustedApps) {
    return ListSearchAppsRequest.of(query, true, onlyTrustedApps, subscribedStores, bodyInterceptor,
        httpClient, converterFactory, tokenInvalidator, sharedPreferences)
        .observe();
  }

  private Observable<ListSearchApps> searchInStore(String query, String storeName) {
    return ListSearchAppsRequest.of(query, storeName, subscribedStores, bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences)
        .observe();
  }

  private boolean hasMoreResults(ListSearchApps listSearchApps) {
    DataList<ListSearchApps.SearchAppsApp> dataList = listSearchApps.getDataList();
    return dataList.getList()
        .size() > 0 || listSearchApps.getTotal() > listSearchApps.getNextSize();
  }
}
