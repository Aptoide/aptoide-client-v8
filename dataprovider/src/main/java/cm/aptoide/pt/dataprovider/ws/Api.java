/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws;

import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;

/**
 * Created by neuro on 21-04-2016.
 */
public class Api {

  public static final String LANG = AptoideUtils.SystemU.getCountryCode();
  public static final String Q = AptoideUtils.Core.filters(ManagerPreferences.getHWSpecsFilter());
}
