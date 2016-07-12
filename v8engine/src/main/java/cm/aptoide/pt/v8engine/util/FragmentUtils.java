/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/06/2016.
 */

package cm.aptoide.pt.v8engine.util;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import java.util.concurrent.atomic.AtomicInteger;

import cm.aptoide.pt.v8engine.R;

/**
 * Created by neuro on 06-05-2016.
 */
public final class FragmentUtils {

	private static final int EXIT_ANIMATION = android.R.anim.fade_out;
	private static final int ENTER_ANIMATION = android.R.anim.fade_in;

	private static final AtomicInteger atomicInt = new AtomicInteger(0);

	public static void replaceFragment(@NonNull android.app.Activity activity, @NonNull android.app.Fragment fragment,
	                                   String backstackName) {
		activity.getFragmentManager()
				.beginTransaction()
//				.setCustomAnimations(ENTER_ANIMATION, EXIT_ANIMATION, ENTER_ANIMATION, EXIT_ANIMATION)
				.addToBackStack(backstackName)
				.replace(R.id.fragment_placeholder, fragment)
				.commit();
	}

	public static void replaceFragment(@NonNull android.app.Activity activity, @NonNull android.app.Fragment
			fragment) {
		replaceFragment(activity, fragment, "fragment_" + atomicInt.incrementAndGet());
	}

	public static void replaceFragmentV4(@NonNull android.support.v4.app.FragmentActivity fragmentActivity, @NonNull
	android.support.v4.app.Fragment fragment, String backstackName) {
		fragmentActivity.getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(ENTER_ANIMATION, EXIT_ANIMATION, ENTER_ANIMATION, EXIT_ANIMATION)
				.addToBackStack(backstackName)
				.replace(R.id.fragment_placeholder, fragment)
				.commit();
	}

	public static void replaceFragmentV4(@NonNull android.support.v4.app.FragmentActivity fragmentActivity, @NonNull
	android.support.v4.app.Fragment fragment) {
		replaceFragmentV4(fragmentActivity, fragment, fragment.getClass().getSimpleName() + "_" + atomicInt
				.incrementAndGet());
	}
}
