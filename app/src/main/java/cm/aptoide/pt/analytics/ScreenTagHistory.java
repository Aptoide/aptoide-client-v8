package cm.aptoide.pt.analytics;

import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;

/**
 * Created by pedroribeiro on 29/09/17.
 */

public class ScreenTagHistory {

  private final static String NO_HISTORY = "NO_HISTORY";

  private String fragment;
  private String tag;
  private String store;

  public ScreenTagHistory() {
    this(NO_HISTORY, NO_HISTORY, NO_HISTORY);
  }

  private ScreenTagHistory(String fragment, String tag, String store) {
    this.fragment = fragment;
    this.tag = tag;
    this.store = store;
  }

  public String getStore() {
    return store != null ? store : "";
  }

  public String getFragment() {
    return fragment != null ? fragment : "";
  }

  public void setFragment(String fragment) {
    this.fragment = fragment;
  }

  public String getTag() {
    return tag != null ? tag : "";
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  @Override public String toString() {
    return "ScreenTagHistory{"
        + "fragment='"
        + fragment
        + '\''
        + ", tag='"
        + tag
        + '\''
        + ", store='"
        + store
        + '\''
        + '}';
  }

  public static class Builder {

    public static final String APTOIDE_MAIN_HISTORY_STORE = "aptoide_main";

    private Builder() {
    }

    public static ScreenTagHistory build(String fragment) {
      return build(fragment, "");
    }

    public static ScreenTagHistory build(String fragment, String tag) {
      return build(fragment, tag, StoreContext.home);
    }

    public static ScreenTagHistory build(String fragment, String tag, StoreContext storeContext) {
      String store;
      if (storeContext == null) {
        store = "";
      } else if (storeContext.equals(StoreContext.home)) {
        store = APTOIDE_MAIN_HISTORY_STORE;
      } else {
        store = storeContext.name();
      }
      return new ScreenTagHistory(fragment, tag, store);
    }
  }
}
