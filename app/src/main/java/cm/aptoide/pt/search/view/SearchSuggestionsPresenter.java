package cm.aptoide.pt.search.view;

import android.support.annotation.NonNull;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.search.SearchNavigator;
import cm.aptoide.pt.search.SuggestionCursorAdapter;
import cm.aptoide.pt.search.analytics.SearchAnalytics;
import cm.aptoide.pt.search.suggestions.SearchQueryEvent;
import cm.aptoide.pt.search.suggestions.SearchSuggestionManager;
import cm.aptoide.pt.search.suggestions.TrendingManager;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import rx.Observable;
import rx.Scheduler;
import rx.Single;

@SuppressWarnings("Convert2MethodRef") public class SearchSuggestionsPresenter
    implements Presenter {

  private static final String TAG = SearchSuggestionsPresenter.class.getName();

  private final SearchSuggestionsView view;
  private final SearchSuggestionManager searchSuggestionManager;
  private final Scheduler viewScheduler;
  private final SuggestionCursorAdapter suggestionCursorAdapter;
  private final CrashReport crashReport;
  private final TrendingManager trendingManager;
  private final SearchNavigator navigator;
  private final SearchAnalytics searchAnalytics;
  private boolean showSuggestionsOnFirstLoadWithEmptyQuery;

  public SearchSuggestionsPresenter(SearchSuggestionsView view,
      SearchSuggestionManager searchSuggestionManager, Scheduler viewScheduler,
      SuggestionCursorAdapter suggestionCursorAdapter, CrashReport crashReport,
      TrendingManager trendingManager, SearchNavigator navigator,
      boolean showSuggestionsOnFirstLoadWithEmptyQuery, SearchAnalytics searchAnalytics) {
    this.view = view;
    this.searchSuggestionManager = searchSuggestionManager;
    this.viewScheduler = viewScheduler;
    this.suggestionCursorAdapter = suggestionCursorAdapter;
    this.crashReport = crashReport;
    this.trendingManager = trendingManager;
    this.navigator = navigator;
    this.showSuggestionsOnFirstLoadWithEmptyQuery = showSuggestionsOnFirstLoadWithEmptyQuery;
    this.searchAnalytics = searchAnalytics;
  }

  @Override public void present() {
    handleQueryTextSubmitted();
    handleQueryTextCleaned();
    handleQueryTextChanged();
    if (showSuggestionsOnFirstLoadWithEmptyQuery) {
      showSuggestionsIfCurrentQueryIsEmpty();
    }
  }

  private void handleQueryTextSubmitted() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(
            __ -> getDebouncedQueryChanges().filter(data -> data.hasQuery() && data.isSubmitted()))
        .observeOn(viewScheduler)
        .doOnNext(data -> {
          view.collapseSearchBar(true);
          navigator.navigate(data.getQuery());
          if (data.isSuggestion()) {
            searchAnalytics.searchFromSuggestion(data.getQuery(), data.getPosition());
          } else {
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
        .flatMap(__ -> getDebouncedQueryChanges().filter(data -> !data.hasQuery())
            .flatMapSingle(data -> trendingManager.getTrendingCursorSuggestions()
                .observeOn(viewScheduler)
                .doOnSuccess(trendingList -> view.setTrendingCursor(trendingList)))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void handleQueryTextChanged() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(
            __ -> getDebouncedQueryChanges().filter(data -> data.hasQuery() && !data.isSubmitted())
                .map(data -> data.getQuery()
                    .toString())
                .flatMapSingle(query -> searchSuggestionManager.getSuggestionsForApp(query)
                    .onErrorResumeNext(err -> {
                      if (err instanceof TimeoutException) {
                        Logger.i(TAG, "Timeout reached while waiting for application suggestions");
                        return Single.just(suggestionCursorAdapter.getSuggestions());
                      }
                      return Single.error(err);
                    })
                    .observeOn(viewScheduler)
                    .doOnSuccess(queryResults -> suggestionCursorAdapter.setData(queryResults)))
                .retry())
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
        .flatMapSingle(__ -> trendingManager.getTrendingCursorSuggestions())
        .filter(data -> data != null && data.size() > 0)
        .observeOn(viewScheduler)
        .doOnNext(data -> view.setTrendingCursor(data))
        .doOnNext(__ -> view.focusInSearchBar())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }
}
