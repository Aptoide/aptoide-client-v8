package cm.aptoide.pt.social.commentslist;

import android.support.v4.app.FragmentManager;
import cm.aptoide.pt.comments.view.CommentDialogFragment;
import cm.aptoide.pt.navigator.CommentsTimelineTabNavigation;
import cm.aptoide.pt.navigator.TabNavigator;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 13/12/2017.
 */

class CommentsNavigator {
  private final FragmentManager fragmentManager;
  private final PublishSubject<CommentDataWrapper> commentDialogSubject;
  private final PublishSubject<String> commentOnErrorSubject;
  private final TabNavigator tabNavigator;

  CommentsNavigator(FragmentManager fragmentManager,
      PublishSubject<CommentDataWrapper> successSubject, PublishSubject<String> onErrorSubject,
      TabNavigator tabNavigator) {
    this.fragmentManager = fragmentManager;
    this.commentDialogSubject = successSubject;
    this.commentOnErrorSubject = onErrorSubject;
    this.tabNavigator = tabNavigator;
  }

  void navigateToPostCommentInTimeline(String postId, String comment) {
    tabNavigator.navigate(new CommentsTimelineTabNavigation(comment, postId, false));
  }

  void navigateToPostCommentInTimelineError(String postId) {
    tabNavigator.navigate(new CommentsTimelineTabNavigation(postId, true));
  }

  void showCommentDialog(String postId, Long commentId) {
    CommentDialogFragment commentDialog =
        CommentDialogFragment.newInstanceTimelineArticleComment(postId, commentId);
    commentDialog.setCommentDialogCallbackContract(
        (inputText, longAsId, previousCommentId, idAsString) -> commentDialogSubject.onNext(
            new CommentDataWrapper(inputText, longAsId, commentId, postId)));
    commentDialog.setCommentOnErrorCallbackContract(
        (postId1) -> commentOnErrorSubject.onNext(postId1));
    commentDialog.show(fragmentManager, "fragment_comment_dialog");
  }

  void showCommentDialog(String postId) {
    CommentDialogFragment commentDialog =
        CommentDialogFragment.newInstanceTimelineArticleComment(postId);
    commentDialog.setCommentDialogCallbackContract(
        (inputText, longAsId, previousCommentId, idAsString) -> commentDialogSubject.onNext(
            new CommentDataWrapper(inputText, longAsId, previousCommentId, postId)));
    commentDialog.setCommentOnErrorCallbackContract(
        (postId1) -> commentOnErrorSubject.onNext(postId1));
    commentDialog.show(fragmentManager, "fragment_comment_dialog");
  }

  Observable<CommentDataWrapper> commentDialogResult() {
    return commentDialogSubject;
  }

  Observable<String> commentDialogOnError() {
    return commentOnErrorSubject;
  }
}
