/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/05/2016.
 */

package cm.aptoide.pt.store.view.subscribed;

import cm.aptoide.pt.R;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;

/**
 * Created by neuro on 11-05-2016.
 */
public class SubscribedStoreDisplayable extends DisplayablePojo<Store> {

  public SubscribedStoreDisplayable() {
    super();
  }

  public SubscribedStoreDisplayable(Store pojo) {
    super(pojo);
  }

  @Override protected Configs getConfig() {
    return new Configs(3, false);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_grid_store_subscribed;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof SubscribedStoreDisplayable)) return false;
    final SubscribedStoreDisplayable other = (SubscribedStoreDisplayable) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    return true;
  }

  public String toString() {
    return "SubscribedStoreDisplayable()";
  }

  protected boolean canEqual(Object other) {
    return other instanceof SubscribedStoreDisplayable;
  }
}
