/*
 * Copyright (c) 2016.
 * Modified on 18/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

/**
 * Created by neuro on 27-04-2016.
 */
public class ListAppCoinsCampaigns extends BaseV7EndlessListResponse<AppCoinsCampaign> {

  public ListAppCoinsCampaigns() {
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof ListAppCoinsCampaigns;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof ListAppCoinsCampaigns)) return false;
    final ListAppCoinsCampaigns other = (ListAppCoinsCampaigns) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    return true;
  }

  public String toString() {
    return "ListApps()";
  }
}
