package cm.aptoide.pt.search.model;

import cm.aptoide.aptoideviews.filters.Filter;
import cm.aptoide.pt.search.view.SearchResultView;
import java.util.LinkedList;
import java.util.List;
import org.parceler.Parcel;

@SuppressWarnings("WeakerAccess") @Parcel public class SearchViewModel
    implements SearchResultView.Model {

  SearchQueryModel searchQueryModel;
  String storeName;
  boolean onlyTrustedApps;
  int allStoresOffset = 0;
  boolean reachedBottomAllStores = false;
  boolean loadedAds = false;

  List<SearchAppResult> allStoresSearchAppResults;
  List<SearchAdResult> allStoresSearchAdResults;

  private String storeTheme;

  List<Filter> filters;

  public SearchViewModel() {
    this.allStoresSearchAppResults = new LinkedList<>();
    this.allStoresSearchAdResults = new LinkedList<>();
  }

  public SearchViewModel(SearchQueryModel searchQueryModel, String storeName,
      boolean onlyTrustedApps, List<SearchAppResult> allStoresSearchAppResults,
      List<SearchAdResult> allStoresSearchAdResults, String storeTheme) {
    this.searchQueryModel = searchQueryModel;
    this.storeName = storeName;
    this.onlyTrustedApps = onlyTrustedApps;
    this.allStoresSearchAppResults = allStoresSearchAppResults;
    this.allStoresSearchAdResults = allStoresSearchAdResults;
    this.storeTheme = storeTheme;
  }

  public SearchViewModel(SearchQueryModel searchQueryModel, boolean onlyTrustedApps) {
    this(searchQueryModel, null, onlyTrustedApps, new LinkedList<>(), new LinkedList<>(), "");
  }

  public SearchViewModel(SearchQueryModel searchQueryModel, String storeName, String storeTheme) {
    this(searchQueryModel, storeName, true, new LinkedList<>(), new LinkedList<>(), storeTheme);
  }

  @Override public SearchQueryModel getSearchQueryModel() {
    return searchQueryModel;
  }

  @Override public String getStoreName() {
    return storeName;
  }

  @Override public String getStoreTheme() {
    return storeTheme;
  }

  @Override public boolean isOnlyTrustedApps() {
    return onlyTrustedApps;
  }

  public int getAllStoresOffset() {
    return allStoresOffset;
  }

  public boolean hasReachedBottomOfAllStores() {
    return reachedBottomAllStores;
  }

  public void incrementOffsetAndCheckIfReachedBottomOfAllStores(int offset) {
    this.allStoresOffset += offset;
    if (offset == 0) {
      reachedBottomAllStores = true;
    }
  }

  @Override public boolean hasLoadedAds() {
    return loadedAds;
  }

  @Override public void setHasLoadedAds() {
    loadedAds = true;
  }

  @Override public List<SearchAppResult> getAllStoresSearchAppResults() {
    return allStoresSearchAppResults;
  }

  @Override public List<SearchAdResult> getAllStoresSearchAdResults() {
    return allStoresSearchAdResults;
  }

  @Override public boolean hasData() {
    return (allStoresSearchAppResults != null && allStoresSearchAppResults.size() > 0);
  }

  public void clearListData() {
    this.allStoresSearchAdResults.clear();
    this.allStoresSearchAppResults.clear();
  }

  public void addAllStoresSearchAdResults(List<SearchAdResult> allStoresSearchAdResults) {
    this.allStoresSearchAdResults.addAll(allStoresSearchAdResults);
  }

  public void addAllStoresSearchAppResults(List<SearchAppResult> allStoresSearchAppResults) {
    this.allStoresSearchAppResults = allStoresSearchAppResults;
  }

  public List<Filter> getFilters() {
    return filters;
  }

  public void setFilters(List<Filter> filters) {
    this.filters = filters;
  }
}
