package cm.aptoide.pt.social.data;

/**
 * Created by jdandrade on 22/06/2017.
 */

public class StoreAppCardTouchEvent extends CardTouchEvent {
  private final String packageName;

  public StoreAppCardTouchEvent(Post card, Type actionType, String packageName) {
    super(card, actionType);
    this.packageName = packageName;
  }

  public String getPackageName() {
    return packageName;
  }
}
