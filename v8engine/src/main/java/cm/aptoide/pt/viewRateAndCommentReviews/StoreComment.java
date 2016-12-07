package cm.aptoide.pt.viewRateAndCommentReviews;

import cm.aptoide.pt.model.v7.Comment;
import rx.Observable;

public class StoreComment extends Comment {

  private final Observable<Void> onClickReplyAction;

  public StoreComment(Comment comment, Observable<Void> onClickReplyAction) {
    this.setAdded(comment.getAdded());
    this.setBody(comment.getBody());
    this.setId(comment.getId());
    this.setParentReview(comment.getParentReview());
    this.setUser(comment.getUser());
    this.onClickReplyAction = onClickReplyAction;
  }

  public Observable<Void> observeReplySubmission() {
    return onClickReplyAction;
  }
}
