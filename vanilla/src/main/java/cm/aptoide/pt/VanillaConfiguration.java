/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 24/06/2016.
 */

package cm.aptoide.pt;

import android.os.Environment;
import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.v8engine.store.StoreThemeEnum;

/**
 * Created by neuro on 10-05-2016.
 */
public class VanillaConfiguration implements AptoidePreferencesConfiguration {

  private static final String PATH_SDCARD = Environment.getExternalStorageDirectory()
      .getAbsolutePath();
  private static final String PATH_CACHE = PATH_SDCARD + "/.aptoide/";
  private static final String PATH_CACHE_APKS = PATH_CACHE + "apks/";
  private static final String PATH_CACHE_IMAGES = PATH_CACHE + "icons/";
  private static final String PATH_CACHE_USER_AVATAR = PATH_CACHE + "user_avatar/";
  private static final String APP_ID = BuildConfig.APPLICATION_ID;
  private static final String AUTO_UPDATE_URL = "http://imgs.aptoide.com/latest_version_v8.xml";
  private static final String MARKETNAME = "Aptoide";
  private static final String DEFAULT_STORE = "apps";
  private static final String FEEDBACK_EMAIL = "support@aptoide.com";

  @Override public String getAppId() {
    return APP_ID;
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
    return APP_ID;
  }

  @Override public String getAutoUpdateUrl() {
    return AUTO_UPDATE_URL;
  }

  @Override public String getMarketName() {
    return MARKETNAME;
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
    return null;
  }

  @Override public String getExtraId() {
    return null;
  }

  @Override public boolean isAlwaysUpdate() {
    return ManagerPreferences.isAllwaysUpdate();
  }

  @Override public String getDefaultTheme() {
    return "default";
  }

  @Override public int getDefaultThemeRes() {
    return StoreThemeEnum.get(getDefaultTheme())
        .getThemeResource();
  }

  @Override public String getFeedbackEmail() {
    return FEEDBACK_EMAIL;
  }

  @Override public boolean isLoginAvailable(SocialLogin loginType) {
    switch (loginType) {
      case FACEBOOK:
      case GOOGLE:
        return true;
    }
    return false;
  }

  @Override public String getPartnerDimension() {
    return "vanilla";
  }

  @Override public String getVerticalDimension() {
    return "smartphone";
  }

  @Override public boolean isCreateStoreAndSetUserPrivacyAvailable() {
    return true;
  }
}
