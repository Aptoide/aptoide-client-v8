package cm.aptoide.pt.view.app;

import android.support.annotation.NonNull;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Scheduler;
import rx.Single;

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

    onCreateHandleRetry();
  }

  private void onCreateHandleRetry() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> view.getRetryEvent()
            .observeOn(viewScheduler)
            .doOnNext(bottom -> view.showStartingLoading())
            .flatMapSingle(reachesBottom -> loadShowNextApps())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, throwable -> crashReport.log(throwable));
  }

  @NonNull private Single<AppsList> loadShowNextApps() {
    return appCenter.loadNextApps(storeId)
        .observeOn(viewScheduler)
        .doOnSuccess(applications -> {
          if (applications.hasErrors()) {
            handleError(applications.getError());
          } else {
            if (!applications.isLoading()) {
              view.addApps(applications.getList());
              view.hideLoading();
            }
          }
        });
  }

  private void onCreateHandleRefresh() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(lifecycleEvent -> view.getRefreshEvent()
            .flatMapSingle(refresh -> appCenter.loadFreshApps(storeId)
                .observeOn(viewScheduler)
                .doOnSuccess(applications -> {
                  if (!applications.isLoading()) {
                    view.setApps(applications.getList());
                    view.hideRefreshLoading();
                  }
                })
                .retry()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void onCreateLoadApps() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMapSingle(lifecycleEvent -> appCenter.getApps(storeId)
            .observeOn(viewScheduler)
            .doOnSuccess(applications -> {
              if (!applications.isLoading()) {
                view.setApps(applications.getList());
              }
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
            .flatMapSingle(reachesBottom -> loadShowNextApps())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleError(AppsList.Error error) {
    switch (error) {
      case NETWORK:
        view.showNetworkError();
        break;
      case GENERIC:
        view.showGenericError();
        break;
    }
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