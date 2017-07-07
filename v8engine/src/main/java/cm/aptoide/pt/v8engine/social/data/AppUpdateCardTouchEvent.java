package cm.aptoide.pt.v8engine.social.data;

/**
 * Created by jdandrade on 25/06/2017.
 */

public class AppUpdateCardTouchEvent extends CardTouchEvent {
  private final int cardPosition;

  public AppUpdateCardTouchEvent(AppUpdate card, Type touchType, int cardPosition) {
    super(card, touchType);
    this.cardPosition = cardPosition;
  }

  public int getCardPosition() {
    return cardPosition;
  }
}
