package cm.aptoide.pt.search.view;

import android.support.annotation.NonNull;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.search.analytics.SearchAnalytics;
import cm.aptoide.pt.search.SearchCursorAdapter;
import cm.aptoide.pt.search.SearchNavigator;
import cm.aptoide.pt.search.suggestions.SearchQueryEvent;
import cm.aptoide.pt.search.suggestions.SearchSuggestionManager;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Scheduler;

@SuppressWarnings("Convert2MethodRef") public class SearchSuggestionsPresenter
    implements Presenter {

  private final SearchSuggestionsView view;
  private final SearchSuggestionManager searchSuggestionManager;
  private final Scheduler viewScheduler;
  private final SearchCursorAdapter searchCursorAdapter;
  private final CrashReport crashReport;
  private final TrendingManager trendingManager;
  private final SearchNavigator navigator;
  private boolean showSuggestionsOnFirstLoadWithEmptyQuery;
  private final SearchAnalytics searchAnalytics;

  public SearchSuggestionsPresenter(SearchSuggestionsView view,
      SearchSuggestionManager searchSuggestionManager, Scheduler viewScheduler,
      SearchCursorAdapter searchCursorAdapter, CrashReport crashReport,
      TrendingManager trendingManager, SearchNavigator navigator,
      boolean showSuggestionsOnFirstLoadWithEmptyQuery, SearchAnalytics searchAnalytics) {
    this.view = view;
    this.searchSuggestionManager = searchSuggestionManager;
    this.viewScheduler = viewScheduler;
    this.searchCursorAdapter = searchCursorAdapter;
    this.crashReport = crashReport;
    this.trendingManager = trendingManager;
    this.navigator = navigator;
    this.showSuggestionsOnFirstLoadWithEmptyQuery = showSuggestionsOnFirstLoadWithEmptyQuery;
    this.searchAnalytics = searchAnalytics;
  }

  @Override public void present() {
    handleReceivedSuggestions();
    handleQueryTextSubmitted();
    handleQueryTextCleaned();
    handleQueryTextChanged();
    if (showSuggestionsOnFirstLoadWithEmptyQuery) {
      showSuggestionsIfCurrentQueryIsEmpty();
    }
  }

  private void handleReceivedSuggestions() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> searchSuggestionManager.listenForSuggestions())
        .observeOn(viewScheduler)
        .doOnNext(data -> searchCursorAdapter.setData(data))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void handleQueryTextSubmitted() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> getDebouncedQueryChanges())
        .filter(data -> data.hasQuery() && data.isSubmitted())
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.collapseSearchBar())
        .doOnNext(data -> navigator.navigate(data.getQuery()))
        .doOnNext(data -> {
          if (data.isSuggestion()) {
            searchAnalytics.searchFromSuggestion(data.getQuery(), data.getPosition());
          }else{
            searchAnalytics.search(data.getQuery());
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  @NonNull private Observable<SearchQueryEvent> getDebouncedQueryChanges() {
    return view.onQueryTextChanged()
        .debounce(250, TimeUnit.MILLISECONDS);
  }

  private void handleQueryTextCleaned() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> getDebouncedQueryChanges())
        .filter(data -> !data.hasQuery())
        .flatMapSingle(data -> trendingManager.getTrendingSuggestions()
            .observeOn(viewScheduler)
            .doOnSuccess(trendingList -> view.setTrending(trendingList)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void handleQueryTextChanged() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> getDebouncedQueryChanges())
        .filter(data -> data.hasQuery() && !data.isSubmitted())
        .doOnNext(data -> searchSuggestionManager.getSuggestionsFor(data.getQuery()
            .toString()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void showSuggestionsIfCurrentQueryIsEmpty() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMapSingle(__ -> view.onQueryTextChanged()
            .first()
            .toSingle())
        .filter(data -> {
          final String currentQuery = view.getCurrentQuery();
          return (data == null
              || data.getQuery()
              .length() == 0) && (currentQuery == null || currentQuery.isEmpty());
        })
        .flatMapSingle(__ -> trendingManager.getTrendingSuggestions())
        .filter(data -> data != null && data.size() > 0)
        .observeOn(viewScheduler)
        .doOnNext(data -> view.setTrending(data))
        .doOnNext(__ -> view.focusInSearchBar())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }
}
