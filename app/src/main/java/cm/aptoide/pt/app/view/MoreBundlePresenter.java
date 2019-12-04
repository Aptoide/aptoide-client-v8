package cm.aptoide.pt.app.view;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.home.ChipManager;
import cm.aptoide.pt.home.HomeAnalytics;
import cm.aptoide.pt.home.HomeNavigator;
import cm.aptoide.pt.home.bundles.HomeBundlesModel;
import cm.aptoide.pt.home.bundles.ads.AdHomeEvent;
import cm.aptoide.pt.home.bundles.ads.AdMapper;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.BundleEvent;
import cm.aptoide.pt.view.app.Application;
import rx.Scheduler;
import rx.Single;
import rx.exceptions.OnErrorNotImplementedException;

/**
 * Created by D01 on 04/06/2018.
 */

public class MoreBundlePresenter implements Presenter {

  private final MoreBundleView view;
  private final MoreBundleManager moreBundleManager;
  private final Scheduler viewScheduler;
  private final CrashReport crashReporter;
  private final HomeNavigator homeNavigator;
  private final AdMapper adMapper;
  private final BundleEvent bundleEvent;
  private final HomeAnalytics homeAnalytics;
  private final ChipManager chipManager;

  public MoreBundlePresenter(MoreBundleView view, MoreBundleManager moreBundleManager,
      Scheduler viewScheduler, CrashReport crashReporter, HomeNavigator homeNavigator,
      AdMapper adMapper, BundleEvent bundleEvent, HomeAnalytics homeAnalytics,
      ChipManager chipManager) {
    this.view = view;
    this.moreBundleManager = moreBundleManager;
    this.viewScheduler = viewScheduler;
    this.crashReporter = crashReporter;
    this.homeNavigator = homeNavigator;
    this.adMapper = adMapper;
    this.bundleEvent = bundleEvent;
    this.homeAnalytics = homeAnalytics;
    this.chipManager = chipManager;
  }

  @Override public void present() {
    onCreateSetupToolbar();

    onCreateLoadBundles();

    handleAppClick();

    handleAdClick();

    handleMoreClick();

    handleBottomReached();

    handlePullToRefresh();

    handleRetryClick();

    handleBundleScrolledRight();
  }

  @VisibleForTesting public void onCreateSetupToolbar() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .doOnNext(created -> view.setToolbarInfo(bundleEvent.getTitle()))
        .subscribe(__ -> {
        }, crashReporter::log);
  }

  @VisibleForTesting public void onCreateLoadBundles() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .doOnNext(created -> view.showLoading())
        .flatMapSingle(created -> loadBundles())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReporter::log);
  }

  @NonNull private Single<HomeBundlesModel> loadBundles() {
    return moreBundleManager.loadBundle(bundleEvent.getTitle(), bundleEvent.getAction())
        .observeOn(viewScheduler)
        .doOnSuccess(bundlesModel -> {
          if (bundlesModel.hasErrors()) {
            handleError(bundlesModel.getError());
          } else if (!bundlesModel.isLoading()) {
            view.hideLoading();
            view.showBundles(bundlesModel.getList());
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
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.appClicked()
            .doOnNext(click -> {
              ChipManager.Chip chip = chipManager.getCurrentChip();
              homeAnalytics.sendTapOnAppInteractEvent(click.getApp()
                      .getRating(), click.getApp()
                      .getPackageName(), click.getAppPosition(), click.getBundlePosition(),
                  click.getBundle()
                      .getTag(), click.getBundle()
                      .getContent()
                      .size(), chip != null ? chip.getName() : null);
              if (chip != null) {
                homeAnalytics.sendChipTapOnApp(click.getBundle()
                    .getTag(), click.getApp()
                    .getPackageName(), chip.getName());
              }
            })
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

  @VisibleForTesting public void handleAdClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.adClicked()
            .map(AdHomeEvent::getAdClick)
            .map(adMapper::mapAdToSearchAd)
            .observeOn(viewScheduler)
            .doOnNext(result -> homeNavigator.navigateToAppView(result.getTag(),
                result.getSearchAdResult()))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(homeClick -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @VisibleForTesting public void handleMoreClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.moreClicked()
            .doOnNext(homeMoreClick -> {
              ChipManager.Chip chip = chipManager.getCurrentChip();
              homeAnalytics.sendTapOnMoreInteractEvent(homeMoreClick.getBundlePosition(),
                  homeMoreClick.getBundle()
                      .getTag(), homeMoreClick.getBundle()
                      .getContent()
                      .size(), chip != null ? chip.getName() : null);
              if (chip != null) {
                homeAnalytics.sendChipTapOnMore(homeMoreClick.getBundle()
                    .getTag(), chip.getName());
              }
            })
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
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.bundleScrolled()
            .doOnNext(click -> homeAnalytics.sendScrollRightInteractEvent(click.getBundlePosition(),
                click.getBundle()
                    .getTag(), click.getBundle()
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
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.reachesBottom()
            .filter(__ -> moreBundleManager.hasMore(bundleEvent.getTitle()))
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
    return moreBundleManager.loadNextBundles(bundleEvent.getTitle(), bundleEvent.getAction())
        .observeOn(viewScheduler)
        .doOnSuccess(bundlesModel -> {
          if (bundlesModel.hasErrors()) {
            handleError(bundlesModel.getError());
          } else {
            if (!bundlesModel.isLoading()) {
              view.showMoreHomeBundles(bundlesModel.getList());
              view.hideLoading();
            }
          }
          view.hideShowMore();
        });
  }

  @VisibleForTesting public void handlePullToRefresh() {
    view.getLifecycleEvent()
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
    return moreBundleManager.loadFreshBundles(bundleEvent.getTitle(), bundleEvent.getAction())
        .observeOn(viewScheduler)
        .doOnSuccess(bundlesModel -> {
          view.hideRefresh();
          if (bundlesModel.hasErrors()) {
            handleError(bundlesModel.getError());
          } else {
            if (!bundlesModel.isLoading()) {
              view.showBundles(bundlesModel.getList());
            }
          }
        });
  }

  @VisibleForTesting public void handleRetryClick() {
    view.getLifecycleEvent()
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
}
