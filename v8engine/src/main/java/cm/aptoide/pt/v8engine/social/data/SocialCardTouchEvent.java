package cm.aptoide.pt.v8engine.social.data;

/**
 * Created by jdandrade on 26/07/2017.
 */

public class SocialCardTouchEvent extends CardTouchEvent {
  private final int postPosition;

  public SocialCardTouchEvent(Post card, Type actionType, int postPosition) {
    super(card, actionType);
    this.postPosition = postPosition;
  }

  public int getPostPosition() {
    return postPosition;
  }
}
