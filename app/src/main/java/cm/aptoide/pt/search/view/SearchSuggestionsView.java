package cm.aptoide.pt.search.view;

import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.search.suggestions.SearchQueryEvent;
import com.jakewharton.rxbinding.support.v7.widget.SearchViewQueryTextEvent;
import java.util.List;
import rx.Observable;

public interface SearchSuggestionsView extends View {
  Observable<SearchQueryEvent> onQueryTextChanged();

  void collapseSearchBar();

  String getCurrentQuery();

  void focusInSearchBar();

  void setTrending(List<String> trending);
}
