package cm.aptoide.pt.view.store;

import cm.aptoide.pt.R;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.view.recycler.displayable.Displayable;

public class StoreAddCommentDisplayable extends Displayable {

  private final long storeId;
  private final String storeName;
  private final StoreTheme storeTheme;

  public StoreAddCommentDisplayable() {
    storeId = -1;
    storeName = "";
    storeTheme = StoreTheme.DEFAULT;
  }

  public StoreAddCommentDisplayable(long storeId, String storeName, StoreTheme storeTheme) {
    this.storeId = storeId;
    this.storeName = storeName;
    this.storeTheme = storeTheme;
  }

  public long getStoreId() {
    return storeId;
  }

  public String getStoreName() {
    return storeName;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_grid_add_store_comment;
  }

  public StoreTheme getStoreTheme() {
    return storeTheme;
  }
}
