package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by neuro on 04-08-2016.
 */
public class GridStoreMetaDisplayable extends DisplayablePojo<GetStoreMeta> {

  public GridStoreMetaDisplayable() {
  }

  public GridStoreMetaDisplayable(GetStoreMeta pojo) {
    super(pojo);
  }

  @Override public Type getType() {
    return Type.STORE_META;
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_store_meta;
  }
}
