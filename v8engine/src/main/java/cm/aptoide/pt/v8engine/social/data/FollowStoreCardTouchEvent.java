package cm.aptoide.pt.v8engine.social.data;

/**
 * Created by jdandrade on 04/07/2017.
 */

public class FollowStoreCardTouchEvent extends CardTouchEvent {
  private final String storeName;

  public FollowStoreCardTouchEvent(Post card, String storeName, Type actionType) {
    super(card, actionType);
    this.storeName = storeName;
  }

  public String getStoreName() {
    return storeName;
  }
}
