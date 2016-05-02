/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/05/2016.
 */

package cm.aptoide.pt.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by neuro on 14-04-2016.
 */
public class ScreenUtils {

	public static int getCurrentOrientation(final Context context) {
		return context.getResources().getConfiguration().orientation;
	}

	public static float getScreenWidthInDip(final Context context) {
		WindowManager wm = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels / dm.density;
	}
}

