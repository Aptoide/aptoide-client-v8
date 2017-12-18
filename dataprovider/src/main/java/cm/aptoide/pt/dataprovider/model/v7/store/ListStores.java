/*
 * Copyright (c) 2016.
 * Modified on 18/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7.store;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7EndlessDataListResponse;

/**
 * Created by neuro on 27-04-2016.
 */
public class ListStores extends BaseV7EndlessDataListResponse<Store> {

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof ListStores)) return false;
    final ListStores other = (ListStores) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    return true;
  }

  protected boolean canEqual(Object other) {
    return other instanceof ListStores;
  }
}
