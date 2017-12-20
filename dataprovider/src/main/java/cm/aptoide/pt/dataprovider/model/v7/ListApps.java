/*
 * Copyright (c) 2016.
 * Modified on 18/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

import cm.aptoide.pt.dataprovider.model.v7.listapp.App;

/**
 * Created by neuro on 27-04-2016.
 */
public class ListApps extends BaseV7EndlessDataListResponse<App> {

  public ListApps() {
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof ListApps;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof ListApps)) return false;
    final ListApps other = (ListApps) o;
    if (!other.canEqual(this)) return false;
    return super.equals(o);
  }

  public String toString() {
    return "ListApps()";
  }
}
