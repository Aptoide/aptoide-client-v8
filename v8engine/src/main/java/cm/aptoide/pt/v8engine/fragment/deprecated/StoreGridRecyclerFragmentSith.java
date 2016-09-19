/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 25/05/2016.
 */

package cm.aptoide.pt.v8engine.fragment.deprecated;

import android.os.Bundle;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragmentWithDecorator;
import cm.aptoide.pt.v8engine.view.recycler.DisplayableType;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sithengineer on 02/05/16.
 *
 * Used for tests only
 */
@Deprecated public class StoreGridRecyclerFragmentSith extends GridRecyclerFragmentWithDecorator {

  private List<Store> stores;

  public static StoreGridRecyclerFragmentSith newInstance() {
    return new StoreGridRecyclerFragmentSith();
  }

  public void setStoreList(List<Store> stores) {
    this.stores = stores;
  }

  @Override public void onStart() {
    super.onStart();
    List<Displayable> displayables = storesToDisplayable(stores);
    addDisplayables(displayables);
  }

  public List<Displayable> storesToDisplayable(List<Store> stores) {
    List<Displayable> displayables = new ArrayList<>(stores.size());
    for (Store store : stores) {
      displayables.add(storeToDisplayable(store));
    }
    return displayables;
  }

  public Displayable storeToDisplayable(Store store) {
    DisplayablePojo<Store> d =
        (DisplayablePojo<Store>) DisplayableType.newDisplayable(Type.STORES_GROUP);
    d.setPojo(store);
    return d;
  }

  @Override public void load(boolean refresh, Bundle savedInstanceState) {

  }

  @Override public void setupToolbar() {

  }
}
