package cm.aptoide.pt.v8engine.social.data.share;

import android.support.annotation.IntDef;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.pt.v8engine.social.data.Post;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ShareEvent {
  public static final int SHARE = 0;
  public static final int CANCEL = -1;
  private final @EventType int event;
  private final Post post;
  private Account.Access access;

  ShareEvent(@EventType int event, Post post) {
    this.event = event;
    this.post = post;
  }

  public @EventType int getEvent() {
    return event;
  }

  public Post getPost() {
    return post;
  }

  public Account.Access getAccess() {
    return access;
  }

  void setAccess(Account.Access access) {
    this.access = access;
  }

  @Retention(RetentionPolicy.SOURCE) @IntDef({ SHARE, CANCEL }) public @interface EventType {
  }
}
