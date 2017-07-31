package cm.aptoide.pt.v8engine.social.data.share;

import android.support.annotation.IntDef;
import cm.aptoide.pt.v8engine.social.data.Post;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ShareEvent {
  private final @EventType int event;
  private final Post post;

  public ShareEvent(@EventType int event, Post post) {
    this.event = event;
    this.post = post;
  }

  public @EventType int getEvent() {
    return event;
  }

  public Post getPost() {
    return post;
  }

  @Retention(RetentionPolicy.SOURCE)
  @IntDef({SHARE, CANCEL})
  public @interface EventType {}
  public static final int SHARE = 0;
  public static final int CANCEL = -1;
}
