package cm.aptoide.pt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import cm.aptoide.pt.notification.NotificationSyncScheduler;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.ActivityComponent;
import cm.aptoide.pt.view.ActivityComponentTest;
import cm.aptoide.pt.view.ActivityModuleTest;
import cm.aptoide.pt.view.FragmentComponentTest;
import cm.aptoide.pt.view.FragmentModuleTest;

/**
 * Created by jose_messejana on 13-11-2017.
 */

public class ComponentFactory {

  public static ApplicationComponentTest createApplicationComponent(AptoideApplication context) {
    return DaggerApplicationComponentTest.builder()
        .applicationModuleTest(
            new ApplicationModuleTest(context, context.getImageCachePath(), context.getCachePath(),
                context.getAccountType(), context.getPartnerId(), context.getMarketName(),
                context.getExtraId(), context.getAptoidePackage(), context.getAptoideMd5sum(),
                context.getLoginPreferences()))
        .build();
  }

  public static FragmentComponentTest createFragmentComponent(ActivityComponent activityComponent, Fragment fragment, Bundle savedInstance, boolean dismissToNavigateToMainView, boolean navigateToHome, boolean goToHome,
      boolean isEditProfile, boolean isCreateStoreUserPrivacyEnabled, String packageName){
      return ((ActivityComponentTest) activityComponent).plus(new FragmentModuleTest(fragment, savedInstance, dismissToNavigateToMainView, navigateToHome, goToHome, isEditProfile,
          isCreateStoreUserPrivacyEnabled, packageName));
  }

  public static ActivityComponentTest createActivityComponent(ApplicationComponent applicationComponent, AppCompatActivity activity, Intent intent,
      NotificationSyncScheduler notificationSyncScheduler, String marketName,
      String autoUpdateUrl, View view, String defaultTheme, String defaultStoreName,
      boolean firstCreated, String fileProviderAuthority){
    return ((ApplicationComponentTest) applicationComponent).plus(new ActivityModuleTest(activity,intent, notificationSyncScheduler,marketName, autoUpdateUrl, view,defaultTheme,
        defaultStoreName,firstCreated,fileProviderAuthority));
  }
}
