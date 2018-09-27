package cm.aptoide.pt.comment;

import android.os.Bundle;
import cm.aptoide.pt.commentdetail.CommentDetailFragment;
import cm.aptoide.pt.navigator.FragmentNavigator;

public class CommentsNavigator {
  private final FragmentNavigator fragmentNavigator;

  public CommentsNavigator(FragmentNavigator fragmentNavigator) {
    this.fragmentNavigator = fragmentNavigator;
  }

  public void navigateToCommentView(Long commentId) {
    CommentDetailFragment fragment = new CommentDetailFragment();
    Bundle args = new Bundle();
    args.putLong("comment_id", commentId);
    fragment.setArguments(args);
    fragmentNavigator.navigateTo(fragment, true);
  }
}
