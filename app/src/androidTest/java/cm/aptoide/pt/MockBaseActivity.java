package cm.aptoide.pt;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.ActivityComponent;
import cm.aptoide.pt.view.BaseActivity;

/**
 * Created by jose_messejana on 12-12-2017.
 */

public class MockBaseActivity extends BaseActivity {
  private ActivityComponent activityComponent;
  private boolean firstCreated;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    firstCreated = savedInstanceState == null;
  }

  @Override public ActivityComponent getActivityComponent() {
    if (activityComponent == null) {
      activityComponent = ((VanillaApplication) getApplication()).getApplicationComponent()
          .plus(new MockActivityModule(this, getIntent(),
              ((AptoideApplication) getApplication()).getNotificationSyncScheduler(),
              ((AptoideApplication) getApplication()).getMarketName(),
              ((AptoideApplication) getApplication()).getAutoUpdateUrl(), (View) this,
              ((AptoideApplication) getApplication()).getDefaultThemeName(),
              ((AptoideApplication) getApplication()).getDefaultStoreName(), firstCreated,
              BuildConfig.APPLICATION_ID + ".provider"));
    }
    return activityComponent;
  }
}
