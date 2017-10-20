package cm.aptoide.pt.view.app;

import android.os.Bundle;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.navigator.FragmentNavigator;
import rx.Completable;
import rx.Scheduler;

/**
 * Created by trinkes on 17/10/2017.
 */

public class ListStoreAppsPresenter implements Presenter {
  private static final String TAG = ListStoreAppsPresenter.class.getSimpleName();
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
    onCreateLoadApps();

    onCreateShowApps();

    onCreateHandleAppClicks();

    onCreateHandleBottomReached();
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }

  private void onCreateLoadApps() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMapCompletable(lifecycleEvent -> appCenter.loadNextApps(storeId, false)
            .observeOn(viewScheduler)
            .andThen(Completable.fromAction(() -> view.showLoading()))
            .andThen(Completable.fromAction(() -> view.hideStartingLoading())))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void onCreateHandleBottomReached() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> view.reachesBottom()
            .flatMapCompletable(reachesBottom -> appCenter.loadNextApps(storeId, false)
                .observeOn(viewScheduler)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void onCreateHandleAppClicks() {
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

  private void onCreateShowApps() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> appCenter.getStoreApps()
            .observeOn(viewScheduler)
            .doOnNext(appsList -> {
              if (appsList.isEmpty()) {
                view.hideLoading();
              } else {
                view.addApps(appsList);
              }
            }))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, throwable -> crashReport.log(throwable));
  }
}