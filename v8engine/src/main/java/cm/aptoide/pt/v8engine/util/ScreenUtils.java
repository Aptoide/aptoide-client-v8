/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 16/04/2016.
 */

package cm.aptoide.pt.v8engine.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import cm.aptoide.pt.v8engine.Aptoide;

/**
 * Created by neuro on 14-04-2016.
 */
public class ScreenUtils {

	public static int getCurrentOrientation() {
		return Aptoide.getContext().getResources().getConfiguration().orientation;
	}

	public static float getScreenWidthInDip() {
		WindowManager wm = ((WindowManager) Aptoide.getContext().getSystemService(Context.WINDOW_SERVICE));
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels / dm.density;
	}
}

