/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 08/05/2016.
 */

package cm.aptoide.pt.v8engine.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import cm.aptoide.pt.v8engine.R;

/**
 * Created by neuro on 06-05-2016.
 */
public class FragmentUtils {

	public static void replaceFragment(FragmentActivity fragmentActivity, Fragment fragment,
									   String backstackName) {
		fragmentActivity.getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(enterAnimation(), defaultExitAnimation(), enterAnimation(),
						defaultExitAnimation())
				.addToBackStack(backstackName)
				.replace(R.id.fragment_placeholder, fragment)
				.commit();
	}

	public static void replaceFragment(FragmentActivity fragmentActivity, Fragment fragment) {
		replaceFragment(fragmentActivity, fragment, "");
	}

	public static int defaultExitAnimation() {
		return android.R.anim.fade_out;
	}

	public static int enterAnimation() {
		return android.R.anim.fade_in;
	}
}
