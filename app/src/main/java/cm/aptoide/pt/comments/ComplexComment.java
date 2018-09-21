package cm.aptoide.pt.comments;

import cm.aptoide.pt.dataprovider.model.v7.Comment;
import rx.Completable;

public class ComplexComment extends Comment {
  private final Completable onClickReplyAction;
  private final int level;

  public ComplexComment(CommentNode commentNode, Completable onClickReplyAction) {
    this.level = commentNode.getLevel();
    Comment comment = commentNode.getComment();
    this.setAdded(comment.getAdded());
    this.setBody(comment.getBody());
    this.setId(comment.getId());
    if (comment.getParent() != null) {
      this.setParent(comment.getParent());
    }
    this.setParentReview(comment.getParentReview());
    this.setUser(comment.getUser());
    this.setStats(comment.getStats());
    this.onClickReplyAction = onClickReplyAction;
  }

  public Completable observeReplySubmission() {
    return onClickReplyAction;
  }

  public int getLevel() {
    return level;
  }
}
