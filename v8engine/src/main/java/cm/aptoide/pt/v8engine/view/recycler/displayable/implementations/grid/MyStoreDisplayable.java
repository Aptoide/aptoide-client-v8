package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import lombok.Getter;

/**
 * Created by trinkes on 05/12/2016.
 */

public class MyStoreDisplayable extends Displayable {
  @Getter private GetStoreMeta meta;

  public MyStoreDisplayable() {
  }

  public MyStoreDisplayable(GetStoreMeta meta) {
    this.meta = meta;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.my_store_displayable_layout;
  }
}
