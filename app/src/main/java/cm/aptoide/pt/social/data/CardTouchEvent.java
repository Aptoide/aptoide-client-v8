package cm.aptoide.pt.social.data;

/**
 * Created by jdandrade on 07/06/2017.
 */

public class CardTouchEvent {

  private final Post card;
  private final Type actionType;
  private final int position;

  public CardTouchEvent(Post card, int position, Type actionType) {
    this.card = card;
    this.position = position;
    this.actionType = actionType;
  }

  public Post getCard() {
    return card;
  }

  public Type getActionType() {
    return actionType;
  }

  public int getPosition() {
    return position;
  }

  public enum Type {HEADER, LIKE, TIMELINE_STATS, LOGIN, COMMENT, SHARE, LIKES_PREVIEW, COMMENT_NUMBER, LAST_COMMENT, BODY, ERROR, NOTIFICATION, NOTIFICATION_CENTER, POST, DELETE_POST, REPORT_ABUSE, UNFOLLOW_STORE, ADD_FRIEND}
}
