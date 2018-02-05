package cm.aptoide.pt;

import android.content.Intent;
import android.os.Bundle;
import cm.aptoide.pt.notification.NotificationSyncScheduler;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.ActivityModule;
import cm.aptoide.pt.view.BaseActivity;
import cm.aptoide.pt.view.BaseFragment;
import cm.aptoide.pt.view.FragmentModule;
import cm.aptoide.pt.view.MockActivityModule;
import cm.aptoide.pt.view.MockFragmentModule;

public class MockAptoideApplication extends VanillaApplication {

  private ApplicationComponent applicationComponent;

  @Override public ApplicationComponent getApplicationComponent() {
    if (applicationComponent == null) {
      applicationComponent = DaggerApplicationComponent.builder()
          .applicationModule(
              new MockApplicationModule(this,
                  getAptoideMd5sum()))
          .flavourApplicationModule(
              new FlavourApplicationModule(this)
          )
          .build();
    }
    return applicationComponent;
  }

  @Override public ActivityModule getActivityModule(BaseActivity activity, Intent intent,
      NotificationSyncScheduler notificationSyncScheduler, String marketName, String autoUpdateUrl,
      View view, String defaultThemeName, String defaultStoreName, boolean firstCreated, String s) {

    return new MockActivityModule(activity, intent, notificationSyncScheduler, marketName,
        autoUpdateUrl, view, defaultThemeName, defaultStoreName, firstCreated, s);
  }

  @Override
  public FragmentModule getFragmentModule(BaseFragment baseFragment, Bundle savedInstanceState,
      Bundle arguments, boolean createStoreUserPrivacyEnabled, String packageName) {
    return new MockFragmentModule(baseFragment, savedInstanceState, arguments,
        createStoreUserPrivacyEnabled, packageName);
  }
}