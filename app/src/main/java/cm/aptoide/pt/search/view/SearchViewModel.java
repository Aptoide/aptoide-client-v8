package cm.aptoide.pt.search.view;

import org.parceler.Parcel;

@Parcel class SearchViewModel implements SearchView.Model {
  private String currentQuery;
  private String storeName;
  private boolean onlyTrustedApps;
  private boolean allStoresSelected;

  SearchViewModel() {
  }

  private SearchViewModel(String currentQuery, String storeName, boolean onlyTrustedApps,
      boolean allStoresSelected) {
    this.currentQuery = currentQuery;
    this.storeName = storeName;
    this.onlyTrustedApps = onlyTrustedApps;
    this.allStoresSelected = allStoresSelected;
  }

  SearchViewModel(String currentQuery, String storeName, boolean onlyTrustedApps) {
    this(currentQuery, storeName, onlyTrustedApps, true);
  }

  SearchViewModel(String currentQuery, boolean onlyTrustedApps) {
    this(currentQuery, null, onlyTrustedApps, true);
  }

  SearchViewModel(String currentQuery, String storeName) {
    this(currentQuery, storeName, true, true);
  }

  @Override public String getCurrentQuery() {
    return currentQuery;
  }

  @Override public String getStoreName() {
    return storeName;
  }

  @Override public boolean isOnlyTrustedApps() {
    return onlyTrustedApps;
  }

  @Override public boolean isAllStoresSelected() {
    return allStoresSelected;
  }

  public void setAllStoresSelected(boolean allStoresSelected) {
    this.allStoresSelected = allStoresSelected;
  }
}
