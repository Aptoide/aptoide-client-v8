package cm.aptoide.pt.viewRateAndCommentReviews;

import cm.aptoide.pt.model.v7.Comment;
import java.util.List;

abstract class CommentAdder {

  final int itemIndex;

  CommentAdder(int itemIndex) {
    this.itemIndex = itemIndex;
  }

  public abstract void addComment(List<Comment> comments);

  public void collapseComments() {
    // non-mandatory method
  }
}
