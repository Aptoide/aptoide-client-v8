package cm.aptoide.pt.navigation;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import java.lang.ref.WeakReference;

class ConcreteNavigationManagerV4 implements NavigationManagerV4 {

  private static final String TAG = ConcreteNavigationManagerV4.class.getName();

  private static final int EXIT_ANIMATION = android.R.anim.fade_out;
  private static final int ENTER_ANIMATION = android.R.anim.fade_in;

  private final WeakReference<FragmentActivity> weakReference;

  ConcreteNavigationManagerV4(FragmentActivity fragmentActivity) {
    this.weakReference = new WeakReference<>(fragmentActivity);
  }

  @Override public void navigateUsing(Event event, String storeTheme, String title, String tag,
      StoreContext storeContext) {
    Fragment fragment;

    // TODO: 22/12/2016 sithengineer refactor this using the rules present in "StoreTabGridRecyclerFragment.java"
    if (event.getName() == Event.Name.listComments) {
      String action = event.getAction();
      String url = action != null ? action.replace(V7.BASE_HOST, "") : null;

      fragment =
          V8Engine.getFragmentProvider().newCommentGridRecyclerFragmentUrl(CommentType.STORE, url);
    } else {
      fragment = V8Engine.getFragmentProvider()
          .newStoreTabGridRecyclerFragment(event, title, storeTheme, tag, storeContext);
    }

    navigateTo(fragment);
  }

  @Override public String navigateTo(Fragment fragment) {
    FragmentActivity activity = weakReference.get();

    if (activity == null) {
      CrashReport.getInstance()
          .log(new RuntimeException(
              "Activity is null in " + ConcreteNavigationManagerV4.class.getName()));
      return null;
    }

    final FragmentManager fragmentManager = activity.getSupportFragmentManager();

    // add current fragment
    String tag = Integer.toString(fragmentManager.getBackStackEntryCount());
    fragmentManager.beginTransaction()
        .setCustomAnimations(ENTER_ANIMATION, EXIT_ANIMATION, ENTER_ANIMATION, EXIT_ANIMATION)
        .addToBackStack(tag)
        .replace(R.id.fragment_placeholder, fragment, tag)
        .commit();

    return tag;
  }

  @Override public void cleanBackStack() {
    FragmentActivity activity = weakReference.get();

    if (activity == null) {
      CrashReport.getInstance()
          .log(new RuntimeException(
              "Activity is null in " + TAG));
      return;
    }

    cleanBackStack(activity.getSupportFragmentManager());
  }

  @Override public void cleanBackStackUntil(String fragmentTag) {
    FragmentActivity activity = weakReference.get();

    if (activity == null) {
      CrashReport.getInstance()
          .log(new RuntimeException(
              "Activity is null in " + TAG));
      return;
    }

    cleanBackStackUntil(fragmentTag, activity.getSupportFragmentManager());
  }

  @Override public Fragment peekLast() {
    FragmentActivity activity = weakReference.get();

    if (activity == null) {
      CrashReport.getInstance()
          .log(new RuntimeException(
              "Activity is null in " + TAG));
      return null;
    }

    final FragmentManager fragmentManager = activity.getSupportFragmentManager();
    if (fragmentManager.getBackStackEntryCount() > 0) {
      FragmentManager.BackStackEntry backStackEntry =
          fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1);
      return fragmentManager.findFragmentByTag(backStackEntry.getName());
    }
    return null;
  }

  @Override public Fragment peekFirst() {
    FragmentActivity activity = weakReference.get();

    if (activity == null) {
      CrashReport.getInstance()
          .log(new RuntimeException(
              "Activity is null in " + TAG));
      return null;
    }

    final FragmentManager fragmentManager = activity.getSupportFragmentManager();
    if (fragmentManager.getBackStackEntryCount() > 0) {
      FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(0);
      return fragmentManager.findFragmentByTag(backStackEntry.getName());
    }
    return null;
  }

  @Override public void navigateToWithoutBackSave(Fragment fragment) {
    FragmentActivity activity = weakReference.get();

    if (activity == null) {
      CrashReport.getInstance()
          .log(new RuntimeException(
              "Activity is null in " + TAG));
      return;
    }

    final FragmentManager fragmentManager = activity.getSupportFragmentManager();

    // add current fragment
    fragmentManager.beginTransaction()
        .setCustomAnimations(ENTER_ANIMATION, EXIT_ANIMATION, ENTER_ANIMATION, EXIT_ANIMATION)
        .replace(R.id.fragment_placeholder, fragment)
        .commit();
  }

  private void cleanBackStackUntil(String fragmentTag, FragmentManager fragmentManager) {
    if (fragmentManager.getBackStackEntryCount() == 0) {
      return;
    }

    boolean popped = false;

    while (fragmentManager.getBackStackEntryCount() > 0 || !popped) {
      if (fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1)
          .getName()
          .equals(fragmentTag)) {
        popped = true;
      }
      fragmentManager.popBackStackImmediate();
    }
  }

  private void cleanBackStack(FragmentManager fragmentManager) {
    for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
      fragmentManager.popBackStack();
    }
    fragmentManager.executePendingTransactions();
  }
}
