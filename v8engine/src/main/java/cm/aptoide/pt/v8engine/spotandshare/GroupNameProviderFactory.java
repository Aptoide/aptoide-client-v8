package cm.aptoide.pt.v8engine.spotandshare;

import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;

/**
 * Created by filipe on 06-04-2017.
 */

public abstract class GroupNameProviderFactory extends DataProvider {
  @Override protected TokenInvalidator getTokenInvalidator() {
    return null;
  }

  @Override public AptoidePreferencesConfiguration createConfiguration() {
    return null;
  }
}
