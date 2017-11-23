package cm.aptoide.pt.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.presenter.View;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

public abstract class BaseActivity extends RxAppCompatActivity {

  private ActivityComponent activityComponent;
  private boolean firstCreated;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    firstCreated = savedInstanceState == null;
  }

  @Override protected void onDestroy() {
    activityComponent = null;
    super.onDestroy();
  }

  public ActivityComponent getActivityComponent() {
    if (activityComponent == null) {
      activityComponent = ((AptoideApplication) getApplication()).getApplicationComponent()
          .plus(new ActivityModule(this, getIntent(),
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
