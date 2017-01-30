/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 09/08/2016.
 */

package cm.aptoide.pt.viewRateAndCommentReviews;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import lombok.Getter;

public class RateAndReviewCommentDisplayable
    extends DisplayablePojo<ReviewWithAppName> {

  @Getter private CommentAdder commentAdder;
  @Getter private int numberComments;

  public RateAndReviewCommentDisplayable() {
  }

  public RateAndReviewCommentDisplayable(ReviewWithAppName pojo) {
    super(pojo);
  }

  public RateAndReviewCommentDisplayable(ReviewWithAppName pojo, CommentAdder commentAdder,
      int numberComments) {
    super(pojo);
    this.commentAdder = commentAdder;
    this.numberComments = numberComments;
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_rate_and_review;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

}
