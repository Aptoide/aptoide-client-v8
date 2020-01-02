package cm.aptoide.pt.view;

import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;
import cm.aptoide.pt.R;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.store.StoreTheme;

/**
 * Created by pedroribeiro on 23/06/16.
 */
public class ThemeUtils {

  public static final String DARK_THEME = "dark_theme";
  private static final String TAG = "ThemeUtils";

  /**
   * Responsible for changing status bar color
   */
  public static void setStatusBarThemeColor(Activity activity, String theme) {
    if (Build.VERSION.SDK_INT >= 21) {
      StoreTheme storeTheme = StoreTheme.get(theme);
      Window window = activity.getWindow();
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      window.setStatusBarColor(activity.getResources()
          .getColor(storeTheme.getDarkerColor()));
    }
  }

  /**
   * Used to set Default themes
   */
  public static void setAptoideTheme(Activity activity, String theme) {
    if (theme.equals(DARK_THEME)) {
      activity.setTheme(R.style.AptoideThemeDefaultDark);
      Logger.getInstance()
          .d(TAG, "Setting dark theme");
    } else {
      Logger.getInstance()
          .d(TAG, "Setting default light theme");
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
}
