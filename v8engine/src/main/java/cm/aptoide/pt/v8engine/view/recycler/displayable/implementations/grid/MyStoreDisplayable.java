package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import lombok.Getter;

/**
 * Created by trinkes on 05/12/2016.
 */

public class MyStoreDisplayable extends Displayable {
  @Getter private StoreThemeEnum theme;

  public MyStoreDisplayable() {
  }

  public MyStoreDisplayable(StoreThemeEnum theme) {
    this.theme = theme;
  }

  @Override public int getViewLayout() {
    return R.layout.my_store_displayable_layout;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }
}
