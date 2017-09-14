package cm.aptoide.pt.view.navigator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.view.store.home.HomeFragment;
import com.jakewharton.rxrelay.BehaviorRelay;
import java.util.Map;
import rx.Observable;

public class FragmentResultNavigator implements FragmentNavigator {

  private final FragmentManager fragmentManager;
  private final int containerId;
  private final int exitAnimation;
  private final int enterAnimation;
  private final SharedPreferences sharedPreferences;
  private final String defaultStore;
  private final String defaultTheme;
  private final Map<Integer, Result> results;
  private final BehaviorRelay<Map<Integer, Result>> resultRelay;

  public FragmentResultNavigator(FragmentManager fragmentManager, @IdRes int containerId,
      int enterAnimation, int exitAnimation, SharedPreferences sharedPreferences,
      String defaultStore, String defaultTheme, Map<Integer, Result> resultMap,
      BehaviorRelay<Map<Integer, Result>> resultRelay) {
    this.fragmentManager = fragmentManager;
    this.containerId = containerId;
    this.enterAnimation = enterAnimation;
    this.exitAnimation = exitAnimation;
    this.sharedPreferences = sharedPreferences;
    this.defaultStore = defaultStore;
    this.defaultTheme = defaultTheme;
    this.results = resultMap;
    this.resultRelay = resultRelay;
  }

  @Override public void navigateWithoutReplace(Fragment fragment) {
    String tag = Integer.toString(fragmentManager.getBackStackEntryCount());
    prepareFragmentAdd(fragment, tag).commit();
  }

  @Override public void navigateForResult(NavigateFragment fragment, int requestCode) {
    Bundle extras = fragment.getArguments();
    if (extras == null) {
      extras = new Bundle();
    }
    extras.putInt(NavigateFragment.REQUEST_CODE_KEY, requestCode);
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

  @Override public void navigateUsing(Event event, String storeTheme, String title, String tag,
      StoreContext storeContext) {
    Fragment fragment;

    // TODO: 22/12/2016 refactor this using the rules present in "StoreTabGridRecyclerFragment.java"
    if (event.getName() == Event.Name.listComments) {
      String action = event.getAction();
      String url = action != null ? action.replace(V7.getHost(sharedPreferences), "") : null;

      fragment = AptoideApplication.getFragmentProvider()
          .newCommentGridRecyclerFragmentUrl(CommentType.STORE, url, "View Comments");
    } else {
      fragment = AptoideApplication.getFragmentProvider()
          .newStoreTabGridRecyclerFragment(event, title, storeTheme, tag, storeContext);
    }

    navigateTo(fragment);
  }

  @Override public String navigateTo(Fragment fragment) {
    String tag = Integer.toString(fragmentManager.getBackStackEntryCount());
    prepareFragmentReplace(fragment, tag).commit();

    return tag;
  }

  @Override public String navigateToAllowingStateLoss(Fragment fragment) {
    // add current fragment
    String tag = Integer.toString(fragmentManager.getBackStackEntryCount());
    prepareFragmentReplace(fragment, tag).commitAllowingStateLoss();

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

  @Override public void navigateToHomeCleaningBackStack() {
    Fragment home = HomeFragment.newInstance(defaultStore, defaultTheme);
    cleanBackStack();
    navigateToWithoutBackSave(home);
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

  private FragmentTransaction prepareFragmentAdd(Fragment fragment, String tag) {
    return fragmentManager.beginTransaction()
        .setCustomAnimations(enterAnimation, exitAnimation, enterAnimation, exitAnimation)
        .addToBackStack(tag)
        .add(containerId, fragment);
  }

  private FragmentTransaction prepareFragmentReplace(Fragment fragment, String tag) {
    return fragmentManager.beginTransaction()
        .setCustomAnimations(enterAnimation, exitAnimation, enterAnimation, exitAnimation)
        .addToBackStack(tag)
        .replace(containerId, fragment, tag);
  }
}
