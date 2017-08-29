/*
 * Copyright (c) 2016.
 * Modified on 01/07/2016.
 */

package cm.aptoide.pt;

import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;

public class VanillaApplication extends AptoideApplication {

  @Override public AptoidePreferencesConfiguration createConfiguration() {
    return new VanillaConfiguration(getDefaultSharedPreferences());
  }
}
