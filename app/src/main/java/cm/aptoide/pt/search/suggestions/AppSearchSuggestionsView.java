package cm.aptoide.pt.search.suggestions;

import android.support.annotation.NonNull;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.search.SearchCursorAdapter;
import cm.aptoide.pt.search.analytics.SearchAnalytics;
import cm.aptoide.pt.search.analytics.SearchSource;
import cm.aptoide.pt.search.view.SearchSuggestionsView;
import cm.aptoide.pt.view.fragment.FragmentView;
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.trello.rxlifecycle.LifecycleTransformer;
import java.util.List;
import rx.Observable;
import rx.subjects.PublishSubject;

public class AppSearchSuggestionsView implements SearchSuggestionsView {

  private static final int COMPLETION_THRESHOLD = 0;

  private final FragmentView parentView;
  private final Observable<Void> toolbarClickObservable;
  private final CrashReport crashReport;
  private final String currentQuery;
  private final SearchCursorAdapter searchCursorAdapter;
  private final PublishSubject<SearchQueryEvent> queryTextChangedPublisher;
  private final SearchAnalytics searchAnalytics;
  private SearchView searchView;
  private MenuItem searchMenuItem;
  private Observable<MenuItem> toolbarMenuItemClick;

  public AppSearchSuggestionsView(FragmentView parentView, Observable<Void> toolbarClickObservable,
      CrashReport crashReport, String currentQuery, SearchCursorAdapter searchCursorAdapter,
      PublishSubject<SearchQueryEvent> queryTextChangedPublisher,
      Observable<MenuItem> toolbarMenuItemClick, SearchAnalytics searchAnalytics) {
    this.parentView = parentView;
    this.toolbarClickObservable = toolbarClickObservable;
    this.crashReport = crashReport;
    this.currentQuery = currentQuery;
    this.searchCursorAdapter = searchCursorAdapter;
    this.queryTextChangedPublisher = queryTextChangedPublisher;
    this.toolbarMenuItemClick = toolbarMenuItemClick;
    this.searchAnalytics = searchAnalytics;
  }

  public void initialize(@NonNull MenuItem searchMenuItem) {
    this.searchMenuItem = searchMenuItem;
    searchView = (SearchView) searchMenuItem.getActionView();
    searchView.setSuggestionsAdapter(searchCursorAdapter);
    searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
      @Override public boolean onSuggestionSelect(int position) {
        return false;
      }

      @Override public boolean onSuggestionClick(int position) {
        queryTextChangedPublisher.onNext(new SearchQueryEvent(
            searchCursorAdapter.getQueryAt(position)
                .toString(), position, true, true));
        return true;
      }
    });

    AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) searchView.findViewById(
        android.support.v7.appcompat.R.id.search_src_text);
    autoCompleteTextView.setThreshold(COMPLETION_THRESHOLD);

    getLifecycle().filter(event -> event == LifecycleEvent.RESUME)
        .flatMap(__ -> RxSearchView.queryTextChangeEvents(searchView))
        .doOnNext(event -> queryTextChangedPublisher.onNext(new SearchQueryEvent(event.queryText()
            .toString(), event.isSubmitted())))
        .compose(bindUntilEvent(LifecycleEvent.PAUSE))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));

    toolbarClickExpandsSearch();
    searchMenuItemClickExpandsSearch();
  }

  private void toolbarClickExpandsSearch() {
    getLifecycle().filter(event -> event == LifecycleEvent.RESUME)
        .flatMap(__ -> toolbarClickObservable)
        .doOnNext(__ -> searchAnalytics.searchStart(SearchSource.SEARCH_TOOLBAR))
        .doOnNext(__ -> focusInSearchBar())
        .compose(bindUntilEvent(LifecycleEvent.PAUSE))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void searchMenuItemClickExpandsSearch() {
    getLifecycle().filter(event -> event == LifecycleEvent.RESUME)
        .flatMap(__ -> toolbarMenuItemClick)
        .filter(item -> item.getItemId() == searchMenuItem.getItemId())
        .doOnNext(__ -> searchAnalytics.searchStart(SearchSource.SEARCH_ICON))
        .doOnNext(__ -> focusInSearchBar())
        .compose(bindUntilEvent(LifecycleEvent.PAUSE))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  @Override public Observable<SearchQueryEvent> onQueryTextChanged() {
    return queryTextChangedPublisher;
  }

  @Override public void collapseSearchBar() {
    if (searchMenuItem != null) searchMenuItem.collapseActionView();
  }

  @Override public String getCurrentQuery() {
    return currentQuery != null ? currentQuery : "";
  }

  @Override public void focusInSearchBar() {
    if (searchMenuItem != null) {
      searchMenuItem.expandActionView();
    }

    if (searchView != null && !getCurrentQuery().isEmpty()) {
      final String currentQuery = getCurrentQuery();
      searchView.setQuery(currentQuery, false);
    }
  }

  @Override public void setTrending(List<String> trending) {
    searchCursorAdapter.setData(trending);
  }

  @NonNull @Override
  public <T> LifecycleTransformer<T> bindUntilEvent(@NonNull LifecycleEvent lifecycleEvent) {
    return parentView.bindUntilEvent(lifecycleEvent);
  }

  @Override public Observable<LifecycleEvent> getLifecycle() {
    return parentView.getLifecycle();
  }

  @Override public void attachPresenter(Presenter presenter) {
    parentView.attachPresenter(presenter);
  }
}
