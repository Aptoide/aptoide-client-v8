package cm.aptoide.pt.store.view;

import cm.aptoide.pt.R;
import cm.aptoide.pt.themes.StoreTheme;
import cm.aptoide.pt.view.recycler.displayable.Displayable;

public class StoreAddCommentDisplayable extends Displayable {

  private final long storeId;
  private final String storeName;
  private final StoreTheme storeTheme;
  private final int raisedButtonDrawable;

  public StoreAddCommentDisplayable() {
    storeId = -1;
    storeName = "";
    storeTheme = StoreTheme.DEFAULT;
    raisedButtonDrawable = R.drawable.aptoide_gradient_rounded;
  }

  public StoreAddCommentDisplayable(long storeId, String storeName, StoreTheme storeTheme,
      int raisedButtonDrawable) {
    this.storeId = storeId;
    this.storeName = storeName;
    this.storeTheme = storeTheme;
    this.raisedButtonDrawable = raisedButtonDrawable;
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

  public int getRaisedButtonDrawable() {
    return raisedButtonDrawable;
  }
}
