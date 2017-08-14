/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 27/07/2016.
 */

package cm.aptoide.pt.dataprovider;

import android.telephony.TelephonyManager;

public class NetworkOperatorManager {

  private final TelephonyManager telephonyManager;

  public NetworkOperatorManager(TelephonyManager telephonyManager) {
    this.telephonyManager = telephonyManager;
  }

  public String getMobileCountryCode() {
    final String networkOperator = telephonyManager.getNetworkOperator();
    return networkOperator == null ? ""
        : networkOperator.substring(0, codePortionLength(networkOperator));
  }

  private int codePortionLength(String networkOperator) {
    return Math.min(3, networkOperator.length());
  }

  public boolean isSimStateReady() {
    return telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
  }

  public String getMobileNetworkCode() {
    final String networkOperator = telephonyManager.getNetworkOperator();
    return networkOperator == null ? ""
        : networkOperator.substring(codePortionLength(networkOperator));
  }

  public String getSimCountryISO() {
    return telephonyManager.getSimCountryIso();
  }
}
