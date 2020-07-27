package cm.aptoide.pt.search.model;

import cm.aptoide.aptoideviews.filters.Filter;
import cm.aptoide.pt.search.view.SearchResultView;
import java.util.List;
import org.parceler.Parcel;

@SuppressWarnings("WeakerAccess") @Parcel public class SearchViewModel
    implements SearchResultView.Model {

  SearchQueryModel searchQueryModel;
  String storeName;
  boolean onlyTrustedApps;
  boolean loadedAds = false;

  private String storeTheme;

  List<Filter> filters;

  public SearchViewModel() {
  }

  public SearchViewModel(SearchQueryModel searchQueryModel, String storeName,
      boolean onlyTrustedApps, String storeTheme) {
    this.searchQueryModel = searchQueryModel;
    this.storeName = storeName;
    this.onlyTrustedApps = onlyTrustedApps;
    this.storeTheme = storeTheme;
  }

  public SearchViewModel(SearchQueryModel searchQueryModel, boolean onlyTrustedApps) {
    this(searchQueryModel, null, onlyTrustedApps, "");
  }

  public SearchViewModel(SearchQueryModel searchQueryModel, String storeName, String storeTheme) {
    this(searchQueryModel, storeName, true, storeTheme);
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

  @Override public boolean hasLoadedAds() {
    return loadedAds;
  }

  @Override public void setHasLoadedAds() {
    loadedAds = true;
  }

  public List<Filter> getFilters() {
    return filters;
  }

  public void setFilters(List<Filter> filters) {
    this.filters = filters;
  }
}
