package cm.aptoide.accountmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MenuItem;

import java.util.Locale;

import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.LanguageUtils;

/**
 * Created by trinkes on 4/18/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

  protected static final int LOGGED_IN_SECOND_STEP_CODE = 126;
  private static final String TAG = BaseActivity.class.getSimpleName();

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    //If the user had previously chosen a language, the app is displayed in that language, if not, it starts with the device's language as default
    SharedPreferences sharedPreferences = getSharedPreferences("LANGUAGES_PREFERENCES", Context.MODE_PRIVATE);
    String lang = sharedPreferences.getString("Language", Locale.getDefault().toString());
    Resources res = getResources();
    DisplayMetrics dm = res.getDisplayMetrics();
    android.content.res.Configuration conf = res.getConfiguration();
    conf.locale = LanguageUtils.getLocaleFromString(lang);
    res.updateConfiguration(conf, dm);

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

  public enum UserAccessState {
    PUBLIC,
    PRIVATE,
    UNLISTED
  }
}
