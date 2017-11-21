package cm.aptoide.pt.search.view;

import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.search.SearchCursorAdapter;
import cm.aptoide.pt.search.SearchNavigator;
import cm.aptoide.pt.search.SearchSuggestionManager;
import java.util.concurrent.TimeUnit;
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
    handleQueryTextSubmitted();
    handleQueryTextCleaned();
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

  private void handleQueryTextSubmitted() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.onQueryTextChanged()
            .throttleFirst(250, TimeUnit.MILLISECONDS))
        .filter(data -> data != null
            && data.queryText()
            .length() > 0
            && data.isSubmitted())
        .doOnNext(data -> {
          view.collapseSearchBar();
          navigator.navigate(data.queryText()
              .toString());
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void handleQueryTextCleaned() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.onQueryTextChanged()
            .throttleFirst(250, TimeUnit.MILLISECONDS))
        .filter(data -> data != null
            && data.queryText()
            .length() == 0)
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
        .flatMap(__ -> view.onQueryTextChanged()
            .throttleFirst(250, TimeUnit.MILLISECONDS))
        .filter(data -> data != null
            && data.queryText()
            .length() > 0)
        .doOnNext(data -> searchSuggestionManager.getSuggestionsFor(data.queryText()
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
