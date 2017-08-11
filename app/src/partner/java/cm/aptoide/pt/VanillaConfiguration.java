package cm.aptoide.pt;

import android.content.SharedPreferences;
import android.os.Environment;
import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.remotebootconfig.datamodel.BootConfig;
import cm.aptoide.pt.store.StoreTheme;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by diogoloureiro on 11/08/2017.
 *
 * App Configurations partners dimension implementation
 */

public class VanillaConfiguration implements AptoidePreferencesConfiguration {

  @Setter @Getter private BootConfig bootConfig;
  private static final String PATH_SDCARD =
      Environment.getExternalStorageDirectory().getAbsolutePath();
  private static String PATH_CACHE;
  private static final String PATH_CACHE_APKS = PATH_CACHE + "apks/";
  private static final String PATH_CACHE_IMAGES = PATH_CACHE + "icons/";
  private static final String PATH_CACHE_USER_AVATAR = PATH_CACHE + "user_avatar/";
  private static final String APP_ID = BuildConfig.APPLICATION_ID;
  private static String AUTO_UPDATE_URL;
  private static String DEFAULT_STORE;
  private final SharedPreferences sharedPreferences;

  VanillaConfiguration(SharedPreferences sharedPreferences, BootConfig bootConfig) {
    this.bootConfig = bootConfig;
    this.sharedPreferences = sharedPreferences;
    DEFAULT_STORE = bootConfig.getPartner().getStore().getName();
    PATH_CACHE = PATH_SDCARD + "/." + DEFAULT_STORE + "/";
    AUTO_UPDATE_URL = "http://imgs.aptoide.com/latest_version_" + DEFAULT_STORE + ".xml";
  }

  @Override public String getAppId() {
    return APP_ID.replace(".dev", "").replace(".internal", "");
  }

  @Override public String getCachePath() {
    return PATH_CACHE;
  }

  @Override public String getApkCachePath() {
    return PATH_CACHE_APKS;
  }

  @Override public String getUserAvatarCachePath() {
    return PATH_CACHE_USER_AVATAR;
  }

  @Override public String getImagesCachePath() {
    return PATH_CACHE_IMAGES;
  }

  @Override public String getAccountType() {
    return APP_ID.replace(".dev", "").replace(".internal", "");
  }

  @Override public String getAutoUpdateUrl() {
    return AUTO_UPDATE_URL;
  }

  @Override public String getMarketName() {
    return bootConfig.getPartner().getStore().getLabel();
  }

  @Override public int getIcon() {
    return R.mipmap.ic_launcher;
  }

  @Override public String getDefaultStore() {
    return DEFAULT_STORE;
  }

  @Override public String getContentAuthority() {
    return BuildConfig.CONTENT_AUTHORITY;
  }

  @Override public String getSearchAuthority() {
    return APP_ID + ".SearchSuggestionProvider";
  }

  @Override public String getAutoUpdatesSyncAdapterAuthority() {
    return APP_ID + ".AutoUpdateProvider";
  }

  @Override public String getTimelineActivitySyncAdapterAuthority() {
    return APP_ID + ".TimelineActivity";
  }

  @Override public String getTimeLinePostsSyncAdapterAuthority() {
    return APP_ID + ".TimelinePosts";
  }

  @Override public Class<?> getPushNotificationReceiverClass() {
    throw new IllegalArgumentException(
        "getPushNotificationReceiverClass not implemented " + "yet!");
  }

  @Override public String getPartnerId() {
    return String.valueOf(bootConfig.getPartner().getUid());
  }

  @Override public String getExtraId() {
    return String.valueOf(bootConfig.getPartner().getUid());
  }

  @Override public boolean isAlwaysUpdate() {
    return ManagerPreferences.isAllwaysUpdate(sharedPreferences);
  }

  @Override public String getDefaultTheme() {
    return bootConfig.getPartner().getAppearance().getTheme();
  }

  @Override public int getDefaultThemeRes() {
    return StoreTheme.get(getDefaultTheme()).getThemeResource();
  }

  @Override public String getFeedbackEmail() {
    return bootConfig.getPartner().getFeedback().getEmail();
  }

  @Override public boolean isLoginAvailable(SocialLogin loginType) {
    switch (loginType) {
      case FACEBOOK:
        return bootConfig.getPartner().getSocial().getLogin().isFacebook();
      case GOOGLE:
    }
    return false;
  }

  @Override public String getPartnerDimension() {
    return bootConfig.getPartner().getStore().getName();
  }

  @Override public String getVerticalDimension() {
    return bootConfig.getPartner().getType();
  }

  @Override public boolean isCreateStoreAndSetUserPrivacyAvailable() {
    return false;
  }

  @Override public String getVersionName() {
    return BuildConfig.VERSION_NAME;
  }
}
