/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 15/07/2016.
 */

package cm.aptoide.pt.v8engine.util;

import android.app.Activity;
import android.app.FragmentManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import cm.aptoide.pt.v8engine.R;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by neuro on 06-05-2016.
 */
public final class FragmentUtils {

  private static final int EXIT_ANIMATION = android.R.anim.fade_out;
  private static final int ENTER_ANIMATION = android.R.anim.fade_in;

  private static final AtomicInteger atomicInt = new AtomicInteger(0);

  public static void replaceFragment(@NonNull Activity activity, @NonNull Fragment fragment,
      String tag) {

    activity.getFragmentManager().beginTransaction()
        //				.setCustomAnimations(ENTER_ANIMATION, EXIT_ANIMATION, ENTER_ANIMATION, EXIT_ANIMATION)
        .addToBackStack(tag).replace(R.id.fragment_placeholder, fragment, tag).commit();
  }

  public static void replaceFragment(@NonNull Activity activity, @NonNull Fragment fragment) {
    replaceFragment(activity, fragment,
        fragment.getClass().getSimpleName() + "_" + atomicInt.incrementAndGet());
  }

  public static Fragment getFirstFragment(@NonNull Activity activity) {
    FragmentManager.BackStackEntry backStackEntry =
        activity.getFragmentManager().getBackStackEntryAt(0);
    return activity.getFragmentManager().findFragmentByTag(backStackEntry.getName());
  }

  public static Fragment getLastFragment(@NonNull Activity activity) {
    FragmentManager fragmentManager = activity.getFragmentManager();
    FragmentManager.BackStackEntry backStackEntry =
        fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1);
    return activity.getFragmentManager().findFragmentByTag(backStackEntry.getName());
  }

  public static void replaceFragmentV4(@NonNull FragmentActivity fragmentActivity,
      @NonNull Fragment fragment, String tag) {
    fragmentActivity.getSupportFragmentManager()
        .beginTransaction()
        .setCustomAnimations(ENTER_ANIMATION, EXIT_ANIMATION, ENTER_ANIMATION, EXIT_ANIMATION)
        .addToBackStack(tag)
        .replace(R.id.fragment_placeholder, fragment, tag).commitAllowingStateLoss();
  }

  public static void replaceFragmentV4(@NonNull FragmentActivity fragmentActivity,
      @NonNull Fragment fragment) {
    replaceFragmentV4(fragmentActivity, fragment,
        fragment.getClass().getSimpleName() + "_" + atomicInt.incrementAndGet());
  }

  public static Fragment getFirstFragmentV4(@NonNull FragmentActivity fragmentActivity) {
    android.support.v4.app.FragmentManager.BackStackEntry backStackEntry =
        fragmentActivity.getSupportFragmentManager().getBackStackEntryAt(0);
    return fragmentActivity.getSupportFragmentManager().findFragmentByTag(backStackEntry.getName());
  }

  public static Fragment getLastFragmentV4(@NonNull FragmentActivity fragmentActivity) {
    android.support.v4.app.FragmentManager fragmentManager =
        fragmentActivity.getSupportFragmentManager();
    android.support.v4.app.FragmentManager.BackStackEntry backStackEntry =
        fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1);
    return fragmentActivity.getSupportFragmentManager().findFragmentByTag(backStackEntry.getName());
  }
}
