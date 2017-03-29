package cm.aptoide.pt.navigation;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.annotation.Nullable;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.v8engine.R;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class ConcreteNavigationManager implements NavigationManager {

  private static final int EXIT_ANIMATION = android.R.anim.fade_out;
  private static final int ENTER_ANIMATION = android.R.anim.fade_in;

  private final AtomicInteger atomicInt = new AtomicInteger(0);

  private final WeakReference<Activity> weakReference;

  ConcreteNavigationManager(Activity activity) {
    this.weakReference = new WeakReference<>(activity);
  }

  @Override
  public void navigateUsing(GetStoreWidgets.WSWidget wsWidget, String storeTheme, String tag) {
    throw new UnsupportedOperationException("android.app.Activity does not support v4 fragment");
  }

  @Override public void navigateTo(Fragment fragment) {
    Activity activity = weakReference.get();

    if (activity == null) {
      CrashReport.getInstance()
          .log(new RuntimeException(
              "Activity is null in " + ConcreteNavigationManagerV4.class.getName()));
      return;
    }

    final FragmentManager fragmentManager = activity.getFragmentManager();

    // add current fragment
    String tag = generateTag(fragment);
    fragmentManager.beginTransaction()
        .setCustomAnimations(ENTER_ANIMATION, EXIT_ANIMATION, ENTER_ANIMATION, EXIT_ANIMATION)
        .addToBackStack(tag)
        .replace(R.id.fragment_placeholder, fragment)
        .commit();
  }

  @Override public void navigateTo(Fragment fragment, @Nullable List<Fragment> newBackStack) {
    Activity activity = weakReference.get();

    if (activity == null) {
      CrashReport.getInstance()
          .log(new RuntimeException(
              "Activity is null in " + ConcreteNavigationManagerV4.class.getName()));
      return;
    }

    final FragmentManager fragmentManager = activity.getFragmentManager();

    if (newBackStack != null && newBackStack.size() > 0) {
      // replace the entire back stack in the current fragment manager
      cleanBackStack(fragmentManager);

      for (Fragment f : newBackStack) {
        String tag = generateTag(f);
        fragmentManager.beginTransaction()
            .setCustomAnimations(ENTER_ANIMATION, EXIT_ANIMATION, ENTER_ANIMATION, EXIT_ANIMATION)
            .addToBackStack(tag)
            .add(R.id.fragment_placeholder, f)
            .commit();
      }
    }

    // add current fragment
    String tag = generateTag(fragment);
    fragmentManager.beginTransaction()
        .setCustomAnimations(ENTER_ANIMATION, EXIT_ANIMATION, ENTER_ANIMATION, EXIT_ANIMATION)
        .addToBackStack(tag)
        .add(R.id.fragment_placeholder, fragment)
        .commit();
  }

  @Override public void cleanBackStack() {
    Activity activity = weakReference.get();

    if (activity == null) {
      CrashReport.getInstance()
          .log(new RuntimeException(
              "Activity is null in " + ConcreteNavigationManagerV4.class.getName()));
      return;
    }

    cleanBackStack(activity.getFragmentManager());
  }

  private void cleanBackStack(FragmentManager fragmentManager) {
    while (fragmentManager.popBackStackImmediate()) ;
  }

  private String generateTag(Fragment f) {
    return f.getClass().getSimpleName() + "_" + atomicInt.incrementAndGet();
  }
}
