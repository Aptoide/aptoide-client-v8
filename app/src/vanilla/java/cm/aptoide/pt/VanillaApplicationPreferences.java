package cm.aptoide.pt;

import android.os.Environment;

class VanillaApplicationPreferences implements ApplicationPreferences {

  @Override public String getCachePath() {
    return Environment.getExternalStorageDirectory()
        .getAbsolutePath() + "/.aptoide/";
  }

  @Override public boolean hasMultiStoreSearch() {
    return false;
  }

  @Override public String getDefaultStore() {
    return "apps";
  }

  @Override public String getMarketName() {
    return "Aptoide";
  }

  @Override public String getFeedbackEmail() {
    return "support@aptoide.com";
  }

  @Override public String getImageCachePath() {
    return getCachePath() + "icons/";
  }

  @Override public String getAccountType() {
    return BuildConfig.APPLICATION_ID;
  }

  @Override public String getAutoUpdateUrl() {
    return "http://imgs.aptoide.com/latest_version_v8.xml";
  }

  @Override public String getPartnerId() {
    return null;
  }

  @Override public String getExtraId() {
    return null;
  }

  @Override public String getDefaultTheme() {
    return "default";
  }

  @Override public boolean isCreateStoreUserPrivacyEnabled() {
    return true;
  }
}
