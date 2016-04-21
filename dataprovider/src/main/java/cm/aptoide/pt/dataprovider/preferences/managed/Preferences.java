/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 21/04/2016.
 */

package cm.aptoide.pt.dataprovider.preferences.managed;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import cm.aptoide.pt.dataprovider.DataProvider;

/**
 * Created by neuro on 21-04-2016.
 */
class Preferences {

	private static final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(DataProvider.getContext());

	public static SharedPreferences get() {
		return preferences;
	}
}
