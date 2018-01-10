package cm.aptoide.pt.social.commentslist;

import android.support.v4.app.FragmentManager;
import cm.aptoide.pt.comments.view.CommentDialogFragment;
import cm.aptoide.pt.navigator.FragmentNavigator;

/**
 * Created by jdandrade on 13/12/2017.
 */

class CommentsNavigator {
  private final FragmentNavigator fragmentNavigator;
  private final FragmentManager fragmentManager;

  CommentsNavigator(FragmentNavigator fragmentNavigator, FragmentManager fragmentManager) {
    this.fragmentNavigator = fragmentNavigator;
    this.fragmentManager = fragmentManager;
  }

  void showCommentDialog(String postId, Long commentId) {
    CommentDialogFragment commentDialog =
        CommentDialogFragment.newInstanceTimelineArticleComment(postId, commentId);
    commentDialog.show(fragmentManager, "fragment_comment_dialog");
  }

  void showCommentDialog(String postId) {
    CommentDialogFragment commentDialog =
        CommentDialogFragment.newInstanceTimelineArticleComment(postId);
    commentDialog.show(fragmentManager, "fragment_comment_dialog");
  }

  void showCommentDialog(String postId, PostCommentsFragment view) {
    CommentDialogFragment commentDialog =
        CommentDialogFragment.newInstanceTimelineArticleComment(postId);
    commentDialog.setCommentDialogCallbackContract(view);
    commentDialog.show(fragmentManager, "fragment_comment_dialog");
  }
}
