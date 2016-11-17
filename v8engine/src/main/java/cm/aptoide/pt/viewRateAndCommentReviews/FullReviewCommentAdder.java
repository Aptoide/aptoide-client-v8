package cm.aptoide.pt.viewRateAndCommentReviews;

import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.Review;
import cm.aptoide.pt.v8engine.adapters.ReviewsAndCommentsAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import java.util.ArrayList;
import java.util.List;

public class FullReviewCommentAdder extends CommentAdder {

  private final RateAndReviewsFragment fragment;
  private final Review review;

  public FullReviewCommentAdder(int reviewIndex, RateAndReviewsFragment fragment, Review review) {
    super(reviewIndex);
    this.fragment = fragment;
    this.review = review;
  }

  @Override public void addComment(List<Comment> comments) {
    List<Displayable> displayableList = new ArrayList<>();
    fragment.createDisplayableComments(comments, displayableList);
    int reviewPosition = fragment.getAdapter().getReviewPosition(reviewIndex);
    if (comments.size() > 2) {
      displayableList.add(fragment.createReadMoreDisplayable(reviewPosition, review));
    }
    fragment.getAdapter().addDisplayables(reviewPosition + 1, displayableList);
  }

  @Override public void collapseComments() {
    ReviewsAndCommentsAdapter adapter = fragment.getAdapter();
    int reviewIndex = adapter.getReviewPosition(this.reviewIndex);
    int nextReview = adapter.getReviewPosition(this.reviewIndex + 1);
    nextReview = nextReview == -1 ? fragment.getAdapter().getItemCount() : nextReview;
    adapter.removeDisplayables(reviewIndex + 1, nextReview - 1);
    // the -1 because we don't want to remove the next review,only until
    // the comment before the review
  }
}
