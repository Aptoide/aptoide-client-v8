package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import java.util.Collections;
import java.util.List;

public class StoreLatestCommentsDisplayable extends Displayable {

  private final long storeId;
  private String storeName;
  private final List<Comment> comments;

  public StoreLatestCommentsDisplayable() {
    this.storeId = -1;
    this.comments = Collections.emptyList();
  }

  public StoreLatestCommentsDisplayable(long storeId, String storeName, List<Comment> comments) {
    this.storeId = storeId;
    this.storeName = storeName;
    this.comments = comments;
  }

  public List<Comment> getComments() {
    return comments;
  }

  public long getStoreId() {
    return storeId;
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_grid_latest_store_comments;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  public String getStoreName() {
    return storeName;
  }
}
