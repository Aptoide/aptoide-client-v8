package cm.aptoide.pt.social.view.viewholder;

import cm.aptoide.pt.social.data.Post;

/**
 * Created by jdandrade on 17/08/2017.
 */

public class NativeAdErrorEvent extends cm.aptoide.pt.social.data.CardTouchEvent {
  private final int postPosition;

  public NativeAdErrorEvent(Post card, Type actionType, int postPosition) {
    super(card, actionType);
    this.postPosition = postPosition;
  }

  public int getPostPosition() {
    return postPosition;
  }
}
