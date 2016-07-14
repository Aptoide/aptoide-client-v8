/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 07/07/2016.
 */

package cm.aptoide.pt.preferences.managed;

import android.preference.PreferenceManager;

import cm.aptoide.pt.preferences.Application;

/**
 * Created by neuro on 21-04-2016.
 */
public class ManagerPreferences {

	public static boolean getHWSpecsFilter() {
		return Preferences.get().getBoolean(ManagedKeys.HWSPECS_FILTER, true);
	}

	public static boolean getAnimationsEnabledStatus() {
		return PreferenceManager.getDefaultSharedPreferences(Application.getContext()).getBoolean(ManagedKeys.ANIMATIONS_ENABLED, true);
	}
}
