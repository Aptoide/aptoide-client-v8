/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws;

import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.q.QManager;
import lombok.Getter;

/**
 * Created by neuro on 21-04-2016.
 */
public class Api {

  @Getter private static final String lang;
  private static final QManager qManager;

  static {
    qManager = QManager.getInstance();

    lang = AptoideUtils.SystemU.getCountryCode();
  }

  public static String getQ() {
    return qManager.getFilters(ManagerPreferences.getHWSpecsFilter());
  }
}
