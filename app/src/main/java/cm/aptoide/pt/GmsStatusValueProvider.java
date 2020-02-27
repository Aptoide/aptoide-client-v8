package cm.aptoide.pt;

import android.content.Context;
import cm.aptoide.pt.dataprovider.ads.AdNetworkUtils;

public class GmsStatusValueProvider {

  private static final String HAS_GMS = "Has GMS";
  private static final String NO_GMS = "No GMS";
  private Context context;
  private String gmsValue = "";

  public GmsStatusValueProvider(Context context) {
    this.context = context;
  }

  public String getGmsValue() {
    if (gmsValue == null || gmsValue.isEmpty()) {
      gmsValue = AdNetworkUtils.isGooglePlayServicesAvailable(context) ? HAS_GMS : NO_GMS;
    }
    return gmsValue;
  }
}
