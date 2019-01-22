package cm.aptoide.pt.search.model;

import cm.aptoide.pt.search.view.SearchResultView;
import java.util.LinkedList;
import java.util.List;
import org.parceler.Parcel;

@SuppressWarnings("WeakerAccess") @Parcel public class SearchViewModel
    implements SearchResultView.Model {
  String currentQuery;
  String storeName;
  boolean onlyTrustedApps;
  boolean allStoresSelected;
  int allStoresOffset = 0;
  int followedStoresOffset = 0;
  boolean reachedBottomAllStores = false;
  boolean reachedBottomFollowedStores = false;
  boolean loadedAds = false;

  List<SearchAppResult> allStoresSearchAppResults;
  List<SearchAdResult> allStoresSearchAdResults;

  List<SearchAppResult> followedStoresSearchAppResults;
  List<SearchAdResult> followedStoresSearchAdResults;
  private String storeTheme;

  public SearchViewModel() {
    this.allStoresSearchAppResults = new LinkedList<>();
    this.allStoresSearchAdResults = new LinkedList<>();
    this.followedStoresSearchAppResults = new LinkedList<>();
    this.followedStoresSearchAdResults = new LinkedList<>();
  }

  public SearchViewModel(String currentQuery, String storeName, boolean onlyTrustedApps,
      boolean allStoresSelected, List<SearchAppResult> allStoresSearchAppResults,
      List<SearchAdResult> allStoresSearchAdResults,
      List<SearchAppResult> followedStoresSearchAppResults,
      List<SearchAdResult> followedStoresSearchAdResults, String storeTheme) {
    this.currentQuery = currentQuery;
    this.storeName = storeName;
    this.onlyTrustedApps = onlyTrustedApps;
    this.allStoresSelected = allStoresSelected;
    this.allStoresSearchAppResults = allStoresSearchAppResults;
    this.allStoresSearchAdResults = allStoresSearchAdResults;
    this.followedStoresSearchAppResults = followedStoresSearchAppResults;
    this.followedStoresSearchAdResults = followedStoresSearchAdResults;
    this.storeTheme = storeTheme;
  }

  public SearchViewModel(String currentQuery, boolean onlyTrustedApps) {
    this(currentQuery, null, onlyTrustedApps, true, new LinkedList<>(), new LinkedList<>(),
        new LinkedList<>(), new LinkedList<>(), "");
  }

  public SearchViewModel(String currentQuery, String storeName, String storeTheme) {
    this(currentQuery, storeName, true, true, new LinkedList<>(), new LinkedList<>(),
        new LinkedList<>(), new LinkedList<>(), storeTheme);
  }

  @Override public List<SearchAppResult> getFollowedStoresSearchAppResults() {
    return followedStoresSearchAppResults;
  }

  @Override public List<SearchAdResult> getFollowedStoresSearchAdResults() {
    return followedStoresSearchAdResults;
  }

  @Override public String getCurrentQuery() {
    return currentQuery;
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

  @Override public List<SearchAppResult> getAllStoresSearchAppResults() {
    return allStoresSearchAppResults;
  }

  @Override public List<SearchAdResult> getAllStoresSearchAdResults() {
    return allStoresSearchAdResults;
  }

  @Override public boolean hasData() {
    return (allStoresSearchAppResults != null && allStoresSearchAppResults.size() > 0) || (
        followedStoresSearchAppResults != null
            && followedStoresSearchAppResults.size() > 0);
  }

  public void clearListData() {
    this.allStoresSearchAdResults.clear();
    this.allStoresSearchAppResults.clear();
    this.followedStoresSearchAdResults.clear();
    this.followedStoresSearchAppResults.clear();
  }

  public void addAllStoresSearchAdResults(List<SearchAdResult> allStoresSearchAdResults) {
    this.allStoresSearchAdResults.addAll(allStoresSearchAdResults);
  }

  public void addFollowedStoresSearchAppResults(
      List<SearchAppResult> followedStoresSearchAppResults) {
    this.followedStoresSearchAppResults.addAll(followedStoresSearchAppResults);
  }

  public void addFollowedStoresSearchAdResults(List<SearchAdResult> followedStoresSearchAdResults) {
    this.followedStoresSearchAdResults.addAll(followedStoresSearchAdResults);
  }

  public void addAllStoresSearchAppResults(List<SearchAppResult> allStoresSearchAppResults) {
    this.allStoresSearchAppResults.addAll(allStoresSearchAppResults);
  }
}
