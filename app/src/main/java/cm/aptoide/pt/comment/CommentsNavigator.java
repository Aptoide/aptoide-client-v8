package cm.aptoide.pt.comment;

import android.os.Bundle;
import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.commentdetail.CommentDetailFragment;
import cm.aptoide.pt.navigator.FragmentNavigator;

public class CommentsNavigator {
  private final FragmentNavigator fragmentNavigator;

  public CommentsNavigator(FragmentNavigator fragmentNavigator) {
    this.fragmentNavigator = fragmentNavigator;
  }

  public void navigateToCommentView(Comment comment) {
    CommentDetailFragment fragment = new CommentDetailFragment();
    Bundle args = new Bundle();
    args.putLong("comment_id", comment.getId());
    args.putString("comment_message", comment.getMessage());
    args.putLong("comment_user_id", comment.getUser()
        .getId());
    args.putString("comment_user_avatar", comment.getUser()
        .getAvatar());
    args.putString("comment_user_name", comment.getUser()
        .getName());
    args.putLong("comment_replies_number", comment.getReplies());
    args.putString("comment_timestamp", comment.getDate()
        .toString());
    fragment.setArguments(args);
    fragmentNavigator.navigateTo(fragment, true);
  }
}
