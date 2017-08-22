package cm.aptoide.pt.v8engine.social.data;

/**
 * Created by jdandrade on 08/07/2017.
 */

public class LikesCardTouchEvent extends CardTouchEvent {
  private final long likesNumber;

  public LikesCardTouchEvent(Post post, long likesNumber, Type type) {
    super(post, type);
    this.likesNumber = likesNumber;
  }

  public long getLikesNumber() {
    return likesNumber;
  }
}
