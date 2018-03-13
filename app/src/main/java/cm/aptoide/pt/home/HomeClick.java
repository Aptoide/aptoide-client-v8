package cm.aptoide.pt.home;

/**
 * Created by jdandrade on 12/03/2018.
 */

public class HomeClick {
  private final HomeBundle bundle;
  private final Type actionType;

  public HomeClick(HomeBundle bundle, Type actionType) {
    this.bundle = bundle;
    this.actionType = actionType;
  }

  public HomeBundle getBundle() {
    return bundle;
  }

  public Type getActionType() {
    return actionType;
  }

  public enum Type {
    MORE
  }
}
