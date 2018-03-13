package cm.aptoide.pt.home;

import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import io.reactivex.exceptions.OnErrorNotImplementedException;
import rx.Scheduler;

/**
 * Created by jdandrade on 07/03/2018.
 */

public class HomePresenter implements Presenter {

  private final HomeView view;
  private final Home home;
  private final Scheduler viewScheduler;
  private final CrashReport crashReporter;
  private final HomeNavigator homeNavigator;

  public HomePresenter(HomeView view, Home home, Scheduler viewScheduler, CrashReport crashReporter,
      HomeNavigator homeNavigator) {
    this.view = view;
    this.home = home;
    this.viewScheduler = viewScheduler;
    this.crashReporter = crashReporter;
    this.homeNavigator = homeNavigator;
  }

  @Override public void present() {
    onCreateLoadBundles();

    handleAppClick();
  }

  private void handleAppClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.appClicked()
            .doOnNext(homeNavigator::navigateToAppView)
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(homeClick -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void onCreateLoadBundles() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .doOnNext(created -> view.showLoading())
        .flatMapSingle(created -> home.getHomeBundles())
        .observeOn(viewScheduler)
        .doOnNext(view::showHomeBundles)
        .doOnNext(bundles -> view.hideLoading())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          view.showGenericError();
          crashReporter.log(throwable);
        });
  }
}
