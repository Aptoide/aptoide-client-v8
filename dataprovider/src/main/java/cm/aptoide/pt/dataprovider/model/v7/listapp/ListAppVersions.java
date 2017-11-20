/*
 * Copyright (c) 2016.
 * Modified on 18/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7.listapp;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7EndlessListResponse;

/**
 * Created by neuro on 22-04-2016.
 */
public class ListAppVersions extends BaseV7EndlessListResponse<App> {

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof ListAppVersions)) return false;
    final ListAppVersions other = (ListAppVersions) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    return true;
  }

  protected boolean canEqual(Object other) {
    return other instanceof ListAppVersions;
  }
}
