/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 09/08/2016.
 */

package cm.aptoide.pt.v8engine.view.comments;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.comments.CommentAdder;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

/**
 * Created by trinkes on 8/5/16.
 */
public class CommentsReadMoreDisplayable extends Displayable {

  private final long resourceId;
  private final int next;
  private final CommentAdder commentAdder;
  private final boolean isReview;

  public CommentsReadMoreDisplayable() {
    this(-1, true, 0, null);
  }

  public CommentsReadMoreDisplayable(long resourceId, boolean isReview, int next,
      CommentAdder commentAdder) {
    this.commentAdder = commentAdder;
    this.next = next;
    this.resourceId = resourceId;
    this.isReview = isReview;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.comments_read_more_layout;
  }

  public long getResourceId() {
    return resourceId;
  }

  public int getNext() {
    return next;
  }

  public CommentAdder getCommentAdder() {
    return commentAdder;
  }

  public boolean isReview() {
    return isReview;
  }
}
