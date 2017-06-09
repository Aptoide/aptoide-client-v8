package cm.aptoide.pt.v8engine.social.data;

/**
 * Created by jdandrade on 07/06/2017.
 */

public class CardTouchEvent {

  private final Media card;
  private final Type actionType;

  public CardTouchEvent(Media card, Type actionType) {
    this.card = card;
    this.actionType = actionType;
  }

  public Media getCard() {
    return card;
  }

  public Type getActionType() {
    return actionType;
  }

  public enum Type {ARTICLE_HEADER, ARTICLE_BODY}
}
