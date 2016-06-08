/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/05/2016.
 */

package cm.aptoide.pt.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

/**
 * Created by trinkes on 5/9/16.
 */
public class ShowMessage {

	public static void show(View view, String msg) {
		Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
		//Toast.makeText(view.getContext(), msg, Toast.LENGTH_SHORT).show();
	}

	public static void show(View view, @StringRes int msg) {
		Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
		//Toast.makeText(view.getContext(), msg, Toast.LENGTH_SHORT).show();
	}

	public static void toast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public static void toast(Context context, @StringRes int msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
}
