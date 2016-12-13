package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by trinkes on 06/12/2016.
 */

public class RecommendedStoreDisplayable extends DisplayablePojo<Store> {
  public RecommendedStoreDisplayable() {
  }

  public RecommendedStoreDisplayable(Store pojo) {
    super(pojo);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_recommended_store;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }
}
