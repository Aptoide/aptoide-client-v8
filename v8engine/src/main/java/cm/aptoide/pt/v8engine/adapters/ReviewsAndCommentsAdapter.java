package cm.aptoide.pt.v8engine.adapters;

import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RateAndReviewCommentDisplayable;

/**
 * Created by trinkes on 8/5/16.
 */
public class ReviewsAndCommentsAdapter extends BaseAdapter {

  /**
   * Get the review position using the number of the review. For example, if
   * <code>reviewNumber</code> == 2, it will return the third review it finds in
   * <code>displayable</code>.
   *
   * @param reviewNumber number of the review
   * @return next review's position or -1 if there are no more reviews
   */
  public int getReviewPosition(int reviewNumber) {
    int toReturn = -1;

    int reviewsCounter = 0;
    for (int i = 0; i < getItemCount(); i++) {
      if (getDisplayable(i) instanceof RateAndReviewCommentDisplayable) {
        if (reviewsCounter == reviewNumber) {
          toReturn = i;
          break;
        } else {
          reviewsCounter++;
        }
      }
    }
    return toReturn;
  }
}
