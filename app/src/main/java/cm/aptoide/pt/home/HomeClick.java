package cm.aptoide.pt.home;

/**
 * Created by jdandrade on 12/03/2018.
 */

public class HomeClick {
  private final HomeBundle bundle;
  private final int position;
  private final Type clickType;

  public HomeClick(HomeBundle bundle, int position, Type clickType) {
    this.bundle = bundle;
    this.position = position;
    this.clickType = clickType;
  }

  public HomeBundle getBundle() {
    return bundle;
  }

  public int getPosition() {
    return position;
  }

  public Type getClickType() {
    return clickType;
  }

  public enum Type {
    MORE, APP, AD
  }
}
