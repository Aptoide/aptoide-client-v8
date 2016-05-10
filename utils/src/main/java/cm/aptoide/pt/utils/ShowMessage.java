package cm.aptoide.pt.utils;

import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Toast;

/**
 * Created by trinkes on 5/9/16.
 */
public class ShowMessage {

	public static void show(View view, String msg) {
		Toast.makeText(view.getContext(), msg, Toast.LENGTH_SHORT).show();
	}

	public static void show(View view, @StringRes int msg) {
		Toast.makeText(view.getContext(), msg, Toast.LENGTH_SHORT).show();
	}
}
