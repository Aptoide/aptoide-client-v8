package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by trinkes on 8/4/16.
 */
public class CommentDisplayable extends DisplayablePojo<Comment> {

  public CommentDisplayable(Comment pojo) {
    super(pojo);
  }

  public CommentDisplayable() {
  }

  @Override public int getViewLayout() {
    return R.layout.comment_layout;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }
}
