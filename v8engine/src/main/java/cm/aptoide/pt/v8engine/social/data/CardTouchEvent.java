package cm.aptoide.pt.v8engine.social.data;

/**
 * Created by jdandrade on 07/06/2017.
 */

public class CardTouchEvent {

  private final Post card;
  private final Type actionType;

  public CardTouchEvent(Post card, Type actionType) {
    this.card = card;
    this.actionType = actionType;
  }

  public Post getCard() {
    return card;
  }

  public Type getActionType() {
    return actionType;
  }

  public enum Type {HEADER, LIKE, TIMELINE_STATS, LOGIN, COMMENT, SHARE, LIKES_PREVIEW, COMMENT_NUMBER, BODY}
}
