package cm.aptoide.pt;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import cm.aptoide.pt.view.ActivityComponent;
import cm.aptoide.pt.view.FragmentComponentTest;
import cm.aptoide.pt.view.FragmentModuleTest;

/**
 * Created by jose_messejana on 13-11-2017.
 */

public class ComponentFactory {

  public static ApplicationComponentTest create(AptoideApplication context) {
    return DaggerApplicationComponentTest.builder()
        .applicationModuleTest(
            new ApplicationModuleTest(context, context.getImageCachePath(), context.getCachePath(),
                context.getAccountType(), context.getPartnerId(), context.getMarketName(),
                context.getExtraId(), context.getAptoidePackage(), context.getAptoideMd5sum(),
                context.getLoginPreferences()))
        .build();
  }

  public static FragmentComponentTest create(ActivityComponent activityComponent, Fragment fragment, Bundle savedInstance, boolean dismissToNavigateToMainView, boolean navigateToHome, boolean goToHome,
      boolean isEditProfile, boolean isCreateStoreUserPrivacyEnabled, String packageName){
      return activityComponent.plus(new FragmentModuleTest(fragment, savedInstance, dismissToNavigateToMainView, navigateToHome, goToHome, isEditProfile,
          isCreateStoreUserPrivacyEnabled, packageName));
  }
}
