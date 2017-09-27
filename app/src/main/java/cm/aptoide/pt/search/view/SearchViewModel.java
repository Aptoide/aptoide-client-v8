package cm.aptoide.pt.search.view;

import org.parceler.Parcel;

@Parcel class SearchViewModel implements SearchView.Model {
  private String currentQuery;
  private String storeName;
  private boolean onlyTrustedApps;
  private boolean allStoresSelected;
  private int offset;
  private boolean reachedBottom;

  SearchViewModel() {
  }

  private SearchViewModel(String currentQuery, String storeName, boolean onlyTrustedApps,
      boolean allStoresSelected, boolean reachedBottom) {
    this.currentQuery = currentQuery;
    this.storeName = storeName;
    this.onlyTrustedApps = onlyTrustedApps;
    this.allStoresSelected = allStoresSelected;
    this.reachedBottom = reachedBottom;
  }

  SearchViewModel(String currentQuery, String storeName, boolean onlyTrustedApps) {
    this(currentQuery, storeName, onlyTrustedApps, true, false);
  }

  SearchViewModel(String currentQuery, boolean onlyTrustedApps) {
    this(currentQuery, null, onlyTrustedApps, true, false);
  }

  SearchViewModel(String currentQuery, String storeName) {
    this(currentQuery, storeName, true, true, false);
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

  @Override public int getOffset() {
    return offset;
  }

  @Override public boolean hasReachedBottom() {
    return reachedBottom;
  }

  public void setAllStoresSelected(boolean allStoresSelected) {
    this.allStoresSelected = allStoresSelected;
  }

  public void incrementOffset(int offset) {
    this.offset += offset;
    if (offset == 0) {
      reachedBottom = true;
    }
  }
}
