package cm.aptoide.pt;

import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;
import cm.aptoide.pt.remotebootconfig.BootConfigJSONUtils;

public class PartnerApplication extends AptoideApplication {

  @Override public AptoidePreferencesConfiguration createConfiguration() {
    return new VanillaConfiguration(getDefaultSharedPreferences(),
        BootConfigJSONUtils.getSavedRemoteBootConfig(getBaseContext())
            .getData());
  }
}