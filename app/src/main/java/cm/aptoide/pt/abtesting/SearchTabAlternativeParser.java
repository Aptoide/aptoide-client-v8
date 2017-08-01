/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 06/05/2016.
 */

package cm.aptoide.pt.abtesting;

public class SearchTabAlternativeParser implements AlternativeParser<SearchTabOptions> {

  public static final String FOLLOWED_STORES = "followed_stores";
  public static final String ALL_STORES = "all_stores";

  @Override public SearchTabOptions parse(String string) {
    switch (string) {
      case ALL_STORES:
        return SearchTabOptions.ALL_STORES;
      case FOLLOWED_STORES:
      default:
        return SearchTabOptions.FOLLOWED_STORES;
    }
  }
}
