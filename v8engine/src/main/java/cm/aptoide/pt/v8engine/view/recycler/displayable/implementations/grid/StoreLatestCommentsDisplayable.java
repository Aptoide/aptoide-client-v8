package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import java.util.Collections;
import java.util.List;

public class StoreLatestCommentsDisplayable extends Displayable {

  private final List<Comment> comments;

  public StoreLatestCommentsDisplayable() {
    comments = Collections.emptyList();
  }

  public StoreLatestCommentsDisplayable(List<Comment> comments) {
    this.comments = comments;
  }

  public List<Comment> getComments() {
    return comments;
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_grid_latest_store_comments;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }
}
