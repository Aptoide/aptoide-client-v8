package cm.aptoide.pt.navigator;

import android.os.Bundle;

/**
 * Created by jdandrade on 16/01/2018.
 */

public class CommentsTimelineTabNavigation implements TabNavigation {
  public static final String COMMENT_KEY = "comment_key";
  public static final String POST_ID = "post_id";
  public static final String ERROR_STATUS = "error_status";

  private String commentBody;
  private String postId;
  private boolean error;

  public CommentsTimelineTabNavigation(String commentBody, String postId, boolean error) {
    this.commentBody = commentBody;
    this.postId = postId;
    this.error = error;
  }

  public CommentsTimelineTabNavigation(String postId, boolean error) {
    this("", postId, error);
  }

  @Override public Bundle getBundle() {
    Bundle bundle = new Bundle();
    bundle.putString(COMMENT_KEY, commentBody);
    bundle.putString(POST_ID, postId);
    bundle.putBoolean(ERROR_STATUS, error);

    return bundle;
  }

  @Override public int getTab() {
    return COMMENTS;
  }
}
