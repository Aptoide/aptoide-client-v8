package cm.aptoide.pt.social.data;

/**
 * Created by jdandrade on 03/07/2017.
 */

public class PopularAppTouchEvent extends CardTouchEvent {
  private final Long userId;
  private final String storeTheme;

  public PopularAppTouchEvent(PopularApp card, Long userId, String storeTheme, Type actionType) {
    super(card, actionType);
    this.userId = userId;
    this.storeTheme = storeTheme;
  }

  public Long getUserId() {
    return userId;
  }

  public String getStoreTheme() {
    return storeTheme;
  }
}
