package cm.aptoide.pt.home.bundles.base;

/**
 * Created by jdandrade on 12/03/2018.
 */

public class HomeEvent {
  private final HomeBundle bundle;
  private final int bundlePosition;
  private final Type clickType;

  public HomeEvent(HomeBundle bundle, int bundlePosition, Type clickType) {
    this.bundle = bundle;
    this.bundlePosition = bundlePosition;
    this.clickType = clickType;
  }

  public HomeBundle getBundle() {
    return bundle;
  }

  public int getBundlePosition() {
    return bundlePosition;
  }

  public Type getType() {
    return clickType;
  }

  public enum Type {
    MORE, MORE_TOP, APP, AD, SCROLL_RIGHT, REWARD_APP, APPC_KNOW_MORE, DISMISS_BUNDLE, SOCIAL_INSTALL, EDITORIAL, INSTALL_WALLET, NO_OP, REACT_SINGLE_PRESS, REACT_LONG_PRESS, REACTION, POPUP_DISMISS, LOAD_MORE_RETRY, INSTALL_PROMOTIONAL, ARTICLE_PROMOTIONAL, ESKILLS, ESKILLS_APP, ESKILLS_MORE, NOTIFY_ME, CANCEL_NOTIFY_ME
  }
}
