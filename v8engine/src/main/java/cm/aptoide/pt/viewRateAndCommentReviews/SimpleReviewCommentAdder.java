package cm.aptoide.pt.viewRateAndCommentReviews;

import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import java.util.ArrayList;
import java.util.List;

public class SimpleReviewCommentAdder extends CommentAdder {

  private final RateAndReviewsFragment fragment;

  public SimpleReviewCommentAdder(int reviewIndex, RateAndReviewsFragment fragment) {
    super(reviewIndex);
    this.fragment = fragment;
  }

  @Override public void addComment(List<Comment> comments) {
    int nextReviewPosition = fragment.getAdapter().getReviewPosition(reviewIndex + 1);
    nextReviewPosition =
        nextReviewPosition == -1 ? fragment.getAdapter().getItemCount() : nextReviewPosition;
    fragment.getAdapter().removeDisplayable(nextReviewPosition - 1);
    List<Displayable> displayableList = new ArrayList<>();
    fragment.createDisplayableComments(comments, displayableList);
    fragment.getAdapter().addDisplayables(nextReviewPosition - 1, displayableList);
  }
}
