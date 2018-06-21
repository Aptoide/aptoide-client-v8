/*
 * Copyright (c) 2016.
 * Modified on 09/08/2016.
 */

package cm.aptoide.pt.comments.view;

import cm.aptoide.pt.R;
import cm.aptoide.pt.comments.CommentAdder;
import cm.aptoide.pt.comments.ReviewWithAppName;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;

public class RateAndReviewCommentDisplayable extends DisplayablePojo<ReviewWithAppName> {

  private CommentAdder commentAdder;
  private int numberComments;

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

  public CommentAdder getCommentAdder() {
    return commentAdder;
  }

  public int getNumberComments() {
    return numberComments;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_rate_and_review;
  }

  public void itemClicked() {

  }
}
