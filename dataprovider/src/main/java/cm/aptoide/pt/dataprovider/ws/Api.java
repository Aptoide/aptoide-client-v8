/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/04/2016.
 */

package cm.aptoide.pt.dataprovider.ws;

import cm.aptoide.pt.dataprovider.util.SystemUtils;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;

/**
 * Created by neuro on 21-04-2016.
 */
public class Api {

	public static final String LANG = SystemUtils.getCountryCode();
	public static final String Q = SystemUtils.filters();
	public static final Boolean MATURE = ManagerPreferences.getMatureFilter();
}
