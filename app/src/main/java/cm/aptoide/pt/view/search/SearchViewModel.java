package cm.aptoide.pt.view.search;

import org.parceler.Parcel;

@Parcel class SearchViewModel implements SearchView.Model {
  private final String currentQuery;
  private final String storeName;
  private final boolean onlyTrustedApps;
  private boolean allStoresSelected;

  SearchViewModel(String currentQuery, String storeName, boolean onlyTrustedApps) {
    this.currentQuery = currentQuery;
    this.storeName = storeName;
    this.onlyTrustedApps = onlyTrustedApps;
    this.allStoresSelected = false;
  }

  SearchViewModel(String currentQuery, boolean onlyTrustedApps) {
    this.currentQuery = currentQuery;
    this.storeName = "";
    this.onlyTrustedApps = onlyTrustedApps;
    this.allStoresSelected = false;
  }

  SearchViewModel(String currentQuery, String storeName) {
    this.currentQuery = currentQuery;
    this.storeName = storeName;
    this.onlyTrustedApps = true;
    this.allStoresSelected = false;
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
