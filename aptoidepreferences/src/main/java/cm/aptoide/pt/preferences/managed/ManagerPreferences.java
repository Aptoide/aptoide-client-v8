/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/04/2016.
 */

package cm.aptoide.pt.preferences.managed;

/**
 * Created by neuro on 21-04-2016.
 */
public class ManagerPreferences {

	public static boolean getHWSpecsFilter() {
		return Preferences.get().getBoolean(ManagedKeys.HWSPECS_FILTER, true);
	}

	public static boolean getMatureFilter() {
		return Preferences.get().getBoolean(ManagedKeys.MATURE_CHECK_BOX, true);
	}
}
