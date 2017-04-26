package cm.aptoide.pt.v8engine.abtesting;

/**
 * Created by trinkes on 11/22/16.
 */

public enum SearchTabOptions {
  FOLLOWED_STORES, ALL_STORES;

  public int chooseTab() {
    switch (this) {
      case ALL_STORES:
        return 1;
      case FOLLOWED_STORES:
      default:
        return 0;
    }
  }
}
