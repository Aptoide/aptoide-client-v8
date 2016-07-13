/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 12/07/2016.
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

	public static void replaceFragment(@NonNull android.app.Activity activity, @NonNull android.app.Fragment fragment, String tag) {

		activity.getFragmentManager()
				.beginTransaction()
//				.setCustomAnimations(ENTER_ANIMATION, EXIT_ANIMATION, ENTER_ANIMATION, EXIT_ANIMATION)
				.addToBackStack(tag).replace(R.id.fragment_placeholder, fragment, tag)
				.commit();
	}

	public static void replaceFragment(@NonNull android.app.Activity activity, @NonNull android.app.Fragment
			fragment) {
		replaceFragment(activity, fragment, fragment.getClass().getSimpleName() + "_" + atomicInt.incrementAndGet());
	}

	public static android.app.Fragment getCurrentFragment(@NonNull android.app.Activity activity) {
		android.app.FragmentManager.BackStackEntry backStackEntry = activity.getFragmentManager().getBackStackEntryAt(0);
		return activity.getFragmentManager().findFragmentByTag(backStackEntry.getName());
	}

	public static void replaceFragmentV4(@NonNull android.support.v4.app.FragmentActivity fragmentActivity, @NonNull android.support.v4.app.Fragment fragment,
	                                     String tag) {
		fragmentActivity.getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(ENTER_ANIMATION, EXIT_ANIMATION, ENTER_ANIMATION, EXIT_ANIMATION)
				.addToBackStack(tag)
				.replace(R.id.fragment_placeholder, fragment, tag)
				.commit();
	}

	public static void replaceFragmentV4(@NonNull android.support.v4.app.FragmentActivity fragmentActivity, @NonNull
	android.support.v4.app.Fragment fragment) {
		replaceFragmentV4(fragmentActivity, fragment, fragment.getClass().getSimpleName() + "_" + atomicInt
				.incrementAndGet());
	}

	public static android.support.v4.app.Fragment getCurrentFragmentV4(@NonNull android.support.v4.app.FragmentActivity fragmentActivity) {
		android.support.v4.app.FragmentManager.BackStackEntry backStackEntry = fragmentActivity.getSupportFragmentManager().getBackStackEntryAt(0);
		return fragmentActivity.getSupportFragmentManager().findFragmentByTag(backStackEntry.getName());
	}
}
