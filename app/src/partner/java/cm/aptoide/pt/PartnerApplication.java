package cm.aptoide.pt;

import android.os.Environment;
import cm.aptoide.pt.account.LoginPreferences;
import cm.aptoide.pt.remotebootconfig.BootConfigJSONUtils;
import cm.aptoide.pt.remotebootconfig.datamodel.BootConfig;
import cm.aptoide.pt.remotebootconfig.datamodel.RemoteBootConfig;

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

  @Override public String getCachePath() {
    return Environment.getExternalStorageDirectory()
        .getAbsolutePath() + "/." + getDefaultStore() + "/";
  }

  @Override public String getDefaultStore() {
    return getBootConfig().getPartner()
        .getStore()
        .getName();
  }

  @Override public String getMarketName() {
    return getBootConfig().getPartner()
        .getStore()
        .getLabel();
  }

  @Override public LoginPreferences getLoginPreferences() {
    return new LoginPreferences(getBootConfig());
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
    return BuildConfig.APPLICATION_ID.replace(".dev", "")
        .replace(".internal", "");
  }

  @Override public String getAutoUpdateUrl() {
    return "http://imgs.aptoide.com/latest_version_" + getDefaultStore() + ".xml";
  }

  @Override public String getPartnerId() {
    return String.valueOf(getBootConfig().getPartner()
        .getUid());
  }

  @Override public String getExtraId() {
    return String.valueOf(getBootConfig().getPartner()
        .getUid());
  }

  @Override public String getDefaultTheme() {
    return getBootConfig().getPartner()
        .getAppearance()
        .getTheme();
  }

  @Override public boolean isCreateStoreUserPrivacyEnabled() {
    return false;
  }
}