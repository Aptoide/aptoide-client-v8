package cm.aptoide.pt.viewRateAndCommentReviews;

import cm.aptoide.pt.model.v7.Review;
import cm.aptoide.pt.v8engine.adapters.ReviewsAndCommentsAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

public interface FullCommentAdderView<T extends ReviewsAndCommentsAdapter>
    extends CommentAdderView<T> {
  Displayable createReadMoreDisplayable(int reviewPosition, Review review);
}
