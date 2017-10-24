package cm.aptoide.pt;

public interface ApplicationPreferences {
  String getCachePath();

  boolean hasMultiStoreSearch();

  String getDefaultStoreName();

  String getMarketName();

  String getFeedbackEmail();

  String getImageCachePath();

  String getAccountType();

  String getAutoUpdateUrl();

  String getPartnerId();

  String getExtraId();

  String getDefaultThemeName();

  boolean isCreateStoreUserPrivacyEnabled();
}
