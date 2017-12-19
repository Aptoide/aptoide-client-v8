/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/04/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7.listapp;

/**
 * Created by neuro on 22-04-2016.
 */

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import java.util.List;

public class ListAppsUpdates extends BaseV7Response {

  private List<App> list;

  public ListAppsUpdates() {
  }

  /*
   * fixme
   * <p>this hack is to prevent updates from not being emited.</p>
   *
   * When a ListAppsUpdates request is made,
   * the request is broken down to 3 different listAppsUpdates (blocks of 50 apps) and
   * then the respective answers are being merged and shown to us as a result.
   * The requests info are not being merged into the final answer and
   * isOk is always returning false
   *
   */
  @Override public boolean isOk() {
    return (super.isOk() || list != null);
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    final Object $list = this.getList();
    result = result * PRIME + ($list == null ? 43 : $list.hashCode());
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof ListAppsUpdates;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof ListAppsUpdates)) return false;
    final ListAppsUpdates other = (ListAppsUpdates) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    final Object this$list = this.getList();
    final Object other$list = other.getList();
    if (this$list == null ? other$list != null : !this$list.equals(other$list)) return false;
    return true;
  }

  public String toString() {
    return "ListAppsUpdates(list=" + this.getList() + ")";
  }

  public List<App> getList() {
    return this.list;
  }

  public void setList(List<App> list) {
    this.list = list;
  }
}
