package cm.aptoide.pt.search.model;

import cm.aptoide.pt.search.view.SearchResultView;

public class SearchResultCount {
  private int resultCount;
  private SearchResultView.Model searchResultViewModel;

  public SearchResultCount(int resultCount, SearchResultView.Model searchResultViewModel) {
    this.resultCount = resultCount;
    this.searchResultViewModel = searchResultViewModel;
  }

  public int getResultCount() {
    return resultCount;
  }

  public void setResultCount(int resultCount) {
    this.resultCount = resultCount;
  }

  public SearchResultView.Model getSearchResultViewModel() {
    return searchResultViewModel;
  }

  public void setSearchResultViewModel(SearchResultView.Model searchResultViewModel) {
    this.searchResultViewModel = searchResultViewModel;
  }
}
