package cm.aptoide.pt;

import cm.aptoide.pt.account.LoginPreferences;
import cm.aptoide.pt.remotebootconfig.BootConfigJSONUtils;
import cm.aptoide.pt.remotebootconfig.datamodel.BootConfig;
import cm.aptoide.pt.remotebootconfig.datamodel.RemoteBootConfig;
import cm.aptoide.pt.view.configuration.FragmentProvider;
import cm.aptoide.pt.view.configuration.implementation.PartnerFragmentProvider;
import rx.Completable;

public class PartnerApplication extends AptoideApplication {

  private BootConfig bootConfig;

  public BootConfig getBootConfig() {
    if (bootConfig == null) {
      bootConfig = BootConfigJSONUtils.getSavedRemoteBootConfig(getBaseContext())
          .getData();
    }
    return bootConfig;
  }

  public void setRemoteBootConfig(RemoteBootConfig remoteBootConfig) {
    BootConfigJSONUtils.saveRemoteBootConfig(getBaseContext(), remoteBootConfig,
        "support@aptoide.com");
    this.bootConfig = remoteBootConfig.getData();
  }

  @Override public Completable createShortcut() {
    if (bootConfig.getPartner()
        .getSwitches()
        .getOptions()
        .isShortcut()) {
      return super.createShortcut();
    } else {
      return null;
    }
  }

  @Override public LoginPreferences getLoginPreferences() {
    return new LoginPreferences(getBootConfig());
  }

  @Override public ApplicationPreferences getApplicationPreferences() {
    return new PartnerApplicationPreferences(getBootConfig());
  }

  @Override public FragmentProvider createFragmentProvider() {
    final ApplicationPreferences appPreferences = getApplicationPreferences();
    return new PartnerFragmentProvider(appPreferences.getDefaultTheme(),
        appPreferences.getDefaultStore(), appPreferences.hasMultiStoreSearch());
  }
}
