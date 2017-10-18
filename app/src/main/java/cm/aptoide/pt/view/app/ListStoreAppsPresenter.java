package cm.aptoide.pt.view.app;

import android.os.Bundle;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.navigator.FragmentNavigator;
import rx.Scheduler;

/**
 * Created by trinkes on 17/10/2017.
 */

public class ListStoreAppsPresenter implements Presenter {
  private final ListStoreAppsView view;
  private final long storeId;
  private final Scheduler viewScheduler;
  private final AppCenter appCenter;
  private final CrashReport crashReport;
  private final FragmentNavigator fragmentNavigator;

  public ListStoreAppsPresenter(ListStoreAppsView view, long storeId, Scheduler viewScheduler,
      AppCenter appCenter, CrashReport crashReport, FragmentNavigator fragmentNavigator) {
    this.view = view;
    this.storeId = storeId;
    this.viewScheduler = viewScheduler;
    this.appCenter = appCenter;
    this.crashReport = crashReport;
    this.fragmentNavigator = fragmentNavigator;
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> appCenter.getStoreApps(storeId, false)
            .observeOn(viewScheduler)
            .doOnNext(appsList -> view.setApps(appsList)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, throwable -> crashReport.log(throwable));

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> view.getAppClick()
            .doOnNext(app -> fragmentNavigator.navigateTo(
                AppViewFragment.newInstance(app.getAppId(), app.getPackageName(),
                    AppViewFragment.OpenType.OPEN_ONLY, ""), true)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, throwable -> crashReport.log(throwable));
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}