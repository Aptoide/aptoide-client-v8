package cm.aptoide.pt.home;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.ads.data.ApplicationAd;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.app.Application;
import rx.Observable;
import rx.Scheduler;
import rx.Single;
import rx.exceptions.OnErrorNotImplementedException;

import static cm.aptoide.pt.home.HomeBundle.BundleType.EDITORS;

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

    handleRecommendedAppClick();

    handleAdClick();

    handleMoreClick();

    handleBottomReached();

    handlePullToRefresh();

    handleBottomNavigationEvents();

    handleRetryClick();

    handleUserImageClick();

    handleBundleScrolledRight();

    handleKnowMoreClick();

    handleDismissClick();

    handleActionBundlesImpression();

    handleLoggedInAcceptTermsAndConditions();

    handleTermsAndConditionsContinueClicked();

    handleTermsAndConditionsLogOutClicked();

    handleClickOnTermsAndConditions();

    handleClickOnPrivacyPolicy();

    handleEditorialCardClick();

    handlePromotionsClick();

    checkForPromotionApps();

    handleClickOnPromotionsDialogContinue();
  }

  private void handlePromotionsClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.promotionsClick()
            .observeOn(viewScheduler)
            .doOnNext(account -> homeNavigator.navigateToPromotions())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @VisibleForTesting public void handleActionBundlesImpression() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.visibleBundles())
        .filter(homeEvent -> homeEvent.getBundle() instanceof ActionBundle)
        .doOnNext(homeEvent -> {
          if (homeEvent.getBundle()
              .getType()
              .equals(HomeBundle.BundleType.INFO_BUNDLE)) {
            homeAnalytics.sendAppcImpressionEvent(homeEvent.getBundle()
                .getTag(), homeEvent.getBundlePosition());
          } else {
            ActionBundle actionBundle = (ActionBundle) homeEvent.getBundle();
            homeAnalytics.sendEditorialImpressionEvent(actionBundle.getTag(),
                homeEvent.getBundlePosition(), actionBundle.getActionItem()
                    .getCardId());
          }
        })
        .filter(homeEvent -> homeEvent.getBundle()
            .getType()
            .equals(HomeBundle.BundleType.INFO_BUNDLE))
        .map(HomeEvent::getBundle)
        .cast(ActionBundle.class)
        .flatMapCompletable(home::actionBundleImpression)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(actionBundle -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @VisibleForTesting public void handleKnowMoreClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.infoBundleKnowMoreClicked())
        .observeOn(viewScheduler)
        .doOnNext(homeEvent -> {
          homeAnalytics.sendAppcKnowMoreInteractEvent(homeEvent.getBundle()
              .getTag(), homeEvent.getBundlePosition());
          homeNavigator.navigateToAppCoinsInformationView();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(lifecycleEvent -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @VisibleForTesting public void handleDismissClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.dismissBundleClicked())
        .filter(homeEvent -> homeEvent.getBundle() instanceof ActionBundle)
        .doOnNext(homeEvent -> homeAnalytics.sendAppcDismissInteractEvent(homeEvent.getBundle()
            .getTag(), homeEvent.getBundlePosition()))
        .flatMap(homeEvent -> home.remove((ActionBundle) homeEvent.getBundle())
            .andThen(Observable.just(homeEvent)))
        .observeOn(viewScheduler)
        .doOnNext(homeEvent -> view.hideBundle(homeEvent.getBundlePosition()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(lifecycleEvent -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
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
    return home.loadHomeBundles()
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
            .doOnNext(click -> homeAnalytics.sendTapOnAppInteractEvent(click.getApp()
                    .getRating(), click.getApp()
                    .getPackageName(), click.getAppPosition(), click.getBundlePosition(),
                click.getBundle()
                    .getTag(), click.getBundle()
                    .getContent()
                    .size()))
            .observeOn(viewScheduler)
            .doOnNext(click -> {
              Application app = click.getApp();
              if (click.getBundle()
                  .getType()
                  .equals(EDITORS)) {
                homeNavigator.navigateWithEditorsPosition(click.getApp()
                    .getAppId(), click.getApp()
                    .getPackageName(), "", "", click.getApp()
                    .getTag(), String.valueOf(click.getAppPosition()));
              } else {
                homeNavigator.navigateToAppView(app.getAppId(), app.getPackageName(), app.getTag());
              }
            })
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(homeClick -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @VisibleForTesting public void handleRecommendedAppClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.recommendedAppClicked()
            .observeOn(viewScheduler)
            .doOnNext(click -> homeNavigator.navigateToRecommendsAppView(click.getApp()
                .getAppId(), click.getApp()
                .getPackageName(), click.getApp()
                .getTag(), click.getType()))
            .doOnNext(click -> homeAnalytics.sendRecommendedAppInteractEvent(click.getApp()
                .getRating(), click.getApp()
                .getPackageName(), click.getBundlePosition(), click.getBundle()
                .getTag(), ((SocialBundle) click.getBundle()).getCardType(), click.getType()))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(homeClick -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void handleEditorialCardClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.editorialCardClicked()
            .observeOn(viewScheduler)
            .doOnNext(click -> {
              homeAnalytics.sendEditorialInteractEvent(click.getBundle()
                  .getTag(), click.getBundlePosition(), click.getCardId());
              homeNavigator.navigateToEditorial(click.getCardId());
            })
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(homeClick -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void handleBottomNavigationEvents() {
    view.getLifecycleEvent()
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
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.adClicked()
            .doOnNext(adHomeEvent -> homeAnalytics.sendAdClickEvent(adHomeEvent.getAdClick()
                .getAd()
                .getStars(), adHomeEvent.getAdClick()
                .getAd()
                .getPackageName(), adHomeEvent.getBundlePosition(), adHomeEvent.getBundle()
                .getTag(), adHomeEvent.getType(), ApplicationAd.Network.SERVER))
            .map(adHomeEvent -> adHomeEvent.getAdClick())
            .map(adMapper.mapAdToSearchAd())
            .observeOn(viewScheduler)
            .doOnError(throwable -> Logger.getInstance()
                .e(this.getClass()
                    .getCanonicalName(), throwable))
            .doOnNext(homeNavigator::navigateToAppView)
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
            .doOnNext(homeMoreClick -> homeAnalytics.sendTapOnMoreInteractEvent(
                homeMoreClick.getBundlePosition(), homeMoreClick.getBundle()
                    .getTag(), homeMoreClick.getBundle()
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
    return home.loadFreshHomeBundles()
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
            .doOnNext(click -> view.showLoading())
            .flatMapSingle(click -> loadNextBundles())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, crashReporter::log);
  }

  @VisibleForTesting public void loadUserImage() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> accountManager.accountStatus())
        .flatMap(account -> getUserAvatar(account))
        .observeOn(viewScheduler)
        .doOnNext(userAvatarUrl -> {
          if (userAvatarUrl != null) {
            view.setUserImage(userAvatarUrl);
          } else {
            view.setDefaultUserImage();
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
    view.getLifecycleEvent()
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

  private void handleLoggedInAcceptTermsAndConditions() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> accountManager.accountStatus()
            .first())
        .filter(Account::isLoggedIn)
        .filter(
            account -> !(account.acceptedPrivacyPolicy() && account.acceptedTermsAndConditions()))
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.showTermsAndConditionsDialog())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void handleTermsAndConditionsContinueClicked() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.gdprDialogClicked())
        .filter(action -> action.equals("continue"))
        .flatMapCompletable(__ -> accountManager.updateTermsAndConditions())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void handleTermsAndConditionsLogOutClicked() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.gdprDialogClicked())
        .filter(action -> action.equals("logout"))
        .flatMapCompletable(__ -> accountManager.logout())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void handleClickOnTermsAndConditions() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.gdprDialogClicked())
        .filter(action -> action.equals("terms"))
        .doOnNext(__ -> homeNavigator.navigateToTermsAndConditions())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          throw new OnErrorNotImplementedException(err);
        });
  }

  private void handleClickOnPrivacyPolicy() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.gdprDialogClicked())
        .filter(action -> action.equals("privacy"))
        .doOnNext(__ -> homeNavigator.navigateToPrivacyPolicy())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          throw new OnErrorNotImplementedException(err);
        });
  }

  private void checkForPromotionApps() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMapSingle(__ -> home.hasPromotionApps())
        .filter(HomePromotionsWrapper::hasPromotions)
        .observeOn(viewScheduler)
        .doOnNext(apps -> {
          view.showPromotionsHomeIcon(apps);
          view.showPromotionsHomeDialog();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          throw new OnErrorNotImplementedException(err);
        });
  }

  private void handleClickOnPromotionsDialogContinue() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.promotionsDialogContinueClicked())
        .doOnNext(__ -> homeNavigator.navigateToPromotions())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          throw new OnErrorNotImplementedException(err);
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
