package cm.aptoide.pt;

import cm.aptoide.pt.account.LoginPreferences;
import cm.aptoide.pt.remotebootconfig.BootConfigJSONUtils;
import cm.aptoide.pt.remotebootconfig.datamodel.BootConfig;
import cm.aptoide.pt.remotebootconfig.datamodel.RemoteBootConfig;
import cm.aptoide.pt.view.configuration.FragmentProvider;
import cm.aptoide.pt.view.configuration.implementation.PartnerFragmentProvider;
import rx.Completable;
import rx.Single;

public class PartnerApplication extends AptoideApplication {

  public BootConfig getBootConfig() {
    return getApplicationPreferences().getBootConfig();
  }

  public void setRemoteBootConfig(RemoteBootConfig remoteBootConfig) {
    BootConfigJSONUtils.saveRemoteBootConfig(getBaseContext(), remoteBootConfig,
        "support@aptoide.com");
    getApplicationPreferences().setBootConfig(remoteBootConfig.getData());
  }

  @Override public Completable createShortcut() {
    return Single.just(getApplicationPreferences())
        .map(PartnerApplicationPreferences::getBootConfig)
        .flatMapCompletable(bootConfig -> {
          if (bootConfig.getPartner()
              .getSwitches()
              .getOptions()
              .isShortcut()) {
            return super.createShortcut();
          } else {
            return Completable.complete();
          }
        });
  }

  @Override public LoginPreferences getLoginPreferences() {
    return new LoginPreferences(getBootConfig());
  }

  @Override public PartnerApplicationPreferences getApplicationPreferences() {
    final BootConfig bootConfig = BootConfigJSONUtils.getSavedRemoteBootConfig(getBaseContext())
        .getData();
    return new PartnerApplicationPreferences(bootConfig);
  }

  @Override public FragmentProvider createFragmentProvider() {
    final ApplicationPreferences appPreferences = getApplicationPreferences();
    return new PartnerFragmentProvider(appPreferences.getDefaultThemeName(),
        appPreferences.getDefaultStoreName(), appPreferences.hasMultiStoreSearch());
  }
}
