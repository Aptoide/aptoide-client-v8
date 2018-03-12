package cm.aptoide.pt.home;

/**
 * Created by jdandrade on 12/03/2018.
 */

public class HomeClick {
  private final AppBundle bundle;
  private final Type actionType;

  public HomeClick(AppBundle bundle, Type actionType) {
    this.bundle = bundle;
    this.actionType = actionType;
  }

  public AppBundle getBundle() {
    return bundle;
  }

  public Type getActionType() {
    return actionType;
  }

  public enum Type {
    MORE
  }
}
