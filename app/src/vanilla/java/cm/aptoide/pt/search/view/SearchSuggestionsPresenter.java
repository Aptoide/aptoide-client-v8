package cm.aptoide.pt.search.view;

import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.search.SearchCursorAdapter;
import cm.aptoide.pt.search.SearchNavigator;
import cm.aptoide.pt.search.SearchSuggestionManager;
import rx.Scheduler;
import rx.Single;

public class SearchSuggestionsPresenter implements Presenter {

  private static final String TAG = SearchSuggestionsPresenter.class.getName();

  private static final int COMPLETION_THRESHOLD = 3;

  private final SearchSuggestionsView view;
  private final SearchSuggestionManager searchSuggestionManager;
  private final Scheduler viewScheduler;
  private final SearchCursorAdapter searchCursorAdapter;
  private final CrashReport crashReport;
  private final TrendingManager trendingManager;
  private final SearchNavigator navigator;

  public SearchSuggestionsPresenter(SearchSuggestionsView view,
      SearchSuggestionManager searchSuggestionManager, Scheduler viewScheduler,
      SearchCursorAdapter searchCursorAdapter, CrashReport crashReport,
      TrendingManager trendingManager, SearchNavigator navigator) {
    this.view = view;
    this.searchSuggestionManager = searchSuggestionManager;
    this.viewScheduler = viewScheduler;
    this.searchCursorAdapter = searchCursorAdapter;
    this.crashReport = crashReport;
    this.trendingManager = trendingManager;
    this.navigator = navigator;
  }

  @Override public void present() {
    handleReceivedSuggestions();
    handleQueryTextChanged();
    showSuggestionsIfCurrentQueryIsEmpty();
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

  private void handleQueryTextChanged() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.onQueryTextChanged())
        .filter(data -> data != null
            && data.queryText()
            .length() > 0)
        .flatMapSingle(data -> {
          final String query = data.queryText()
              .toString();
          if (query.length() < COMPLETION_THRESHOLD) {
            return trendingManager.getTrendingSuggestions()
                .observeOn(viewScheduler)
                .doOnSuccess(trendingList -> view.setTrending(trendingList));
          }
          return Single.fromCallable(() -> {
            final String query1 = data.queryText()
                .toString();

            if (data.isSubmitted()) {
              view.collapseSearchBar();
              navigator.navigate(query1);
              Logger.v(TAG, "Searching for: " + query1);
              return null;
            }

            if (query1.length() >= COMPLETION_THRESHOLD) {
              searchSuggestionManager.getSuggestionsFor(query1);
            }
            return null;
          });
        })
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
              || data.queryText()
              .length() == 0) && (currentQuery == null || currentQuery.isEmpty());
        })
        .flatMapSingle(__ -> trendingManager.getTrendingSuggestions())
        .filter(data -> data != null && data.size() > 0)
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.focusInSearchBar())
        .doOnNext(data -> view.setTrending(data))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }
}
