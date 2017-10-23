package cm.aptoide.pt.view.app;

import android.os.Bundle;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.navigator.FragmentNavigator;
import rx.Observable;
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

    onCreateHandleAppClicks();

    onCreateHandleBottomReached();

    onCreateHandleRefresh();
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }

  private void onCreateHandleRefresh() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(lifecycleEvent -> view.getRefreshEvent()
            .flatMapSingle(refresh -> appCenter.loadFreshApps(storeId)
                .observeOn(viewScheduler)
                .doOnSuccess(applications -> {
                  view.setApps(applications);
                  view.hideRefreshLoading();
                })
                .retry()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void onCreateLoadApps() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMapSingle(lifecycleEvent -> appCenter.loadNextApps(storeId)
            .observeOn(viewScheduler)
            .doOnSuccess(applications -> {
              view.setApps(applications);
              view.hideStartingLoading();
            }))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void onCreateHandleBottomReached() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> view.reachesBottom()
            .observeOn(viewScheduler)
            .doOnNext(bottom -> view.showLoading())
            .flatMapSingle(reachesBottom -> appCenter.loadNextApps(storeId)
                .observeOn(viewScheduler)
                .doOnSuccess(applications -> {
                  view.addApps(applications);
                  view.hideLoading();
                })
                .retryWhen(observable -> shouldRetry(observable))))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, throwable -> crashReport.log(throwable));
  }

  private Observable<Object> shouldRetry(Observable<? extends Throwable> observable) {
    return observable.flatMap(throwable -> {
      if (throwable instanceof AlreadyLoadingException) {
        return Observable.just(null);
      } else {
        return Observable.error(throwable);
      }
    });
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
}