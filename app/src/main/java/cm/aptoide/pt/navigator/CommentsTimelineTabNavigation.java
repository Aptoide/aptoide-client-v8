package cm.aptoide.pt.navigator;

import android.os.Bundle;

/**
 * Created by jdandrade on 16/01/2018.
 */

public class CommentsTimelineTabNavigation implements TabNavigation {
  public static final String COMMENT_KEY = "comment_key";
  private String commentBody;

  public CommentsTimelineTabNavigation(String commentBody) {
    this.commentBody = commentBody;
  }

  @Override public Bundle getBundle() {
    Bundle bundle = new Bundle();
    bundle.putString(COMMENT_KEY, commentBody);




    return bundle;
  }

  @Override public int getTab() {
    return COMMENTS;
  }
}
