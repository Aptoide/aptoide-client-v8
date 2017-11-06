package cm.aptoide.pt.search.view;

import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.search.model.SearchAppResult;
import java.util.List;
import org.parceler.Parcel;

@SuppressWarnings("WeakerAccess") @Parcel public class SearchViewModel implements SearchView.Model {
  String currentQuery;
  String storeName;
  boolean onlyTrustedApps;
  boolean allStoresSelected;
  String defaultStoreName;
  int allStoresOffset = 0;
  int followedStoresOffset = 0;
  boolean reachedBottomAllStores = false;
  boolean reachedBottomFollowedStores = false;
  boolean loadedAds = false;

  List<SearchAppResult> allStoresSearchAppResults;
  List<SearchAdResult> allStoresSearchAdResults;

  List<SearchAppResult> followedStoresSearchAppResults;
  List<SearchAdResult> followedStoresSearchAdResults;

  SearchViewModel() {
  }

  SearchViewModel(String currentQuery, String storeName, boolean onlyTrustedApps,
      boolean allStoresSelected, String defaultStoreName) {
    this.currentQuery = currentQuery;
    this.storeName = storeName;
    this.onlyTrustedApps = onlyTrustedApps;
    this.allStoresSelected = allStoresSelected;
    this.defaultStoreName = defaultStoreName;
  }

  SearchViewModel(String currentQuery, String storeName, boolean onlyTrustedApps,
      String defaultStoreName) {
    this(currentQuery, storeName, onlyTrustedApps, true, defaultStoreName);
  }

  SearchViewModel(String currentQuery, boolean onlyTrustedApps, String defaultStoreName) {
    this(currentQuery, null, onlyTrustedApps, true, defaultStoreName);
  }

  SearchViewModel(String currentQuery, String storeName, String defaultStoreName) {
    this(currentQuery, storeName, true, true, defaultStoreName);
  }

  public List<SearchAppResult> getAllStoresSearchAppResults() {
    return allStoresSearchAppResults;
  }

  public void setAllStoresSearchAppResults(List<SearchAppResult> allStoresSearchAppResults) {
    this.allStoresSearchAppResults = allStoresSearchAppResults;
  }

  public List<SearchAdResult> getAllStoresSearchAdResults() {
    return allStoresSearchAdResults;
  }

  public void setAllStoresSearchAdResults(List<SearchAdResult> allStoresSearchAdResults) {
    this.allStoresSearchAdResults = allStoresSearchAdResults;
  }

  public List<SearchAppResult> getFollowedStoresSearchAppResults() {
    return followedStoresSearchAppResults;
  }

  public void setFollowedStoresSearchAppResults(
      List<SearchAppResult> followedStoresSearchAppResults) {
    this.followedStoresSearchAppResults = followedStoresSearchAppResults;
  }

  public List<SearchAdResult> getFollowedStoresSearchAdResults() {
    return followedStoresSearchAdResults;
  }

  public void setFollowedStoresSearchAdResults(List<SearchAdResult> followedStoresSearchAdResults) {
    this.followedStoresSearchAdResults = followedStoresSearchAdResults;
  }

  @Override public String getCurrentQuery() {
    return currentQuery;
  }

  @Override public String getStoreName() {
    return storeName;
  }

  public String getDefaultStoreName() {
    return defaultStoreName;
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

  public int getAllStoresOffset() {
    return allStoresOffset;
  }

  public int getFollowedStoresOffset() {
    return followedStoresOffset;
  }

  public boolean hasReachedBottomOfAllStores() {
    return reachedBottomAllStores;
  }

  public boolean hasReachedBottomOfFollowedStores() {
    return reachedBottomFollowedStores;
  }

  public void incrementOffsetAndCheckIfReachedBottomOfFollowedStores(int offset) {
    this.followedStoresOffset += offset;
    if (offset == 0) {
      reachedBottomFollowedStores = true;
    }
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
}
