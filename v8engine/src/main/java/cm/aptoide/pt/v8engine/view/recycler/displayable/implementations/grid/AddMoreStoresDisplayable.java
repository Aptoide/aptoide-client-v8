/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 11/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

/**
 * Created by neuro on 11-05-2016.
 */
public class AddMoreStoresDisplayable extends Displayable {

  @Override public Type getType() {
    return Type.ADD_MORE_STORES;
  }

  @Override public int getViewLayout() {
    return R.layout.add_more_stores_row;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }
}
