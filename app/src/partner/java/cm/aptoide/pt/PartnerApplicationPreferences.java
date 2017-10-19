package cm.aptoide.pt;

import android.os.Environment;
import cm.aptoide.pt.remotebootconfig.datamodel.BootConfig;

class PartnerApplicationPreferences implements ApplicationPreferences {

  private final BootConfig bootConfig;

  PartnerApplicationPreferences(BootConfig bootConfig) {
    this.bootConfig = bootConfig;
  }

  public BootConfig getBootConfig() {
    return bootConfig;
  }

  @Override public String getCachePath() {
    return Environment.getExternalStorageDirectory().getAbsolutePath()
        + "/."
        + getDefaultStore()
        + "/";
  }

  @Override public boolean hasMultiStoreSearch() {
    return getBootConfig().getPartner().getSwitches().getOptions().getMultistore().isSearch();
  }

  @Override public String getDefaultStore() {
    return getBootConfig().getPartner().getStore().getName();
  }

  @Override public String getMarketName() {
    return getBootConfig().getPartner().getStore().getLabel();
  }

  @Override public String getFeedbackEmail() {
    return getBootConfig().getPartner().getFeedback().getEmail();
  }

  @Override public String getImageCachePath() {
    return getCachePath() + "/" + "icons/";
  }

  @Override public String getAccountType() {
    return BuildConfig.APPLICATION_ID;
  }

  @Override public String getAutoUpdateUrl() {
    return "http://imgs.aptoide.com/latest_version_" + getDefaultStore() + ".xml";
  }

  @Override public String getPartnerId() {
    return String.valueOf(getBootConfig().getPartner().getUid());
  }

  @Override public String getExtraId() {
    return String.valueOf(getBootConfig().getPartner().getUid());
  }

  @Override public String getDefaultTheme() {
    return getBootConfig().getPartner().getAppearance().getTheme();
  }

  @Override public boolean isCreateStoreUserPrivacyEnabled() {
    return false;
  }
}
