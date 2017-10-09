package cm.aptoide.pt.social.data;

/**
 * Created by franciscocalado on 9/21/17.
 */

public class GameAnswerTouchEvent extends CardTouchEvent {

  private final int cardPosition;

  public GameAnswerTouchEvent(Post card, Type actionType, int position) {
    super(card, position, actionType);
    this.cardPosition = position;
  }

  public int getCardPosition() {
    return cardPosition;
  }
}

