package cm.aptoide.pt.aptoidesdk.entities;

import cm.aptoide.pt.dataprovider.util.DataproviderUtils;

/**
 * Created by neuro on 24-10-2016.
 */

public class AdController {

  public static void clickCpc(Ad ad) {
    DataproviderUtils.knock(ad.clicks.cpcUrl);
  }

  public static void clickCpi(Ad ad) {
    DataproviderUtils.knock(ad.clicks.cpiUrl);
  }
}
