/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 12/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws;

import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.SystemUtils;

/**
 * Created by neuro on 21-04-2016.
 */
public class Api {

	public static final String LANG = SystemUtils.getCountryCode();
	public static final String Q = SystemUtils.filters(ManagerPreferences.getHWSpecsFilter());
	public static final Boolean MATURE = ManagerPreferences.getMatureFilter();
}
