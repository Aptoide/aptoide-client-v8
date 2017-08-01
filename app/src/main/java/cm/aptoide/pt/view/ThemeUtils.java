package cm.aptoide.pt.view;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;
import cm.aptoide.pt.V8Engine;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.R;

/**
 * Created by pedroribeiro on 23/06/16.
 */
public class ThemeUtils {

  /**
   * Responsible for changing status bar color
   */
  public static void setStatusBarThemeColor(Activity activity, StoreTheme storeTheme) {
    if (Build.VERSION.SDK_INT >= 21) {
      Window window = activity.getWindow();
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      window.setStatusBarColor(activity.getResources().getColor(storeTheme.getDarkerColor()));
    }
  }

  /**
   * Used to set Default themes
   */
  public static void setAptoideTheme(Activity activity) {

    SharedPreferences sPref =
        ((V8Engine) activity.getApplicationContext()).getDefaultSharedPreferences();

    if (sPref.getString("theme", "light").equals("dark")) {
      sPref.edit().putString("theme", "default_dark").apply();
      activity.setTheme(R.style.AptoideThemeDefaultDark);
    } else {
      sPref.edit().putString("theme", V8Engine.getConfiguration().getDefaultTheme()).apply();
      activity.setTheme(R.style.AptoideThemeDefault);
    }
  }

  /**
   * Sets Store themes
   */
  public static void setStoreTheme(Activity activity, String theme) {

    StoreTheme storeTheme = StoreTheme.get(theme);
    activity.setTheme(storeTheme.getThemeResource());
  }

  /**
   * Returns applied theme
   */
  public static StoreTheme getAppliedTheme(Activity activity) {
    return StoreTheme.get(activity.getTheme().toString());
  }
}
