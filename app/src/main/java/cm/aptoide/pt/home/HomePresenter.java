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
  private final AdMapper adMapper;
  private final AptoideBottomNavigator aptoideBottomNavigator;

  public HomePresenter(HomeView view, Home home, Scheduler viewScheduler, CrashReport crashReporter,
      HomeNavigator homeNavigator, AdMapper adMapper,
      AptoideBottomNavigator aptoideBottomNavigator) {
    this.view = view;
    this.home = home;
    this.viewScheduler = viewScheduler;
    this.crashReporter = crashReporter;
    this.homeNavigator = homeNavigator;
    this.adMapper = adMapper;
    this.aptoideBottomNavigator = aptoideBottomNavigator;
  }

  @Override public void present() {
    onCreateLoadBundles();

    handleAppClick();

    handleAdClick();

    handleMoreClick();

    handleBottomReached();
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

  private void handleAppClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.appClicked()
            .doOnNext(app -> homeNavigator.navigateToAppView(app.getAppId(), app.getPackageName()))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(homeClick -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> aptoideBottomNavigator.navigationEvent())
        .doOnNext(navigated -> view.scrollToTop())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void handleAdClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.adClicked()
            .map(adMapper.mapAdToSearchAd())
            .doOnNext(homeNavigator::navigateToAppView)
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(homeClick -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void handleMoreClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.moreClicked()
            .doOnNext(homeNavigator::navigateWithAction)
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(homeClick -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void handleBottomReached() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.reachesBottom()
            .filter(__ -> home.hasMore())
            .observeOn(viewScheduler)
            .doOnNext(bottomReached -> view.showLoadMore())
            .flatMapSingle(bottomReached -> home.getNextHomeBundles())
            .observeOn(viewScheduler)
            .doOnNext(view::showMoreHomeBundles)
            .doOnNext(bundles -> view.hideShowMore())
            .doOnError(throwable -> view.hideShowMore())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(bundles -> {
        }, throwable -> {
        });
  }
}
