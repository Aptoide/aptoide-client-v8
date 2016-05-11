package cm.aptoide.pt.utils;

import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

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
}
