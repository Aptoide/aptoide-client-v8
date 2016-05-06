/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/05/2016.
 */

package cm.aptoide.pt.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by neuro on 14-04-2016.
 */
public class ScreenUtils {

	private static ScreenUtilsCache screenWidthInDipCache = new ScreenUtilsCache();

	public static int getCurrentOrientation(final Context context) {
		return context.getResources().getConfiguration().orientation;
	}

	public static float getScreenWidthInDip(final Context context) {
		if (getCurrentOrientation(context) != screenWidthInDipCache.orientation) {
			WindowManager wm = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
			DisplayMetrics dm = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(dm);
			screenWidthInDipCache.set(getCurrentOrientation(context), dm.widthPixels / dm.density);
		}

		return screenWidthInDipCache.value;
	}

	private static class ScreenUtilsCache {

		private int orientation = -1;
		private float value;

		public void set(int currentOrientation, float value) {
			this.orientation = currentOrientation;
			this.value = value;
		}
	}
}

