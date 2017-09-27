package cm.aptoide.pt;

import android.os.Environment;
import cm.aptoide.pt.account.LoginPreferences;
import cm.aptoide.pt.remotebootconfig.BootConfigJSONUtils;
import cm.aptoide.pt.remotebootconfig.datamodel.BootConfig;
import cm.aptoide.pt.remotebootconfig.datamodel.RemoteBootConfig;
import cm.aptoide.pt.view.FragmentProvider;
import cm.aptoide.pt.view.configuration.implementation.PartnerFragmentProvider;
import rx.Completable;
import rx.Single;

public class PartnerApplication extends AptoideApplication {

  private BootConfig bootConfig;

  public void setRemoteBootConfig(RemoteBootConfig remoteBootConfig) {
    BootConfigJSONUtils.saveRemoteBootConfig(getBaseContext(), remoteBootConfig,
        "support@aptoide.com");
    setBootConfig(remoteBootConfig.getData());
  }

  public BootConfig getBootConfig() {
    if (bootConfig == null) {
      bootConfig = BootConfigJSONUtils.getSavedRemoteBootConfig(getBaseContext())
          .getData();
    }
    return bootConfig;
  }

  public void setBootConfig(BootConfig bootConfig) {
    this.bootConfig = bootConfig;
  }

  @Override public String getCachePath() {
    return Environment.getExternalStorageDirectory()
        .getAbsolutePath() + "/." + getDefaultStoreName() + "/";
  }

  @Override public boolean hasMultiStoreSearch() {
    return getBootConfig().getPartner()
        .getSwitches()
        .getOptions()
        .getMultistore()
        .isSearch();
  }

  @Override public String getDefaultStoreName() {
    return getBootConfig().getPartner()
        .getStore()
        .getName();
  }

  @Override public String getMarketName() {
    return getBootConfig().getPartner()
        .getStore()
        .getLabel();
  }

  @Override public String getFeedbackEmail() {
    return getBootConfig().getPartner()
        .getFeedback()
        .getEmail();
  }

  @Override public String getImageCachePath() {
    return getCachePath() + "/" + "icons/";
  }

  @Override public String getAccountType() {
    return BuildConfig.APPLICATION_ID;
  }

  @Override public String getAutoUpdateUrl() {
    return "http://imgs.aptoide.com/latest_version_" + getDefaultStoreName() + ".xml";
  }

  @Override public String getPartnerId() {
    return String.valueOf(getBootConfig().getPartner()
        .getUid());
  }

  @Override public String getExtraId() {
    return String.valueOf(getBootConfig().getPartner()
        .getUid());
  }

  @Override public String getDefaultThemeName() {
    return getBootConfig().getPartner()
        .getAppearance()
        .getTheme();
  }

  @Override public boolean isCreateStoreUserPrivacyEnabled() {
    return false;
  }

  @Override public Completable createShortcut() {
    return Single.just(getBootConfig())
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

  @Override public FragmentProvider createFragmentProvider() {
    return new PartnerFragmentProvider(getDefaultThemeName(), getDefaultStoreName(),
        hasMultiStoreSearch());
  }
}
