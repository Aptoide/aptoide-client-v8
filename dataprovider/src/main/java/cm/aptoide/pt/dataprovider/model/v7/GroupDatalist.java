/*
 * Copyright (c) 2016.
 * Modified on 18/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

/**
 * Created by neuro on 22-04-2016.
 */
public class GroupDatalist extends BaseV7EndlessDataListResponse<Group> {

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof GroupDatalist;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof GroupDatalist)) return false;
    final GroupDatalist other = (GroupDatalist) o;
    if (!other.canEqual(this)) return false;
    return super.equals(o);
  }
}
