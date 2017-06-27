package cm.aptoide.pt.spotandshareandroid.hotspotmanager;

import android.net.wifi.WifiConfiguration;

/**
 * Created by neuro on 21-06-2017.
 */
class WifiConfigurationHelper {

  public WifiConfiguration newPrivateWifi(String ssid, String password) {
    WifiConfiguration netConfig = newBaseConfiguration(ssid);

    netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
    netConfig.preSharedKey = password;
    netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
    netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
    netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

    return netConfig;
  }

  public WifiConfiguration newPublicWifi(String ssid) {
    WifiConfiguration netConfig = newBaseConfiguration(ssid);

    netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
    netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

    return netConfig;
  }

  private WifiConfiguration newBaseConfiguration(String ssid) {
    WifiConfiguration netConfig = new WifiConfiguration();
    netConfig.SSID = ssid;
    netConfig.status = WifiConfiguration.Status.ENABLED;

    return netConfig;
  }
}
