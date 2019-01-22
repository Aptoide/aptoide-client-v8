package cm.aptoide.pt.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.FlavourActivityModule;
import cm.aptoide.pt.presenter.View;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import javax.inject.Inject;
import javax.inject.Named;

public abstract class BaseActivity extends RxAppCompatActivity {

  @Inject @Named("aptoide-theme") String theme;
  private ActivityComponent activityComponent;
  private boolean firstCreated;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    firstCreated = savedInstanceState == null;
    getActivityComponent().inject(this);
    ThemeUtils.setStatusBarThemeColor(this, theme);
    ThemeUtils.setAptoideTheme(this, theme);
  }

  @Override protected void onDestroy() {
    activityComponent = null;
    super.onDestroy();
  }

  public ActivityComponent getActivityComponent() {
    if (activityComponent == null) {
      AptoideApplication aptoideApplication = ((AptoideApplication) getApplication());
      activityComponent = aptoideApplication.getApplicationComponent()
          .plus(aptoideApplication.getActivityModule(this, getIntent(),
              aptoideApplication.getNotificationSyncScheduler(), (View) this, firstCreated,
              BuildConfig.APPLICATION_ID + ".provider"), new FlavourActivityModule());
    }
    return activityComponent;
  }
}
