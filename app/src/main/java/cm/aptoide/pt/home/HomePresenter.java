package cm.aptoide.pt.home;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.app.Application;
import rx.Observable;
import rx.Scheduler;
import rx.Single;
import rx.exceptions.OnErrorNotImplementedException;

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
  private final HomeAnalytics homeAnalytics;

  public HomePresenter(HomeView view, Home home, Scheduler viewScheduler, CrashReport crashReporter,
      HomeNavigator homeNavigator, AdMapper adMapper, AptoideAccountManager accountManager,
      HomeAnalytics homeAnalytics) {
    this.view = view;
    this.home = home;
    this.viewScheduler = viewScheduler;
    this.crashReporter = crashReporter;
    this.homeNavigator = homeNavigator;
    this.adMapper = adMapper;
    this.accountManager = accountManager;
    this.homeAnalytics = homeAnalytics;
  }

  @Override public void present() {
    onCreateLoadBundles();

    loadUserImage();

    handleAppClick();

    handleRewardAppClick();

    handleRecommendedAppClick();

    handleAdClick();

    handleMoreClick();

    handleBottomReached();

    handlePullToRefresh();

    handleBottomNavigationEvents();

    handleRetryClick();

    handleUserImageClick();

    handleBundleScrolledRight();
  }

  private void handleRewardAppClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.rewardAppClicked()
            .doOnNext(click -> homeAnalytics.sendTapOnAppInteractEvent(click.getApp()
                    .getRating(), click.getApp()
                    .getPackageName(), click.getAppPosition(), click.getBundlePosition(),
                click.getBundle()
                    .getTitle(), click.getBundle()
                    .getContent()
                    .size()))
            .map(appClick -> ((RewardApp) appClick.getApp()))
            .doOnNext(rewardApp -> homeNavigator.navigateToRewardAppView(rewardApp.getAppId(),
                rewardApp.getPackageName(), rewardApp.getTag(), rewardApp.getRewardValue()))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReporter.log(throwable));
  }

  @VisibleForTesting public void onCreateLoadBundles() {
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

  @VisibleForTesting public void handleAppClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.appClicked()
            .doOnNext(click -> homeAnalytics.sendTapOnAppInteractEvent(click.getApp()
                    .getRating(), click.getApp()
                    .getPackageName(), click.getAppPosition(), click.getBundlePosition(),
                click.getBundle()
                    .getTitle(), click.getBundle()
                    .getContent()
                    .size()))
            .observeOn(viewScheduler)
            .doOnNext(click -> {
              Application app = click.getApp();
              homeNavigator.navigateToAppView(app.getAppId(), app.getPackageName(), app.getTag());
            })
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(homeClick -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @VisibleForTesting public void handleRecommendedAppClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.recommendedAppClicked()
            .observeOn(viewScheduler)
            .doOnNext(click -> homeNavigator.navigateToAppView(click.getApp()
                .getAppId(), click.getApp()
                .getPackageName(), click.getApp()
                .getTag()))
            .doOnNext(click -> homeAnalytics.sendRecommendedAppInteractEvent(click.getApp()
                .getRating(), click.getApp()
                .getPackageName(), click.getBundlePosition(), click.getType()))
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

  @VisibleForTesting public void handleAdClick() {
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

  @VisibleForTesting public void handleMoreClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.moreClicked()
            .doOnNext(homeMoreClick -> homeAnalytics.sendTapOnMoreInteractEvent(
                homeMoreClick.getBundlePosition(), homeMoreClick.getBundle()
                    .getTitle(), homeMoreClick.getBundle()
                    .getContent()
                    .size()))
            .observeOn(viewScheduler)
            .doOnNext(homeNavigator::navigateWithAction)
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(homeClick -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @VisibleForTesting public void handleBundleScrolledRight() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.bundleScrolled()
            .doOnNext(click -> homeAnalytics.sendScrollRightInteractEvent(click.getBundlePosition(),
                click.getBundle()
                    .getTitle(), click.getBundle()
                    .getContent()
                    .size()))
            .doOnError(crashReporter::log)
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(scroll -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @VisibleForTesting public void handleBottomReached() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.reachesBottom()
            .filter(__ -> home.hasMore())
            .observeOn(viewScheduler)
            .doOnNext(bottomReached -> view.showLoadMore())
            .flatMapSingle(bottomReached -> loadNextBundles())
            .doOnNext(__ -> homeAnalytics.sendLoadMoreInteractEvent())
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

  @VisibleForTesting public void handlePullToRefresh() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.refreshes()
            .doOnNext(__ -> homeAnalytics.sendPullRefreshInteractEvent())
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

  @VisibleForTesting public void handleRetryClick() {
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

  @VisibleForTesting public void loadUserImage() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> accountManager.accountStatus()
            .first())
        .flatMap(account -> getUserAvatar(account))
        .observeOn(viewScheduler)
        .doOnNext(userAvatarUrl -> {
          if (userAvatarUrl != null) {
            view.setUserImage(userAvatarUrl);
          }
          view.showAvatar();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @VisibleForTesting public void handleUserImageClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.imageClick()
            .observeOn(viewScheduler)
            .doOnNext(account -> homeNavigator.navigateToMyAccount())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private Observable<String> getUserAvatar(Account account) {
    String userAvatarUrl = null;
    if (account != null && account.isLoggedIn()) {
      userAvatarUrl = account.getAvatar();
    }
    return Observable.just(userAvatarUrl);
  }
}
