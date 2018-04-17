package cm.aptoide.pt.home;

/**
 * Created by jdandrade on 12/03/2018.
 */

public class HomeClick {
  private final HomeBundle bundle;
  private final int bundlePosition;
  private final Type clickType;

  public HomeClick(HomeBundle bundle, int bundlePosition, Type clickType) {
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

  public Type getClickType() {
    return clickType;
  }

  public enum Type {
    MORE, APP, AD, SOCIAL_CLICK, SOCIAL_INSTALL
  }
}
