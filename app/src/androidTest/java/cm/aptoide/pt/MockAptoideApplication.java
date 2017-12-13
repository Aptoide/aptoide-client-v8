package cm.aptoide.pt;

import android.support.v7.app.AppCompatActivity;
import cm.aptoide.pt.presenter.View;

public class MockAptoideApplication extends VanillaApplication {

  private ApplicationComponent applicationComponent;
  private ActivityComponent activityComponent;

  @Override public ApplicationComponent getApplicationComponent() {
    if (applicationComponent == null) {
      applicationComponent = DaggerApplicationComponent.builder()
          .applicationModule(
              new MockApplicationModule(this, getImageCachePath(), getCachePath(), getAccountType(),
                  getPartnerId(), getMarketName(), getExtraId(), getAptoidePackage(),
                  getAptoideMd5sum(), getLoginPreferences()))
          .build();
    }
    return applicationComponent;
  }

  @Override public ActivityComponent getActivityComponent(AppCompatActivity appCompatActivity, boolean firstCreated){
    if (activityComponent == null) {
      activityComponent = getApplicationComponent()
          .plus(new MockActivityModule(appCompatActivity, appCompatActivity.getIntent(), getNotificationSyncScheduler(), getMarketName(),
              getAutoUpdateUrl(), (View) appCompatActivity, getDefaultThemeName(), getDefaultStoreName(), firstCreated,
              BuildConfig.APPLICATION_ID + ".provider"));
    }
    return activityComponent;
  }

  @Override public void destroyActivityComponent(){
    activityComponent = null;
  }

}