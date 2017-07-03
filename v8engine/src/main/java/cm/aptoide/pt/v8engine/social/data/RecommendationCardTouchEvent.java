package cm.aptoide.pt.v8engine.social.data;

/**
 * Created by jdandrade on 03/07/2017.
 */

public class RecommendationCardTouchEvent extends CardTouchEvent {
  private final String storeName;
  private final String storeTheme;
  private final long userId;

  public RecommendationCardTouchEvent(Post card, String storeName, String storeTheme, long userId,
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

  public long getUserId() {
    return userId;
  }
}
