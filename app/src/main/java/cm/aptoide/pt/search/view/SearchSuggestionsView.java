package cm.aptoide.pt.search.view;

import cm.aptoide.pt.presenter.View;
import com.jakewharton.rxbinding.support.v7.widget.SearchViewQueryTextEvent;
import java.util.List;
import rx.Observable;

interface SearchSuggestionsView extends View {
  Observable<SearchViewQueryTextEvent> onQueryTextChanged();

  void collapseSearchBar();

  String getCurrentQuery();

  void focusInSearchBar();

  void setTrending(List<String> trending);
}
