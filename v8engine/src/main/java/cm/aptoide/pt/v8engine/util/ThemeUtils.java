package cm.aptoide.pt.v8engine.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;

import cm.aptoide.pt.v8engine.R;

/**
 * Created by pedroribeiro on 23/06/16.
 */
public class ThemeUtils {

	/**
	 * Responsible for changing status bar color
	 * @param activity
	 * @param storeThemeEnum
	 */
	public static void setStatusBarThemeColor(Activity activity, StoreThemeEnum storeThemeEnum) {
		if(Build.VERSION.SDK_INT >= 21) {
			Window window = activity.getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setStatusBarColor(activity.getResources().getColor(storeThemeEnum.getColor700tint()));
		}
	}

	/**
	 * Used to set Default themes
	 * @param activity
	 */
	public static void setAptoideTheme(Activity activity) {

		SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(activity);

		if(sPref.getString("theme", "light").equals("dark")){
			sPref.edit().putString("theme", "default_dark").commit();
			activity.setTheme(R.style.AptoideThemeDefaultDark);
		}else{
			sPref.edit().putString("theme", "default").commit();
			activity.setTheme(R.style.AptoideThemeDefault);
		}
	}

	/**
	 * Sets Store themes when opening a Store
	 * @param activity
	 * @param theme
	 */
	public static void setStoreTheme(Activity activity, String theme) {

		StoreThemeEnum storeTheme = StoreThemeEnum.get(theme);
		activity.setTheme(storeTheme.getThemeResource());

	}
	
}
