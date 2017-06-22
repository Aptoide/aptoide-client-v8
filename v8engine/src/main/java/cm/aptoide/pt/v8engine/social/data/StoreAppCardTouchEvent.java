package cm.aptoide.pt.v8engine.social.data;

/**
 * Created by jdandrade on 22/06/2017.
 */

public class StoreAppCardTouchEvent extends CardTouchEvent {
  private final String packageName;

  public StoreAppCardTouchEvent(Card card, Type actionType, String packageName) {
    super(card, actionType);
    this.packageName = packageName;
  }

  public String getPackageName() {
    return packageName;
  }
}
