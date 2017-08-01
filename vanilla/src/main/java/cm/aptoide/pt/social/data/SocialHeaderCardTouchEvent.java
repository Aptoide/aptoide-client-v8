package cm.aptoide.pt.social.data;

/**
 * Created by jdandrade on 03/07/2017.
 */

public class SocialHeaderCardTouchEvent extends CardTouchEvent {
  private final String storeName;
  private final String storeTheme;
  private final Long userId;

  public SocialHeaderCardTouchEvent(Post card, String storeName, String storeTheme, Long userId,
      Type actionType) {
    super(card, actionType);
    this.storeName = storeName;
    this.storeTheme = storeTheme;
    this.userId = userId;
  }

  public String getStoreName() {
    return storeName;
  }

  public String getStoreTheme() {
    return storeTheme;
  }

  public Long getUserId() {
    return userId;
  }
}
