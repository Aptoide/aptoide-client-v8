/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 24/06/2016.
 */

package cm.aptoide.pt.preferences;

import android.support.annotation.DrawableRes;

/**
 * Created by neuro on 10-05-2016.
 */
public interface AptoidePreferencesConfiguration {

  String getAppId();

  // Cache
  String getCachePath();

  String getApkCachePath();

  String getUserAvatarCachePath();

  String getImagesCachePath();

  // Account
  String getAccountType();

  String getAutoUpdateUrl();

  // Market
  String getMarketName();

  @DrawableRes int getIcon();

  String getDefaultStore();

  // Providers
  String getContentAuthority();

  String getSearchAuthority();

  String getAutoUpdatesSyncAdapterAuthority();

  // Authorities
  String getTimelineActivitySyncAdapterAuthority();

  String getTimeLinePostsSyncAdapterAuthority();

  // Classes
  Class<?> getPushNotificationReceiverClass();

  // Partners

  /**
   * @return partner id. null for vanilla.
   */
  String getPartnerId();

  //OEM extra id
  String getExtraId();

  boolean isAlwaysUpdate();

  String getDefaultTheme();

  int getDefaultThemeRes();

  String getFeedbackEmail();

  boolean isLoginAvailable(SocialLogin login);

  String getPartnerDimension();

  String getVerticalDimension();

  /**
   * @return true to show create store and user timeline privacy configurations
   */
  boolean isCreateStoreAndSetUserPrivacyAvailable();

  enum SocialLogin {
    FACEBOOK, GOOGLE
  }
}
