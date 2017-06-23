package cm.aptoide.pt.v8engine.social.data;

/**
 * Created by jdandrade on 07/06/2017.
 */

public class CardTouchEvent {

  private final Card card;
  private final Type actionType;

  public CardTouchEvent(Card card, Type actionType) {
    this.card = card;
    this.actionType = actionType;
  }

  public Card getCard() {
    return card;
  }

  public Type getActionType() {
    return actionType;
  }

  public enum Type {HEADER, BODY}
}
