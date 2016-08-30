/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 27/07/2016.
 */

package cm.aptoide.pt.dataprovider;

import android.telephony.TelephonyManager;

import lombok.AllArgsConstructor;

/**
 * Created by marcelobenites on 7/27/16.
 */
@AllArgsConstructor
public class NetworkOperatorManager {

	private final TelephonyManager telephonyManager;

	public String getMobileCountryCode() {
		final String networkOperator = telephonyManager.getNetworkOperator();
		return networkOperator == null ? "" : networkOperator.substring(0, mncPortionLength(networkOperator));

	}

	public boolean isSimStateReady() {
		return telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
	}

	public String getMobileNetworkCode() {
		final String networkOperator = telephonyManager.getNetworkOperator();
		return networkOperator == null ? "" : networkOperator.substring(mncPortionLength(networkOperator));
	}

	private int mncPortionLength(String networkOperator) {
		return Math.min(3, networkOperator.length());
	}

	public String getSimCountryISO() {
		return telephonyManager.getSimCountryIso();
	}
}
