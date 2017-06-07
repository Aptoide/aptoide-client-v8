package cm.aptoide.pt.v8engine.social.data;

/**
 * Created by jdandrade on 07/06/2017.
 */

public class CardTouchEvent {

  private final Article card;
  private final Type actionType;

  public CardTouchEvent(Article card, Type actionType) {
    this.card = card;
    this.actionType = actionType;
  }

  public Article getCard() {
    return card;
  }

  public Type getActionType() {
    return actionType;
  }

  public enum Type {ARTICLE_HEADER, ARTICLE_BODY}
}
