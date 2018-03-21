package cm.aptoide.pt.home;

import android.support.annotation.NonNull;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import io.reactivex.exceptions.OnErrorNotImplementedException;
import rx.Scheduler;
import rx.Single;

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
  private final AptoideAccountManager accountManager;

  public HomePresenter(HomeView view, Home home, Scheduler viewScheduler, CrashReport crashReporter,
      HomeNavigator homeNavigator, AdMapper adMapper, AptoideAccountManager accountManager) {
    this.view = view;
    this.home = home;
    this.viewScheduler = viewScheduler;
    this.crashReporter = crashReporter;
    this.homeNavigator = homeNavigator;
    this.adMapper = adMapper;
    this.accountManager = accountManager;
  }

  @Override public void present() {
    onCreateLoadBundles();

    loadUserImage();

    handleAppClick();

    handleAdClick();

    handleMoreClick();

    handleBottomReached();

    handlePullToRefresh();

    handleBottomNavigationEvents();

    handleRetryClick();

    handleUserImageClick();
  }

  private void onCreateLoadBundles() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .doOnNext(created -> view.showLoading())
        .flatMapSingle(created -> loadBundles())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReporter::log);
  }

  @NonNull private Single<HomeBundlesModel> loadBundles() {
    return home.loadHomeBundles()
        .observeOn(viewScheduler)
        .doOnSuccess(bundlesModel -> {
          if (bundlesModel.hasErrors()) {
            handleError(bundlesModel.getError());
          } else if (!bundlesModel.isLoading()) {
            view.hideLoading();
            view.showHomeBundles(bundlesModel.getList());
          }
        });
  }

  private void handleError(HomeBundlesModel.Error error) {
    switch (error) {
      case NETWORK:
        view.showNetworkError();
        break;
      case GENERIC:
        view.showGenericError();
        break;
    }
  }

  private void handleAppClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.appClicked()
            .observeOn(viewScheduler)
            .doOnNext(app -> homeNavigator.navigateToAppView(app.getAppId(), app.getPackageName()))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(homeClick -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void handleBottomNavigationEvents() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> homeNavigator.bottomNavigation())
        .observeOn(viewScheduler)
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
            .observeOn(viewScheduler)
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
            .observeOn(viewScheduler)
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
            .flatMapSingle(bottomReached -> loadNextBundles())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(bundles -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @NonNull private Single<HomeBundlesModel> loadNextBundles() {
    return home.loadNextHomeBundles()
        .observeOn(viewScheduler)
        .doOnSuccess(bundlesModel -> {
          if (bundlesModel.hasErrors()) {
            handleError(bundlesModel.getError());
          } else {
            if (!bundlesModel.isLoading()) {
              view.showMoreHomeBundles(bundlesModel.getList());
            }
          }
          view.hideShowMore();
          view.hideLoading();
        });
  }

  private void handlePullToRefresh() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.refreshes()
            .flatMapSingle(refreshed -> loadFreshBundles())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(bundles -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @NonNull private Single<HomeBundlesModel> loadFreshBundles() {
    return home.loadFreshHomeBundles()
        .observeOn(viewScheduler)
        .doOnSuccess(bundlesModel -> {
          view.hideRefresh();
          if (bundlesModel.hasErrors()) {
            handleError(bundlesModel.getError());
          } else {
            if (!bundlesModel.isLoading()) {
              view.showHomeBundles(bundlesModel.getList());
            }
          }
        });
  }

  private void handleRetryClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> view.retryClicked()
            .observeOn(viewScheduler)
            .doOnNext(bottom -> view.showLoading())
            .flatMapSingle(reachesBottom -> loadNextBundles())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, crashReporter::log);
  }

  private void loadUserImage() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> accountManager.accountStatus()
            .first())
        .observeOn(viewScheduler)
        .doOnNext(account -> view.setUserImage(account))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new rx.exceptions.OnErrorNotImplementedException(throwable);
        });
  }

  private void handleUserImageClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.imageClick()
            .observeOn(viewScheduler)
            .doOnNext(account -> homeNavigator.navigateToSettings())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new rx.exceptions.OnErrorNotImplementedException(throwable);
        });
  }
}
