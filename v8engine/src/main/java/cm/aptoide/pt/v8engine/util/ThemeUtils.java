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
		/*if(theme.equals("lightsky")) {
			activity.setTheme(R.style.AptoideThemeDefaultLightblue);
		} else if(theme.equals("happyblue")) {
			activity.setTheme(R.style.AptoideThemeDefaultLightblue);
		}else if(theme.equals("seagreen")) {
			activity.setTheme(R.style.AptoideThemeDefaultGreen);
		}else if(theme.equals("green")) {
			activity.setTheme(R.style.AptoideThemeDefaultGreen);
		}else if(theme.equals("slategray")) {
			activity.setTheme(R.style.AptoideThemeDefaultTeal);
		}else if(theme.equals("teal")) {
			activity.setTheme(R.style.AptoideThemeDefaultTeal);
		}else if(theme.equals("red")) {
			activity.setTheme(R.style.AptoideThemeDefaultRed);
		}else if(theme.equals("blue")) {
			activity.setTheme(R.style.AptoideThemeDefaultIndigo);
		}else if(theme.equals("indigo")) {
			activity.setTheme(R.style.AptoideThemeDefaultIndigo);
		}else if(theme.equals("pink")) {
			activity.setTheme(R.style.AptoideThemeDefaultPink);
		}else if(theme.equals("orange")) {
			activity.setTheme(R.style.AptoideThemeDefaultOrange);
		}else if(theme.equals("maroon")) {
			activity.setTheme(R.style.AptoideThemeDefaultMaroon);
		}else if(theme.equals("brown")) {
			activity.setTheme(R.style.AptoideThemeDefaultBrown);
		}else if(theme.equals("midnight")) {
			activity.setTheme(R.style.AptoideThemeDefaultBluegrey);
		}else if(theme.equals("bluegrey")) {
			activity.setTheme(R.style.AptoideThemeDefaultBluegrey);
		}else if(theme.equals("silver")) {
			activity.setTheme(R.style.AptoideThemeDefaultGrey);
		}else if(theme.equals("dimgrey")) {
			activity.setTheme(R.style.AptoideThemeDefaultGrey);
		}else if(theme.equals("grey")) {
			activity.setTheme(R.style.AptoideThemeDefaultGrey);
		}else if(theme.equals("black")) {
			activity.setTheme(R.style.AptoideThemeDefaultBlack);
		}else if(theme.equals("magenta")) {
			activity.setTheme(R.style.AptoideThemeDefaultDeepPurple);
		}else if(theme.equals("deeppurple")) {
			activity.setTheme(R.style.AptoideThemeDefaultDeepPurple);
		}else if(theme.equals("gold")) {
			activity.setTheme(R.style.AptoideThemeDefaultAmber);
		}else if(theme.equals("yellow")) {
			activity.setTheme(R.style.AptoideThemeDefaultAmber);
		}else if(theme.equals("amber")) {
			activity.setTheme(R.style.AptoideThemeDefaultAmber);
		}else if(theme.equals("springgreen")) {
			activity.setTheme(R.style.AptoideThemeDefaultLightgreen);
		}else if(theme.equals("lightgreen")) {
			activity.setTheme(R.style.AptoideThemeDefaultLightgreen);
		}else if(theme.equals("greenapple")) {
			activity.setTheme(R.style.AptoideThemeDefaultLime);
		}else if(theme.equals("amber")) {
			activity.setTheme(R.style.AptoideThemeDefaultAmber);
		}else if(theme.equals("amber")) {
			activity.setTheme(R.style.AptoideThemeDefaultAmber);
		} else {
			activity.setTheme(R.style.AptoideThemeDefault);
		}*/
	}
	
}
