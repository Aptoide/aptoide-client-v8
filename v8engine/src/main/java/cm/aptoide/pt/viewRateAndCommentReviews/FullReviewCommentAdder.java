package cm.aptoide.pt.viewRateAndCommentReviews;

import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.Review;
import cm.aptoide.pt.v8engine.adapters.ReviewsAndCommentsAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import java.util.ArrayList;
import java.util.List;

public class FullReviewCommentAdder extends CommentAdder {

  private final FullCommentAdderView commentAdderView;
  private final Review review;

  public FullReviewCommentAdder(int reviewIndex, FullCommentAdderView commentAdderView, Review review) {
    super(reviewIndex);
    this.commentAdderView = commentAdderView;
    this.review = review;
  }

  @Override public void addComment(List<Comment> comments) {
    List<Displayable> displayableList = new ArrayList<>();
    commentAdderView.createDisplayableComments(comments, displayableList);
    int reviewPosition = commentAdderView.getAdapter().getReviewPosition(reviewIndex);
    if (comments.size() > 2) {
      displayableList.add(commentAdderView.createReadMoreDisplayable(reviewPosition, review));
    }
    commentAdderView.getAdapter().addDisplayables(reviewPosition + 1, displayableList);
  }

  @Override public void collapseComments() {
    ReviewsAndCommentsAdapter adapter = commentAdderView.getAdapter();
    int reviewIndex = adapter.getReviewPosition(this.reviewIndex);
    int nextReview = adapter.getReviewPosition(this.reviewIndex + 1);
    nextReview = nextReview == -1 ? commentAdderView.getAdapter().getItemCount() : nextReview;
    adapter.removeDisplayables(reviewIndex + 1, nextReview - 1);
    // the -1 because we don't want to remove the next review,only until
    // the comment before the review
  }
}
