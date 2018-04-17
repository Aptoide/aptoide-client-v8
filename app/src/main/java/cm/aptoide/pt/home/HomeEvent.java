package cm.aptoide.pt.home;

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
    MORE, APP, AD, SOCIAL_CLICK, SCROLL_RIGHT, SOCIAL_INSTALL
  }
}
