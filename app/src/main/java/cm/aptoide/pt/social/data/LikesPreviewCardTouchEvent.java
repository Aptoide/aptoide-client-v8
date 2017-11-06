package cm.aptoide.pt.social.data;

/**
 * Created by jdandrade on 08/07/2017.
 */

public class LikesPreviewCardTouchEvent extends CardTouchEvent {
  private final long likesNumber;

  public LikesPreviewCardTouchEvent(Post post, long likesNumber, Type type, int position) {
    super(post, position, type);
    this.likesNumber = likesNumber;
  }

  public long getLikesNumber() {
    return likesNumber;
  }
}
