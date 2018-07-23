/*
 * Copyright (c) 2016.
 * Modified on 18/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

/**
 * Created by neuro on 27-04-2016.
 */
public class ListRewardApps extends BaseV7EndlessDataListResponse<AppCoinsCampaign> {

  public ListRewardApps() {
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof ListRewardApps;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof ListRewardApps)) return false;
    final ListRewardApps other = (ListRewardApps) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    return true;
  }

  public String toString() {
    return "ListApps()";
  }
}
