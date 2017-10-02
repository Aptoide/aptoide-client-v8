package cm.aptoide.pt.social.data;

/**
 * Created by jdandrade on 11/08/2017.
 */

public class MinimalPostTouchEvent extends CardTouchEvent {
  private final Post originalPost;

  public MinimalPostTouchEvent(Post originalPost, MinimalPost post, Type share, int position) {
    super(post, position, share);
    this.originalPost = originalPost;
  }

  public Post getOriginalPost() {
    return originalPost;
  }
}
