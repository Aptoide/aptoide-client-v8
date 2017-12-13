package cm.aptoide.pt.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.ActivityComponent;
import cm.aptoide.pt.AptoideApplication;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

public abstract class BaseActivity extends RxAppCompatActivity {

  private ActivityComponent activityComponent;
  private boolean firstCreated;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    firstCreated = savedInstanceState == null;
  }

  @Override protected void onDestroy() {
    ((AptoideApplication) getApplication()).destroyActivityComponent();
    super.onDestroy();
  }

  public ActivityComponent getActivityComponent() {
    if (activityComponent == null) {
      AptoideApplication aptoideApplication = ((AptoideApplication) getApplication());
      activityComponent  = aptoideApplication.getActivityComponent(this, firstCreated);
    }
    return activityComponent;
  }
}
