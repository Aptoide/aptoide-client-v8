package cm.aptoide.pt.view.search;

import org.parceler.Parcel;

@Parcel class SearchViewModel implements SearchView.Model {
  private final String currentQuery;
  private final String storeName;
  private final boolean onlyTrustedApps;
  private int selectedButton;

  SearchViewModel(String currentQuery, String storeName, boolean onlyTrustedApps) {
    this.currentQuery = currentQuery;
    this.storeName = storeName;
    this.onlyTrustedApps = onlyTrustedApps;
    this.selectedButton = -1;
  }

  SearchViewModel(String currentQuery, boolean onlyTrustedApps) {
    this.currentQuery = currentQuery;
    this.storeName = "";
    this.onlyTrustedApps = onlyTrustedApps;
    this.selectedButton = -1;
  }

  SearchViewModel(String currentQuery, String storeName) {
    this.currentQuery = currentQuery;
    this.storeName = storeName;
    this.onlyTrustedApps = true;
    this.selectedButton = -1;
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

  @Override public int getSelectedButton() {
    return selectedButton;
  }

  public void setSelectedButton(int selectedButton) {
    this.selectedButton = selectedButton;
  }
}
