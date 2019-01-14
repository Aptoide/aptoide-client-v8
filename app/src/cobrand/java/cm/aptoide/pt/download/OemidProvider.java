package cm.aptoide.pt.download;

import cm.aptoide.pt.BuildConfig;

public class OemidProvider {

  public String getOemid() {
    return BuildConfig.COBRAND_OEMID;
  }
}
