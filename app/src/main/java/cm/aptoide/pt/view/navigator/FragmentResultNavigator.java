package cm.aptoide.pt.view.navigator;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.jakewharton.rxrelay.BehaviorRelay;
import java.util.Map;
import rx.Observable;

public class FragmentResultNavigator implements FragmentNavigator {

  private final FragmentManager fragmentManager;
  private final int containerId;
  private final int exitAnimation;
  private final int enterAnimation;
  private final String defaultStore;
  private final String defaultTheme;
  private final Map<Integer, Result> results;
  private final BehaviorRelay<Map<Integer, Result>> resultRelay;

  public FragmentResultNavigator(FragmentManager fragmentManager, @IdRes int containerId,
      int enterAnimation, int exitAnimation, String defaultStore, String defaultTheme,
      Map<Integer, Result> resultMap, BehaviorRelay<Map<Integer, Result>> resultRelay) {
    this.fragmentManager = fragmentManager;
    this.containerId = containerId;
    this.enterAnimation = enterAnimation;
    this.exitAnimation = exitAnimation;
    this.defaultStore = defaultStore;
    this.defaultTheme = defaultTheme;
    this.results = resultMap;
    this.resultRelay = resultRelay;
  }

  @Override public void navigateForResult(Fragment fragment, int requestCode) {
    Bundle extras = fragment.getArguments();
    if (extras == null) {
      extras = new Bundle();
    }
    extras.putInt(FragmentNavigator.REQUEST_CODE_EXTRA, requestCode);
    fragment.setArguments(extras);
    navigateTo(fragment);
  }

  @Override public Observable<Result> results(int requestCode) {
    return resultRelay.filter(integerResultMap -> integerResultMap.containsKey(requestCode))
        .map(integerResultMap -> integerResultMap.get(requestCode))
        .doOnNext(result -> results.remove(requestCode));
  }

  @Override public void popWithResult(Result result) {
    results.put(result.getRequestCode(), result);
    resultRelay.call(results);
    popBackStack();
  }

  @Override public String navigateTo(Fragment fragment) {
    final String tag = Integer.toString(fragmentManager.getBackStackEntryCount());
    fragmentManager.beginTransaction()
        .setCustomAnimations(enterAnimation, exitAnimation, enterAnimation, exitAnimation)
        .addToBackStack(tag)
        .replace(containerId, fragment, tag)
        .commit();
    return tag;
  }

  /**
   * Only use this method when it is navigating to the first fragment in the activity.
   */
  @Override public void navigateToWithoutBackSave(Fragment fragment) {
    fragmentManager.beginTransaction()
        .setCustomAnimations(enterAnimation, exitAnimation, enterAnimation, exitAnimation)
        .replace(containerId, fragment)
        .commit();
  }

  @Override public void navigateToCleaningBackStack(Fragment fragment) {
    cleanBackStack();
    navigateToWithoutBackSave(fragment);
  }

  @Override public boolean popBackStack() {
    return fragmentManager.popBackStackImmediate();
  }

  @Override public void cleanBackStack() {
    for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
      fragmentManager.popBackStack();
    }
    fragmentManager.executePendingTransactions();
  }

  @Override public boolean cleanBackStackUntil(String fragmentTag) {
    if (fragmentManager.getBackStackEntryCount() == 0) {
      return false;
    }

    boolean popped = false;

    while (fragmentManager.getBackStackEntryCount() > 0 && !popped) {
      if (fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1)
          .getName()
          .equals(fragmentTag)) {
        popped = true;
      }
      fragmentManager.popBackStackImmediate();
    }
    return popped;
  }

  @Override public Fragment peekLast() {
    if (fragmentManager.getBackStackEntryCount() > 0) {
      FragmentManager.BackStackEntry backStackEntry =
          fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1);
      return fragmentManager.findFragmentByTag(backStackEntry.getName());
    }

    return null;
  }

  @Override public Fragment getFragment() {
    return fragmentManager.findFragmentById(containerId);
  }
}
