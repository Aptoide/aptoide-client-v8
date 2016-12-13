package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

/**
 * Created by trinkes on 8/4/16.
 */
public class CommentDisplayable extends Displayable {

  private final Comment comment;

  public CommentDisplayable(Comment comment) {
    this.comment = comment;
  }

  public CommentDisplayable() {
    this.comment = null;
  }

  @Override public int getViewLayout() {
    return R.layout.comment_layout;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  public Comment getComment() {
    return comment;
  }
}
