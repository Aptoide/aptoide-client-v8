package cm.aptoide.pt.search.view;

import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.search.model.Suggestion;
import cm.aptoide.pt.search.suggestions.SearchQueryEvent;
import java.util.List;
import rx.Observable;

public interface SearchSuggestionsView extends View {
  Observable<SearchQueryEvent> onQueryTextChanged();

  void collapseSearchBar(boolean shouldShowSuggestions);

  String getCurrentQuery();

  void focusInSearchBar();

  void setTrendingList(List<Suggestion> trendingList);

  void setSuggestionsList(List<String> suggestions);

  void setTrendingCursor(List<String> trendingCursor);


}
