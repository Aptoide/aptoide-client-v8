package cm.aptoide.pt.social.data;

/**
 * Created by jdandrade on 26/07/2017.
 */

public class SocialCardTouchEvent extends CardTouchEvent {
  public SocialCardTouchEvent(Post card, Type actionType, int postPosition) {
    super(card, postPosition, actionType);
  }
}
