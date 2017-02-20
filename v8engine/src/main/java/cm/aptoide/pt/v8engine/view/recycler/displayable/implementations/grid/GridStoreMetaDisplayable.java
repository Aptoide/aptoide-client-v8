package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import java.util.Collections;
import java.util.List;

/**
 * Created by neuro on 04-08-2016.
 */
public class GridStoreMetaDisplayable extends DisplayablePojo<GetStoreMeta> {

  public GridStoreMetaDisplayable() {
  }

  public GridStoreMetaDisplayable(GetStoreMeta pojo) {
    super(pojo);
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_store_meta;
  }

  public List<Store.SocialChannel> getSocialLinks() {
    return getPojo().getData().getSocialChannels() == null ? Collections.EMPTY_LIST
        : getPojo().getData().getSocialChannels();
  }
}
