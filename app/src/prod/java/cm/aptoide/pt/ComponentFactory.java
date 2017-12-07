package cm.aptoide.pt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import cm.aptoide.pt.notification.NotificationSyncScheduler;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.ActivityComponent;
import cm.aptoide.pt.view.ActivityModule;
import cm.aptoide.pt.view.FragmentComponent;
import cm.aptoide.pt.view.FragmentModule;

/**
 * Created by jose_messejana on 13-11-2017.
 */

public class ComponentFactory {

  public static ApplicationComponent createApplicationComponent(AptoideApplication context) {
    return DaggerApplicationComponent.builder()
        .applicationModule(new ApplicationModule(context, context.getImageCachePath(), context.getCachePath(),
            context.getAccountType(), context.getPartnerId(), context.getMarketName(), context.getExtraId(), context.getAptoidePackage(),
            context.getAptoideMd5sum(), context.getLoginPreferences()))
        .build();
  }

  public static FragmentComponent createFragmentComponent(ActivityComponent activityComponent, Fragment fragment,
      Bundle savedInstance, boolean dismissToNavigateToMainView, boolean navigateToHome, boolean goToHome,
      boolean isEditProfile, boolean isCreateStoreUserPrivacyEnabled, String packageName) {
    return activityComponent.plus(
        new FragmentModule(fragment, savedInstance, dismissToNavigateToMainView, navigateToHome,
            goToHome, isEditProfile, isCreateStoreUserPrivacyEnabled, packageName));
  }

  public static ActivityComponent createActivityComponent(ApplicationComponent applicationComponent,
      AppCompatActivity activity, Intent intent,
      NotificationSyncScheduler notificationSyncScheduler, String marketName, String autoUpdateUrl,
      View view, String defaultTheme, String defaultStoreName, boolean firstCreated,
      String fileProviderAuthority) {
    return applicationComponent.plus(
        new ActivityModule(activity, intent, notificationSyncScheduler, marketName, autoUpdateUrl,
            view, defaultTheme, defaultStoreName, firstCreated, fileProviderAuthority));
  }
}
