/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 20/07/2016.
 */

package cm.aptoide.pt.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

/**
 * Created by trinkes on 5/9/16.
 */
public class ShowMessage {

	public static void asSnack(View view, String msg, String actionMsg, View.OnClickListener action) {
		Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).setAction(actionMsg, action).show();
	}

	public static void asSnack(View view, String msg) {
		Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
	}

	public static void asSnack(View view, @StringRes int msg, @StringRes int actionMsg, View.OnClickListener action) {
		Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).setAction(actionMsg, action).show();
	}

	public static void asSnack(View view, @StringRes int msg) {
		Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
	}

	public static void asSnack(Fragment fragment, String msg) {
		asSnack(fragment.getView(), msg);
	}

	public static void asToast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public static void asToast(Context context, @StringRes int msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

}
