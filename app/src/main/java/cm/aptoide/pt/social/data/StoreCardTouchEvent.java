package cm.aptoide.pt.social.data;

/**
 * Created by jdandrade on 04/07/2017.
 */

public class StoreCardTouchEvent extends CardTouchEvent {
  private final String storeName;
  private final String storeTheme;

  public StoreCardTouchEvent(Post card, String storeName, String storeTheme, Type actionType,
      int position) {
    super(card, position, actionType);
    this.storeName = storeName;
    this.storeTheme = storeTheme;
  }

  public String getStoreName() {
    return storeName;
  }

  public String getStoreTheme() {
    return storeTheme;
  }
}
