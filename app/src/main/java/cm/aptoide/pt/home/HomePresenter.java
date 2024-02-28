package cm.aptoide.pt.home;

import android.util.Pair;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import cm.aptoide.pt.UserFeedbackAnalytics;
import cm.aptoide.pt.ads.data.ApplicationAd;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.home.bundles.HomeBundlesModel;
import cm.aptoide.pt.home.bundles.ads.AdMapper;
import cm.aptoide.pt.home.bundles.apps.RewardApp;
import cm.aptoide.pt.home.bundles.base.ActionBundle;
import cm.aptoide.pt.home.bundles.base.AppComingSoonPromotionalBundle;
import cm.aptoide.pt.home.bundles.base.HomeBundle;
import cm.aptoide.pt.home.bundles.base.HomeEvent;
import cm.aptoide.pt.home.bundles.base.PromotionalBundle;
import cm.aptoide.pt.home.bundles.editorial.EditorialHomeEvent;
import cm.aptoide.pt.home.more.eskills.EskillsAnalytics;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.reactions.network.ReactionsResponse;
import cm.aptoide.pt.view.app.Application;
import java.util.Collections;
import java.util.List;
import rx.Observable;
import rx.Scheduler;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorNotImplementedException;

import static cm.aptoide.pt.home.bundles.base.HomeBundle.BundleType.APPCOINS_ADS;
import static cm.aptoide.pt.home.bundles.base.HomeBundle.BundleType.EDITORIAL;
import static cm.aptoide.pt.home.bundles.base.HomeBundle.BundleType.EDITORS;
import static cm.aptoide.pt.home.bundles.base.HomeBundle.BundleType.ESKILLS;

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
  private final HomeAnalytics homeAnalytics;
  private final EskillsAnalytics eskillsAnalytics;
  private final UserFeedbackAnalytics userFeedbackAnalytics;

  public HomePresenter(HomeView view, Home home, Scheduler viewScheduler, CrashReport crashReporter,
      HomeNavigator homeNavigator, AdMapper adMapper, HomeAnalytics homeAnalytics,
      UserFeedbackAnalytics userFeedbackAnalytics, EskillsAnalytics eskillsAnalytics) {
    this.view = view;
    this.home = home;
    this.viewScheduler = viewScheduler;
    this.crashReporter = crashReporter;
    this.homeNavigator = homeNavigator;
    this.adMapper = adMapper;
    this.homeAnalytics = homeAnalytics;
    this.userFeedbackAnalytics = userFeedbackAnalytics;
    this.eskillsAnalytics = eskillsAnalytics;
  }

  @Override public void present() {
    onCreateLoadBundles();

    handleAppClick();

    handleAdClick();

    handleMoreClick();

    handleBottomReached();

    handlePullToRefresh();

    handleRetryClick();

    handleBundleScrolledRight();

    handleKnowMoreClick();

    handleDismissClick();

    handleActionBundlesImpression();

    handleEditorialCardClick();

    handleInstallWalletOfferClick();

    handleReactionButtonClick();

    handleLongPressedReactionButton();

    handleUserReaction();

    handleSnackLogInClick();

    handleLoadMoreErrorRetry();

    handlePromotionalImpression();

    handlePromotionalClick();

    handleESkillsMoreClick();

    handleESkillsClick();

    handleNotifyMeAppComingSoonClick();

    handleCancelNotifyMeAppComingSoonClick();
  }

  private void handleLoadMoreErrorRetry() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.onLoadMoreRetryClicked())
        .doOnNext(__ -> view.removeLoadMoreError())
        .doOnNext(__ -> view.showLoadMore())
        .flatMap(__ -> loadNextBundlesAndReactions())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReporter.log(throwable));
  }

  private void handleInstallWalletOfferClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.walletOfferCardInstallWalletClick())
        .observeOn(viewScheduler)
        .doOnNext(event -> homeAnalytics.sendActionItemTapOnCardInteractEvent(event.getBundle()
            .getTag(), event.getBundlePosition()))
        .map(HomeEvent::getBundle)
        .filter(homeBundle -> homeBundle instanceof ActionBundle)
        .cast(ActionBundle.class)
        .doOnNext(bundle -> view.sendDeeplinkToWalletAppView(bundle.getActionItem()
            .getUrl()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(lifecycleEvent -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @VisibleForTesting public void handleLongPressedReactionButton() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.reactionButtonLongPress())
        .doOnNext(homeEvent -> {
          homeAnalytics.sendReactionButtonClickEvent();
          view.showReactionsPopup(homeEvent.getCardId(), homeEvent.getGroupId(),
              homeEvent.getBundlePosition());
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(lifecycleEvent -> {
        }, crashReporter::log);
  }

  private Single<List<HomeBundle>> loadReactionModel(String cardId, String groupId,
      HomeBundlesModel homeBundlesModel) {
    return home.loadReactionModel(cardId, groupId, homeBundlesModel)
        .observeOn(viewScheduler)
        .doOnSuccess(homeBundles -> view.updateEditorialCards());
  }

  private Single<List<HomeBundle>> loadReactionModel(String cardId, String groupId) {
    return home.loadReactionModel(cardId, groupId)
        .observeOn(viewScheduler)
        .doOnSuccess(homeBundles -> view.updateEditorialCards());
  }

  private Observable<List<HomeBundle>> loadHomeAndReactions() {
    return loadHome().flatMap(homeBundlesModel -> Observable.from(homeBundlesModel.getList())
        .filter(actionBundle -> actionBundle.getType() == EDITORIAL)
        .filter(homeBundle -> homeBundle instanceof ActionBundle)
        .cast(ActionBundle.class)
        .filter(actionBundle -> actionBundle.getActionItem() != null)
        .flatMapSingle(actionBundle -> loadReactionModel(actionBundle.getActionItem()
            .getCardId(), actionBundle.getActionItem()
            .getType(), homeBundlesModel)));
  }

  private Observable<List<HomeBundle>> loadFreshBundlesAndReactions() {
    return loadFreshBundles().first()
        .flatMapIterable(HomeBundlesModel::getList)
        .filter(actionBundle -> actionBundle.getType() == EDITORIAL)
        .filter(homeBundle -> homeBundle instanceof ActionBundle)
        .cast(ActionBundle.class)
        .flatMapSingle(actionBundle -> loadReactionModel(actionBundle.getActionItem()
            .getCardId(), actionBundle.getActionItem()
            .getType()));
  }

  private Observable<List<HomeBundle>> loadNextBundlesAndReactions() {
    return loadNextBundles().toObservable()
        .flatMapIterable(HomeBundlesModel::getList)
        .filter(actionBundle -> actionBundle.getType() == EDITORIAL)
        .filter(homeBundle -> homeBundle instanceof ActionBundle)
        .cast(ActionBundle.class)
        .flatMapSingle(actionBundle -> loadReactionModel(actionBundle.getActionItem()
            .getCardId(), actionBundle.getActionItem()
            .getType()));
  }

  public void handlePromotionalImpression() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.visibleBundles())
        .filter(
            homeEvent -> homeEvent.getBundle() instanceof PromotionalBundle && homeEvent.getBundle()
                .getType()
                .isPromotional())
        .doOnNext(homeEvent -> {
          HomeBundle bundle = homeEvent.getBundle();
          homeAnalytics.sendPromotionalAppImpressionEvent(bundle.getType()
              .name(), ((PromotionalBundle) bundle).getApp()
              .getPackageName());
          homeAnalytics.sendPromotionalAppHomeInteractImpressionEvent(bundle.getTag(),
              homeEvent.getBundlePosition());
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(actionBundle -> {
        }, crashReporter::log);

    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.visibleBundles())
        .filter(homeEvent -> homeEvent.getBundle() instanceof ActionBundle && homeEvent.getBundle()
            .getType()
            .isPromotional())
        .doOnNext(homeEvent -> {
          ActionBundle bundle = ((ActionBundle) homeEvent.getBundle());
          if (bundle.getActionItem() != null) {
            homeAnalytics.sendPromotionalArticleImpressionEvent(bundle.getType()
                .name(), bundle.getActionItem()
                .getCardId());
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(actionBundle -> {
        }, crashReporter::log);
  }

  private void handlePromotionalClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.appClicked())
        .filter(homeEvent -> homeEvent.getBundle() instanceof PromotionalBundle)
        .doOnNext(homeEvent -> {
          HomeBundle bundle = homeEvent.getBundle();
          homeAnalytics.sendPromotionalAppClickEvent(bundle.getType()
              .name(), ((PromotionalBundle) bundle).getApp()
              .getPackageName());
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(actionBundle -> {
        }, crashReporter::log);

    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.editorialCardClicked())
        .filter(homeEvent -> homeEvent.getBundle() instanceof ActionBundle && homeEvent.getBundle()
            .getType()
            .isPromotional())
        .doOnNext(homeEvent -> {
          HomeBundle bundle = homeEvent.getBundle();
          homeAnalytics.sendPromotionalArticleClickEvent(bundle.getType()
              .name(), ((ActionBundle) bundle).getActionItem()
              .getCardId());
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(actionBundle -> {
        }, crashReporter::log);
  }

  @VisibleForTesting public void handleActionBundlesImpression() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.visibleBundles())
        .filter(homeEvent -> homeEvent.getBundle() instanceof ActionBundle)
        .doOnNext(homeEvent -> {
          if (homeEvent.getBundle()
              .getType()
              .equals(HomeBundle.BundleType.INFO_BUNDLE) || homeEvent.getBundle()
              .getType()
              .equals(HomeBundle.BundleType.WALLET_ADS_OFFER) || homeEvent.getBundle()
              .getType()
              .isPromotional()) {
            homeAnalytics.sendActionItemImpressionEvent(homeEvent.getBundle()
                .getTag(), homeEvent.getBundlePosition());
          } else {
            ActionBundle actionBundle = (ActionBundle) homeEvent.getBundle();
            if (actionBundle.getActionItem() != null) {
              homeAnalytics.sendEditorialImpressionEvent(actionBundle.getTag(),
                  homeEvent.getBundlePosition(), actionBundle.getActionItem()
                      .getCardId());
              homeAnalytics.sendActionItemEditorialImpressionEvent(actionBundle.getTag(),
                  homeEvent.getBundlePosition(), actionBundle.getActionItem()
                      .getCardId());
            }
          }
        })
        .filter(homeEvent -> homeEvent.getBundle()
            .getType()
            .equals(HomeBundle.BundleType.INFO_BUNDLE) || homeEvent.getBundle()
            .getType()
            .equals(HomeBundle.BundleType.WALLET_ADS_OFFER))
        .map(HomeEvent::getBundle)
        .cast(ActionBundle.class)
        .flatMapCompletable(home::actionBundleImpression)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(actionBundle -> {
        }, throwable -> {
          crashReporter.log(throwable);
        });
  }

  @VisibleForTesting public void handleKnowMoreClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.infoBundleKnowMoreClicked())
        .observeOn(viewScheduler)
        .doOnNext(homeEvent -> {
          homeAnalytics.sendActionItemTapOnCardInteractEvent(homeEvent.getBundle()
              .getTag(), homeEvent.getBundlePosition());
          homeNavigator.navigateToAppCoinsInformationView();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(lifecycleEvent -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @VisibleForTesting public void handleESkillsMoreClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.eSkillsKnowMoreClick())
        .observeOn(viewScheduler)
        .doOnNext(homeEvent -> {
          homeAnalytics.sendTapOnMoreInteractEvent(homeEvent.getBundlePosition(), homeEvent.getBundle()
              .getTag(), homeEvent.getBundle()
              .getContent()
              .size());
          eskillsAnalytics.sendHomeBundleMoreClickEvent();
          homeNavigator.navigateToEskillsEarnMore(homeEvent);
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(lifecycleEvent -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @VisibleForTesting public void handleESkillsClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.eSkillsClick())
        .observeOn(viewScheduler)
        .doOnNext(homeEvent -> {
          homeAnalytics.sendActionItemTapOnCardInteractEvent(homeEvent.getBundle()
              .getTag(), homeEvent.getBundlePosition());
          eskillsAnalytics.sendHomeBundleHeaderClickEvent();
          homeNavigator.navigateToEskillsEarnMore(homeEvent);
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(lifecycleEvent -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }



  @VisibleForTesting public void handleReactionButtonClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.reactionsButtonClicked())
        .observeOn(viewScheduler)
        .flatMap(this::singlePressReactionButtonAction)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(lifecycleEvent -> {
        }, throwable -> crashReporter.log(throwable));
  }

  @VisibleForTesting public void handleUserReaction() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.reactionClicked())
        .doOnNext(__ -> userFeedbackAnalytics.sendReactionEvent())
        .flatMap(homeEvent -> home.setReaction(homeEvent.getCardId(), homeEvent.getGroupId(),
            homeEvent.getReaction())
            .toObservable()
            .filter(ReactionsResponse::differentReaction)
            .observeOn(viewScheduler)
            .doOnNext(reactionsResponse -> handleReactionsResponse(reactionsResponse, false))
            .filter(ReactionsResponse::wasSuccess)
            .flatMapSingle(__ -> loadReactionModel(homeEvent.getCardId(), homeEvent.getGroupId())))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(lifecycleEvent -> {
        }, crashReporter::log);
  }

  private void handleReactionsResponse(ReactionsResponse reactionsResponse, boolean isDelete) {
    if (reactionsResponse.wasSuccess()) {
      if (isDelete) {
        homeAnalytics.sendDeleteEvent();
      } else {
        homeAnalytics.sendReactedEvent();
      }
    } else if (reactionsResponse.reactionsExceeded()) {
      view.showLogInDialog();
    } else if (reactionsResponse.wasNetworkError()) {
      view.showNetworkErrorToast();
    } else if (reactionsResponse.wasGeneralError()) {
      view.showGenericErrorToast();
    }
  }

  @VisibleForTesting public void handleSnackLogInClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.snackLogInClick())
        .doOnNext(homeEvent -> homeNavigator.navigateToLogIn())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(lifecycleEvent -> {
        }, crashReporter::log);
  }

  @VisibleForTesting public void handleDismissClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.dismissBundleClicked())
        .filter(homeEvent -> homeEvent.getBundle() instanceof ActionBundle)
        .doOnNext(homeEvent -> homeAnalytics.sendActionItemDismissInteractEvent(
            homeEvent.getBundle()
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
        .flatMap(__ -> loadHomeAndReactions())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReporter::log);
  }

  private Observable<HomeBundlesModel> loadHome() {
    return home.loadHomeBundles()
        .cast(HomeBundlesModel.class)
        .observeOn(viewScheduler)
        .doOnNext(view::showBundlesSkeleton)
        .filter(HomeBundlesModel::isComplete)
        .observeOn(viewScheduler)
        .doOnNext(this::handleBundlesResult);
  }

  private void handleBundlesResult(HomeBundlesModel bundlesModel) {
    if (bundlesModel.hasErrors()) {
      handleError(bundlesModel.getError());
    } else if (!bundlesModel.isLoading()) {
      view.showBundles(bundlesModel.getList());
      view.hideLoading();
    }
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
              } else if (click.getBundle()
                  .getType()
                  .equals(APPCOINS_ADS)) {
                RewardApp rewardApp = (RewardApp) app;
                homeAnalytics.convertAppcAdClick(rewardApp.getClickUrl());
                homeNavigator.navigateWithDownloadUrlAndReward(rewardApp.getAppId(),
                    rewardApp.getPackageName(), rewardApp.getTag(), rewardApp.getDownloadUrl(),
                    (float) rewardApp.getReward()
                        .getAppc());
              } else if (click.getBundle()
                  .getType()
                  .equals(ESKILLS)) {
                homeNavigator.navigateToEskillsAppView(app.getAppId(), app.getPackageName(),
                    app.getTag());
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

  private void handleEditorialCardClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.editorialCardClicked()
            .observeOn(viewScheduler)
            .doOnNext(click -> {
              homeAnalytics.sendEditorialInteractEvent(click.getBundle()
                  .getTag(), click.getBundlePosition(), click.getCardId());
              homeAnalytics.sendActionItemTapOnCardInteractEvent(click.getBundle()
                  .getTag(), click.getBundlePosition(), click.getCardId());
              homeNavigator.navigateToEditorial(click.getCardId());
            })
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(homeClick -> {
        }, crashReporter::log);
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
            .map(adMapper::mapAdToSearchAd)
            .observeOn(viewScheduler)
            .doOnError(throwable -> Logger.getInstance()
                .e(this.getClass()
                    .getCanonicalName(), throwable))
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
            .flatMap(bottomReached -> loadNextBundlesAndReactions())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(bundles -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @NonNull private Single<HomeBundlesModel> loadNextBundles() {
    return home.loadNextHomeBundles()
        .filter(HomeBundlesModel::isComplete)
        .toSingle()
        .observeOn(viewScheduler)
        .doOnSuccess(bundlesModel -> {
          homeAnalytics.sendLoadMoreInteractEvent();
          if (bundlesModel.hasErrors()) {
            handleLoadMoreError();
          } else {
            if (!bundlesModel.isLoading()) {
              view.showMoreHomeBundles(bundlesModel.getList());
              view.hideLoading();
            }
          }
          view.hideShowMore();
        });
  }

  private void handleLoadMoreError() {
    view.showLoadMoreError();
  }

  @VisibleForTesting public void handlePullToRefresh() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.refreshes()
            .doOnNext(__ -> homeAnalytics.sendPullRefreshInteractEvent())
            .flatMap(refreshed -> loadFreshBundlesAndReactions())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(bundles -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @NonNull private Observable<HomeBundlesModel> loadFreshBundles() {
    return home.loadFreshHomeBundles()
        .filter(HomeBundlesModel::isComplete)
        .observeOn(viewScheduler)
        .doOnNext(bundlesModel -> {
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
            .flatMap(click -> loadNextBundlesAndReactions())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, crashReporter::log);
  }

  private Observable<List<HomeBundle>> singlePressReactionButtonAction(
      EditorialHomeEvent editorialHomeEvent) {
    return home.isFirstReaction(editorialHomeEvent.getCardId(), editorialHomeEvent.getGroupId())
        .flatMapObservable(firstReaction -> {
          if (firstReaction) {
            homeAnalytics.sendReactionButtonClickEvent();
            view.showReactionsPopup(editorialHomeEvent.getCardId(), editorialHomeEvent.getGroupId(),
                editorialHomeEvent.getBundlePosition());
            return Observable.just(Collections.emptyList());
          } else {
            return home.deleteReaction(editorialHomeEvent.getCardId(),
                editorialHomeEvent.getGroupId())
                .toObservable()
                .doOnNext(reactionsResponse -> handleReactionsResponse(reactionsResponse, true))
                .filter(ReactionsResponse::wasSuccess)
                .flatMapSingle(__ -> loadReactionModel(editorialHomeEvent.getCardId(),
                    editorialHomeEvent.getGroupId()));
          }
        });
  }

  @VisibleForTesting public void handleNotifyMeAppComingSoonClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.notifyMeClicked())
        .filter(homeEvent -> homeEvent.getBundle() instanceof AppComingSoonPromotionalBundle)
        .map(event -> new Pair<>(event.getBundlePosition(),
            ((AppComingSoonPromotionalBundle) event.getBundle())))
        .doOnNext(event -> {
          homeAnalytics.sendPromotionalArticleClickEvent(event.second.getType()
              .name(), event.second.getActionItem()
              .getCardId());
          homeAnalytics.sendActionItemTapOnCardInteractEvent(event.second.getTag(), event.first,
              event.second.getActionItem()
                  .getCardId());
        })
        .map(event -> event.second)
        .flatMap(bundle -> home.setupAppComingSoonNotification(bundle.getActionItem()
            .getPackageName())
            .andThen(Observable.just(bundle)))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(bundle -> view.updateAppComingSoonStatus(bundle, true))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(lifecycleEvent -> {
        }, Throwable::printStackTrace);
  }

  private void handleCancelNotifyMeAppComingSoonClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.cancelNotifyMeClicked())
        .filter(homeEvent -> homeEvent.getBundle() instanceof AppComingSoonPromotionalBundle)
        .map(event -> new Pair<>(event.getBundlePosition(),
            ((AppComingSoonPromotionalBundle) event.getBundle())))
        .doOnNext(event -> {
          homeAnalytics.sendPromotionalArticleClickEvent(event.second.getType()
              .name(), event.second.getActionItem()
              .getCardId());
          homeAnalytics.sendActionItemTapOnCardInteractEvent(event.second.getTag(), event.first,
              event.second.getActionItem()
                  .getCardId());
        })
        .map(event -> event.second)
        .flatMap(bundle -> home.cancelAppComingSoonNotification(bundle.getActionItem()
            .getPackageName())
            .andThen(Observable.just(bundle)))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(homeBundle -> view.updateAppComingSoonStatus(homeBundle, false))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(lifecycleEvent -> {
        }, Throwable::printStackTrace);
  }
}
