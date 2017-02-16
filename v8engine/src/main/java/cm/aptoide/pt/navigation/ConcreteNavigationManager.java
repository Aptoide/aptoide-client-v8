package cm.aptoide.pt.navigation;

import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.v8engine.R;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;

class ConcreteNavigationManager implements NavigationManager {

  private static final int EXIT_ANIMATION = android.R.anim.fade_out;
  private static final int ENTER_ANIMATION = android.R.anim.fade_in;

  private final AtomicInteger atomicInt = new AtomicInteger(0);

  private final WeakReference<android.app.Activity> weakReference;

  ConcreteNavigationManager(android.app.Activity activity) {
    this.weakReference = new WeakReference<>(activity);
  }

  @Override
  public void navigateUsing(GetStoreWidgets.WSWidget wsWidget, String storeTheme, String tag) {
    throw new UnsupportedOperationException("android.app.Activity does not support v4 fragment");
  }

  @Override public void navigateTo(android.app.Fragment fragment) {
    final String tag = fragment.getClass().getSimpleName() + "_" + atomicInt.incrementAndGet();
    android.app.Activity activity = weakReference.get();

    if (activity == null) {
      CrashReport.getInstance()
          .log(new RuntimeException(
              "Activity is null in " + ConcreteNavigationManager.class.getName()));
      return;
    }

    activity.getFragmentManager()
        .beginTransaction()
        .setCustomAnimations(ENTER_ANIMATION, EXIT_ANIMATION, ENTER_ANIMATION, EXIT_ANIMATION)
        .addToBackStack(tag)
        .replace(R.id.fragment_placeholder, fragment, tag)
        .commit();
  }
}
