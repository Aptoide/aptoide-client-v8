/*
 * Copyright (c) 2016.
 * Modified on 01/07/2016.
 */

package cm.aptoide.pt;

import android.os.Environment;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.networking.Pnp1AuthorizationInterceptor;
import cm.aptoide.pt.notification.NotificationService;
import cm.aptoide.pt.notification.NotificationSyncScheduler;
import cm.aptoide.pt.notification.sync.NotificationSyncFactory;
import cm.aptoide.pt.notification.sync.NotificationSyncManager;
import cm.aptoide.pt.view.ActivityProvider;
import cm.aptoide.pt.view.FragmentProvider;
import cm.aptoide.pt.view.configuration.implementation.VanillaActivityProvider;
import cm.aptoide.pt.view.configuration.implementation.VanillaFragmentProvider;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;

public class VanillaApplication extends NotificationApplicationView {

  private NotificationSyncScheduler notificationSyncScheduler;

  @Override public String getCachePath() {
    return Environment.getExternalStorageDirectory()
        .getAbsolutePath() + "/.aptoide/";
  }

  @Override public String getFeedbackEmail() {
    return "support@aptoide.com";
  }

  @Override public String getAccountType() {
    return BuildConfig.APPLICATION_ID;
  }

  @Override public String getPartnerId() {
    return null;
  }

  @Override public String getExtraId() {
    return null;
  }

  @Override public boolean isCreateStoreUserPrivacyEnabled() {
    return true;
  }

  @Override public NotificationSyncScheduler getNotificationSyncScheduler() {
    if (notificationSyncScheduler == null) {
      notificationSyncScheduler = new NotificationSyncManager(getAlarmSyncScheduler(), true,
          new NotificationSyncFactory(new NotificationService(BuildConfig.APPLICATION_ID,
              new OkHttpClient.Builder().readTimeout(45, TimeUnit.SECONDS)
                  .writeTimeout(45, TimeUnit.SECONDS)
                  .addInterceptor(new Pnp1AuthorizationInterceptor(getAuthenticationPersistence(),
                      getTokenInvalidator()))
                  .build(), WebService.getDefaultConverter(), getIdsRepository(),
              BuildConfig.VERSION_NAME, getExtraId(), getDefaultSharedPreferences(), getResources(),
              getAccountManager()), getNotificationProvider()));
    }
    return notificationSyncScheduler;
  }

  @Override public FragmentProvider createFragmentProvider() {
    return new VanillaFragmentProvider();
  }

  @Override public ActivityProvider createActivityProvider() {
    return new VanillaActivityProvider();
  }
}