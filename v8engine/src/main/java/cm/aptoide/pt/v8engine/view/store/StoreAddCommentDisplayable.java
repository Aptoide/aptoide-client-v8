package cm.aptoide.pt.v8engine.view.store;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

public class StoreAddCommentDisplayable extends Displayable {

  private final long storeId;
  private final String storeName;
  private final StoreThemeEnum storeTheme;

  public StoreAddCommentDisplayable() {
    storeId = -1;
    storeName = "";
    storeTheme = StoreThemeEnum.APTOIDE_STORE_THEME_DEFAULT;
  }

  public StoreAddCommentDisplayable(long storeId, String storeName, StoreThemeEnum storeTheme) {
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

  public StoreThemeEnum getStoreTheme() {
    return storeTheme;
  }
}
