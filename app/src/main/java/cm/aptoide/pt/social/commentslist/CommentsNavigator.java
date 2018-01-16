package cm.aptoide.pt.social.commentslist;

import android.support.v4.app.FragmentManager;
import cm.aptoide.pt.comments.view.CommentDialogFragment;
import cm.aptoide.pt.navigator.FragmentNavigator;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 13/12/2017.
 */

class CommentsNavigator {
  private final FragmentNavigator fragmentNavigator;
  private final FragmentManager fragmentManager;
  private final PublishSubject<CommentDataWrapper> commentResultPublishSubject;

  CommentsNavigator(FragmentNavigator fragmentNavigator, FragmentManager fragmentManager,
      PublishSubject<CommentDataWrapper> subject) {
    this.fragmentNavigator = fragmentNavigator;
    this.fragmentManager = fragmentManager;
    this.commentResultPublishSubject = subject;
  }

  void showCommentDialog(String postId, Long commentId) {
    CommentDialogFragment commentDialog =
        CommentDialogFragment.newInstanceTimelineArticleComment(postId, commentId);
    commentDialog.setCommentDialogCallbackContract(
        (inputText, longAsId, previousCommentId, idAsString) -> commentResultPublishSubject.onNext(
            new CommentDataWrapper(inputText, longAsId, commentId, postId)));
    commentDialog.show(fragmentManager, "fragment_comment_dialog");
  }

  void showCommentDialog(String postId) {
    CommentDialogFragment commentDialog =
        CommentDialogFragment.newInstanceTimelineArticleComment(postId);
    commentDialog.setCommentDialogCallbackContract(
        (inputText, longAsId, previousCommentId, idAsString) -> commentResultPublishSubject.onNext(
            new CommentDataWrapper(inputText, longAsId, previousCommentId, postId)));
    commentDialog.show(fragmentManager, "fragment_comment_dialog");
  }

  Observable<CommentDataWrapper> commentDialogResult() {
    return commentResultPublishSubject;
  }
}
