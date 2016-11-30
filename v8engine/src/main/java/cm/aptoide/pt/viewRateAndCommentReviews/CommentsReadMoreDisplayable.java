/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 09/08/2016.
 */

package cm.aptoide.pt.viewRateAndCommentReviews;

import cm.aptoide.pt.model.v7.Review;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import lombok.Getter;

/**
 * Created by trinkes on 8/5/16.
 */
public class CommentsReadMoreDisplayable extends DisplayablePojo<Review> {

  @Getter private CommentAdder commentAdder;
  @Getter private int next;

  public CommentsReadMoreDisplayable() {
  }

  public CommentsReadMoreDisplayable(Review review, int next,
      CommentAdder commentAdder) {
    super(review);
    this.commentAdder = commentAdder;
    this.next = next;
  }

  @Override public int getViewLayout() {
    return R.layout.comments_read_more_layout;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }
}
