package cm.aptoide.pt.social.data;

import cm.aptoide.pt.social.data.CardTouchEvent;
import cm.aptoide.pt.social.data.MinimalPost;
import cm.aptoide.pt.social.data.Post;

/**
 * Created by jdandrade on 11/08/2017.
 */

public class MinimalPostTouchEvent extends CardTouchEvent {
  private final Post originalPost;

  public MinimalPostTouchEvent(Post originalPost, MinimalPost post, Type share) {
    super(post, share);
    this.originalPost = originalPost;
  }

  public Post getOriginalPost() {
    return originalPost;
  }
}
