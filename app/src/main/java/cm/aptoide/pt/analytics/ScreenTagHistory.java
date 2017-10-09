package cm.aptoide.pt.analytics;

import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;

/**
 * Created by pedroribeiro on 29/09/17.
 */

public class ScreenTagHistory {

  private String fragment;
  private String tag;
  private String store;

  private ScreenTagHistory(String fragment, String tag, String store) {
    this.fragment = fragment;
    this.tag = tag;
    this.store = store;
  }

  public String getStore() {
    return store;
  }

  public String getFragment() {
    return fragment;
  }

  public void setFragment(String fragment) {
    this.fragment = fragment;
  }

  public String getTag() {
    return tag;
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
        store = "aptoide_main";
      } else {
        store = storeContext.name();
      }
      return new ScreenTagHistory(fragment, tag, store);
    }
  }
}
