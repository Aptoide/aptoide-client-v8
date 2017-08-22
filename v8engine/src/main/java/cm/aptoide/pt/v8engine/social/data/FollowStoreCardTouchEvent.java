package cm.aptoide.pt.v8engine.social.data;

/**
 * Created by jdandrade on 04/07/2017.
 */

public class FollowStoreCardTouchEvent extends CardTouchEvent {
  private final String storeName;
  private final Long storeId;

  public FollowStoreCardTouchEvent(Post card, Long storeId, String storeName, Type actionType) {
    super(card, actionType);
    this.storeName = storeName;
    this.storeId = storeId;
  }

  public String getStoreName() {
    return storeName;
  }

  public Long getStoreId() {
    return storeId;
  }
}
