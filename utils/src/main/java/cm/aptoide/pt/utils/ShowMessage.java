/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 17/06/2016.
 */

package cm.aptoide.pt.utils;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

/**
 * Created by trinkes on 5/9/16.
 */
public class ShowMessage {

	/**
	 * @param view
	 * @param vh
	 * @param layout
	 *
	 * @deprecated not working
	 * <p>
	 * Show a custom Snackbar using {@link CustomSnackViewHolder} as custom Snackbar view ViewHolder
	 */
	public static void asSnack(View view, CustomSnackViewHolder vh, @LayoutRes int layout) {
		asSnack(view, vh, layout, Snackbar.LENGTH_LONG);
	}

	/**
	 * @param view
	 * @param vh
	 * @param layout
	 * @param duration
	 *
	 * @deprecated not working
	 * <p>
	 * Show a custom Snackbar using {@link CustomSnackViewHolder} as custom Snackbar view ViewHolder
	 */
	public static void asSnack(View view, CustomSnackViewHolder vh, @LayoutRes int layout, int duration) {
		Snackbar snackbar = Snackbar.make(view, "", duration);
		Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
		View v = snackbarLayout.findViewById(R.id.snackbar_text);
		if (v != null) {
			v.setVisibility(View.INVISIBLE);
		}
		v = snackbarLayout.findViewById(R.id.snackbar_action);
		if (v != null) {
			v.setVisibility(View.INVISIBLE);
		}

		snackbarLayout.setBackgroundColor(view.getResources().getColor(android.R.color.transparent));

		View inflatedView = LayoutInflater.from(view.getContext()).inflate(layout, null);
		vh.assignViews(inflatedView);

		snackbarLayout.addView(inflatedView, 0);
		vh.setupBehaviour(snackbar);
		snackbar.show();
	}

	public static void asSnack(View view, String msg) {
		Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
	}

	public static void asSnack(View view, @StringRes int msg) {
		Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
	}

	public static void asToast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public static void asToast(Context context, @StringRes int msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public static abstract class CustomSnackViewHolder {

		public abstract void assignViews(View view);
		public abstract void setupBehaviour(Snackbar snackbar);
	}
}
