package cm.aptoide.pt.viewRateAndCommentReviews;

import cm.aptoide.pt.model.v7.Comment;
import java.util.List;

abstract class CommentAdder {

  final int reviewIndex;

  CommentAdder(int reviewIndex) {
    this.reviewIndex = reviewIndex;
  }

  public abstract void addComment(List<Comment> comments);

  public abstract void collapseComments();
}
