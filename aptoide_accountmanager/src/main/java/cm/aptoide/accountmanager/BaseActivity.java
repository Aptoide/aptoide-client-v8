package cm.aptoide.accountmanager;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import cm.aptoide.pt.preferences.Application;

/**
 * Created by trinkes on 4/18/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

  private static final String TAG = BaseActivity.class.getSimpleName();

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getActivityTitle());
    getTheme().applyStyle(Application.getConfiguration().getDefaultThemeRes(), true);
  }

  protected abstract String getActivityTitle();

  @LayoutRes abstract int getLayoutId();

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int i = item.getItemId();
    if (i == android.R.id.home || i == R.id.home || i == 0) {
      finish();
    }
    return super.onOptionsItemSelected(item);
  }
}
