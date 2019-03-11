package cm.aptoide.pt.app.view;

import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.text.format.DateUtils;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.ads.data.ApplicationAd;
import cm.aptoide.pt.ads.data.AptoideNativeAd;
import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.app.AppViewManager;
import cm.aptoide.pt.app.AppViewSimilarApp;
import cm.aptoide.pt.app.AppViewViewModel;
import cm.aptoide.pt.app.CampaignAnalytics;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.app.ReviewsViewModel;
import cm.aptoide.pt.app.SimilarAppsViewModel;
import cm.aptoide.pt.app.view.similar.SimilarAppsBundle;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.share.ShareDialogs;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorNotImplementedException;
import rx.schedulers.Schedulers;

/**
 * Created by franciscocalado on 08/05/18.
 */

public class AppViewPresenter implements Presenter {
  private static final long TIME_BETWEEN_SCROLL = 2 * DateUtils.SECOND_IN_MILLIS;
  private static final String TAG = AppViewPresenter.class.getSimpleName();

  private final PermissionManager permissionManager;
  private final PermissionService permissionService;
  private AppViewView view;
  private AccountNavigator accountNavigator;
  private AppViewAnalytics appViewAnalytics;
  private CampaignAnalytics campaignAnalytics;
  private AppViewNavigator appViewNavigator;
  private AppViewManager appViewManager;
  private AptoideAccountManager accountManager;
  private Scheduler viewScheduler;
  private CrashReport crashReport;

  public AppViewPresenter(AppViewView view, AccountNavigator accountNavigator,
      AppViewAnalytics appViewAnalytics, CampaignAnalytics campaignAnalytics,
      AppViewNavigator appViewNavigator, AppViewManager appViewManager,
      AptoideAccountManager accountManager, Scheduler viewScheduler, CrashReport crashReport,
      PermissionManager permissionManager, PermissionService permissionService) {
    this.view = view;
    this.accountNavigator = accountNavigator;
    this.appViewAnalytics = appViewAnalytics;
    this.campaignAnalytics = campaignAnalytics;
    this.appViewNavigator = appViewNavigator;
    this.appViewManager = appViewManager;
    this.accountManager = accountManager;
    this.viewScheduler = viewScheduler;
    this.crashReport = crashReport;
    this.permissionManager = permissionManager;
    this.permissionService = permissionService;
  }

  @Override public void present() {
    handleFirstLoad();
    handleReviewAutoScroll();
    handleClickOnScreenshot();
    handleClickOnVideo();
    handleClickOnDescriptionReadMore();
    handleClickOnDeveloperWebsite();
    handleClickOnDeveloperEmail();
    handleClickOnDeveloperPrivacy();
    handleClickOnDeveloperPermissions();
    handleClickOnStoreLayout();
    handleClickOnFollowStore();
    handleClickOnOtherVersions();
    handleClickOnTrustedBadge();
    handleClickOnRateApp();
    handleClickReadReviews();
    handleClickFlags();
    handleClickLoginSnack();
    handleClickOnAppcInfo();
    handleClickOnSimilarApps();
    handleClickOnToolbar();
    handleDefaultShare();
    handleRecommendsShare();
    handleClickOnRetry();
    handleOnScroll();
    handleOnSimilarAppsVisible();

    handleInstallButtonClick();
    pauseDownload();
    resumeDownload();
    cancelDownload();
    loadDownloadApp();
    shareLoggedInRecommendsDialogClick();
    skipLoggedInRecommendsDialogClick();
    dontShowAgainLoggedInRecommendsDialogClick();
    handleNotLoggedinShareResults();
    handleAppBought();
    handleApkfyDialogPositiveClick();
    handleClickOnTopDonorsDonate();
    handleDonateCardImpressions();

    handleInterstitialAdClick();
    handleInterstitialAdLoaded();
    showInterstitialAd();

    handleDismissWalletPromotion();
  }

  private Completable showBannerAd() {
    return appViewManager.shouldLoadBannerAd()
        .observeOn(viewScheduler)
        .flatMapCompletable(loadBanner -> {
          if (loadBanner) {
            view.showBannerAd();
          }
          return Completable.complete();
        });
  }

  private void showInterstitialAd() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(created -> view.installAppClick())
        .flatMap(create -> appViewManager.loadAppViewViewModel()
            .toObservable())
        .filter(app -> !app.isLoading())
        .filter(app -> !app.isAppCoinApp())
        .flatMap(app -> appViewManager.loadDownloadAppViewModel(app.getMd5(), app.getPackageName(),
            app.getVersionCode(), app.isPaid(), app.getPay())
            .filter(model -> model.getDownloadModel()
                .isDownloading())
            .first()
            .observeOn(Schedulers.io())
            .flatMapSingle(model -> appViewManager.shouldLoadInterstitialAd())
            .filter(loadInterstitial -> loadInterstitial)
            .observeOn(viewScheduler)
            .doOnNext(__ -> view.initInterstitialAd())
            .delay(1, TimeUnit.SECONDS)
            .observeOn(viewScheduler)
            .doOnNext(__ -> view.loadInterstitialAd()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new OnErrorNotImplementedException(error);
        });
  }

  private void handleInterstitialAdLoaded() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.interstitialAdLoaded())
        .doOnNext(__ -> view.showInterstitialAd())
        .doOnNext(__ -> appViewAnalytics.installInterstitialImpression())
        .observeOn(Schedulers.io())
        .flatMapSingle(__ -> appViewManager.recordInterstitialImpression())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleInterstitialAdClick() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.InterstitialAdClicked())
        .doOnNext(__ -> appViewAnalytics.installInterstitialClick())
        .observeOn(Schedulers.io())
        .flatMapSingle(__ -> appViewManager.recordInterstitialClick())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleOnSimilarAppsVisible() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.similarAppsVisibility())
        .observeOn(Schedulers.io())
        .doOnNext(similarAppsVisible -> {
          SimilarAppsViewModel similarAppsViewModel =
              appViewManager.getCachedSimilarAppsViewModel();
          if (similarAppsViewModel != null
              && similarAppsViewModel.hasAd()
              && !similarAppsViewModel.hasRecordedAdImpression()) {
            similarAppsViewModel.setHasRecordedAdImpression(true);
            appViewAnalytics.
                similarAppBundleImpression(similarAppsViewModel.getAd()
                    .getNetwork(), true);
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  @VisibleForTesting public void handleFirstLoad() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> view.showLoading())
        .flatMap(__ -> loadApp().flatMapCompletable(appViewViewModel -> showBannerAd())
            .flatMapSingle(
                appViewViewModel -> manageOrganicAds(appViewViewModel.getMinimalAd()).onErrorReturn(
                    __1 -> null)
                    .map(__1 -> appViewViewModel))
            .filter(app -> app.hasDonations())// after getApk webservice is updated
            .flatMapSingle(app -> appViewManager.getTopDonations(app.getPackageName()))
            .observeOn(viewScheduler)
            .doOnNext(donations -> view.showDonations(donations)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private Single<SearchAdResult> manageOrganicAds(SearchAdResult searchAdResult) {
    if (searchAdResult == null) {
      return appViewManager.loadAdsFromAppView()
          .doOnSuccess(ad -> {
            appViewManager.setSearchAdResult(ad);
            handleAdsLogic(appViewManager.getSearchAdResult());
          })
          .doOnError(throwable -> crashReport.log(throwable));
    }
    return Single.just(null);
  }

  private void handleOnScroll() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(lifecycleEvent -> view.scrollVisibleSimilarApps())
        .takeUntil(__ -> view.isSimilarAppsVisible())
        .observeOn(Schedulers.io())
        .doOnNext(__ -> {
          SimilarAppsViewModel similarAppsViewModel =
              appViewManager.getCachedSimilarAppsViewModel();
          if (similarAppsViewModel != null
              && similarAppsViewModel.getAd() != null
              && !similarAppsViewModel.hasRecordedAdImpression()) {
            similarAppsViewModel.setHasRecordedAdImpression(true);
            appViewAnalytics.similarAppBundleImpression(similarAppsViewModel.getAd()
                .getNetwork(), true);
          }
          appViewAnalytics.similarAppBundleImpression(null, false);
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleAdsLogic(SearchAdResult searchAdResult) {
    appViewManager.handleAdsLogic(searchAdResult);
    view.extractReferrer(searchAdResult);
  }

  private void handleReviewAutoScroll() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.scrollReviewsResponse())
        .flatMap(reviews -> scheduleAnimations(reviews))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleClickOnScreenshot() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.getScreenshotClickEvent())
        .filter(event -> !event.isVideo())
        .doOnNext(imageClick -> {
          appViewAnalytics.sendOpenScreenshotEvent();
          appViewNavigator.navigateToScreenshots(imageClick.getImagesUris(),
              imageClick.getImagesIndex());
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnVideo() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.getScreenshotClickEvent())
        .filter(event -> event.isVideo())
        .doOnNext(videoClick -> {
          appViewAnalytics.sendOpenVideoEvent();
          appViewNavigator.navigateToUri(videoClick.getUri());
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnDescriptionReadMore() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickedReadMore())
        .doOnNext(readMoreClickEvent -> {
          appViewAnalytics.sendReadMoreEvent();
          appViewNavigator.navigateToDescriptionReadMore(readMoreClickEvent.getStoreName(),
              readMoreClickEvent.getDescription(), readMoreClickEvent.getStoreTheme());
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnDeveloperWebsite() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickDeveloperWebsite())
        .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
        .filter(app -> !TextUtils.isEmpty(app.getDeveloper()
            .getWebsite()))
        .doOnNext(app -> view.navigateToDeveloperWebsite(app))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnAppcInfo() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickGetAppcInfo())
        .doOnNext(click -> {
          appViewAnalytics.sendAppcInfoInteractEvent();
          appViewNavigator.navigateToAppCoinsInfo();
        })
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void handleClickOnDeveloperEmail() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickDeveloperEmail())
        .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
        .filter(app -> !TextUtils.isEmpty(app.getDeveloper()
            .getEmail()))
        .doOnNext(app -> view.navigateToDeveloperEmail(app))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnDeveloperPrivacy() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickDeveloperPrivacy())
        .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
        .filter(app -> !TextUtils.isEmpty(app.getDeveloper()
            .getPrivacy()))
        .doOnNext(app -> view.navigateToDeveloperPrivacy(app))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnDeveloperPermissions() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickDeveloperPermissions())
        .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
        .doOnNext(app -> view.navigateToDeveloperPermissions(app))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnStoreLayout() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickStoreLayout())
        .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
        .doOnNext(app -> {
          appViewAnalytics.sendStoreOpenEvent(app.getStore());
          appViewAnalytics.sendOpenStoreEvent();
          appViewNavigator.navigateToStore(app.getStore());
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnFollowStore() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickFollowStore())
        .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
        .observeOn(viewScheduler)
        .flatMapCompletable(model -> {
          if (model.isStoreFollowed()) {
            view.setFollowButton(true);
            appViewAnalytics.sendOpenStoreEvent();
            appViewNavigator.navigateToStore(model.getStore());
            return Completable.complete();
          } else {
            view.setFollowButton(false);
            appViewAnalytics.sendFollowStoreEvent();
            view.displayStoreFollowedSnack(model.getStore()
                .getName());
            return appViewManager.subscribeStore(model.getStore()
                .getName());
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          throw new OnErrorNotImplementedException(err);
        });
  }

  private void handleClickOnOtherVersions() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickOtherVersions())
        .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
        .doOnNext(model -> {
          appViewAnalytics.sendOtherVersionsEvent();
          appViewNavigator.navigateToOtherVersions(model.getAppName(), model.getIcon(),
              model.getPackageName());
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnTrustedBadge() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickTrustedBadge())
        .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
        .doOnNext(model -> {
          appViewAnalytics.sendBadgeClickEvent();
          view.showTrustedDialog(model);
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnRateApp() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> Observable.merge(view.clickRateApp(), view.clickRateAppLarge(),
            view.clickRateAppLayout()))
        .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
        .observeOn(viewScheduler)
        .doOnNext(model -> appViewAnalytics.sendRateThisAppEvent())
        .flatMap(model -> view.showRateDialog(model.getAppName(), model.getPackageName(),
            model.getStore()
                .getName()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickReadReviews() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> Observable.merge(view.clickReviewsLayout(), view.clickReadAllReviews()))
        .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
        .doOnNext(model -> {
          appViewAnalytics.sendReadAllEvent();
          appViewNavigator.navigateToRateAndReview(model.getAppId(), model.getAppName(),
              model.getStore()
                  .getName(), model.getPackageName(), model.getStore()
                  .getAppearance()
                  .getTheme());
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickFlags() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> Observable.merge(view.clickVirusFlag(), view.clickLicenseFlag(),
            view.clickWorkingFlag(), view.clickFakeFlag()))
        .doOnNext(type -> view.disableFlags())
        .flatMap(type -> accountManager.accountStatus()
            .first()
            .observeOn(viewScheduler)
            .flatMap(account -> {
              if (!account.isLoggedIn()) {
                view.enableFlags();
                view.displayNotLoggedInSnack();
                return Observable.just(false);
              } else {
                return Observable.just(true);
              }
            })
            .filter(isLoggedIn -> isLoggedIn)
            .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
            .flatMapSingle(model -> appViewManager.flagApk(model.getStore()
                .getName(), model.getMd5(), type))
            .filter(result -> result)
            .observeOn(viewScheduler)
            .doOnNext(__ -> {
              view.incrementFlags(type);
              view.showFlagVoteSubmittedMessage();
            }))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          view.enableFlags();
          crashReport.log(err);
        });
  }

  private void handleClickLoginSnack() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickLoginSnack())
        .doOnNext(__ -> accountNavigator.navigateToAccountView(
            AccountAnalytics.AccountOrigins.APP_VIEW_FLAG))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnSimilarApps() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickSimilarApp())
        .observeOn(viewScheduler)
        .flatMap(similarAppClickEvent -> {
          boolean isAd = false;
          ApplicationAd.Network network = null;
          String packageName;
          AppViewSimilarApp appViewSimilarApp = similarAppClickEvent.getSimilar();

          if (appViewSimilarApp.isAd()) {
            isAd = true;
            network = appViewSimilarApp.getAd()
                .getNetwork();
            packageName = appViewSimilarApp.getAd()
                .getPackageName();
            if (appViewSimilarApp.getAd()
                .getNetwork() == ApplicationAd.Network.SERVER) {
              appViewNavigator.navigateToAd((AptoideNativeAd) appViewSimilarApp.getAd(),
                  similarAppClickEvent.getType());
            }
          } else {
            packageName = appViewSimilarApp.getApp()
                .getPackageName();
            appViewNavigator.navigateToAppView(appViewSimilarApp.getApp()
                .getAppId(), packageName, similarAppClickEvent.getType());
          }
          appViewAnalytics.sendSimilarAppsInteractEvent(similarAppClickEvent.getType());
          appViewAnalytics.similarAppClick(network, packageName, similarAppClickEvent.getPosition(),
              isAd);
          return Observable.just(isAd);
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnToolbar() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickToolbar()
            .flatMap(menuItem -> appViewManager.loadAppViewViewModel()
                .toObservable()
                .filter(appViewViewModel -> menuItem != null)
                .observeOn(viewScheduler)
                .doOnNext(appViewViewModel -> {
                  switch (menuItem.getItemId()) {

                    case R.id.menu_item_share:
                      view.showShareDialog();
                      break;

                    case R.id.menu_remote_install:
                      appViewAnalytics.sendRemoteInstallEvent();
                      view.showShareOnTvDialog(appViewViewModel.getAppId());
                      break;
                  }
                })))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void handleDefaultShare() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.shareDialogResponse())
        .filter(response -> response == ShareDialogs.ShareResponse.SHARE_EXTERNAL)
        .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
        .observeOn(viewScheduler)
        .doOnNext(app -> view.defaultShare(app.getAppName(), app.getWebUrls()))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void handleRecommendsShare() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.shareDialogResponse())
        .filter(response -> response == ShareDialogs.ShareResponse.SHARE_TIMELINE)
        .flatMap(__ -> accountManager.accountStatus())
        .first()
        .observeOn(viewScheduler)
        .flatMap(account -> {
          if (!account.isLoggedIn()) {
            view.displayNotLoggedInSnack();
            return Observable.just(false);
          } else {
            return Observable.just(true);
          }
        })
        .filter(shouldContinue -> shouldContinue)
        .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
        .observeOn(viewScheduler)
        .doOnNext(appModel -> view.recommendsShare(appModel.getPackageName(), appModel.getStore()
            .getId()))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void handleClickOnRetry() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> Observable.merge(view.clickNoNetworkRetry(), view.clickGenericRetry())
            .doOnNext(__1 -> view.showLoading())
            .flatMap(__2 -> loadApp())
            .retry())
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private Observable<Integer> scheduleAnimations(int topReviewsCount) {
    if (topReviewsCount <= 1) {
      // not enough elements for animation
      Logger.getInstance()
          .w(TAG, "Not enough top reviews to do paging animation.");
      return Observable.empty();
    }

    return Observable.range(0, topReviewsCount)
        .concatMap(pos -> Observable.just(pos)
            .delay(TIME_BETWEEN_SCROLL, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(pos2 -> view.scrollReviews(pos2)));
  }

  private Observable<AppViewViewModel> loadApp() {
    return appViewManager.loadAppViewViewModel()
        .flatMap(appViewViewModel -> appViewManager.loadAppCoinsInformation()
            .andThen(Single.just(appViewViewModel)))
        .flatMap(
            appViewViewModel -> appViewManager.loadDownloadAppViewModel(appViewViewModel.getMd5(),
                appViewViewModel.getPackageName(), appViewViewModel.getVersionCode(),
                appViewViewModel.isPaid(), appViewViewModel.getPay())
                .first()
                .observeOn(viewScheduler)
                .doOnNext(downloadAppViewModel -> view.showDownloadAppModel(downloadAppViewModel,
                    appViewViewModel.hasDonations()))
                .doOnNext(downloadAppViewModel -> view.readyToDownload())
                .doOnNext(model -> {
                  if (model.getAppCoinsViewModel()
                      .hasAdvertising() || model.getAppCoinsViewModel()
                      .hasBilling()) {
                    view.setupAppcAppView();
                  }
                })
                .toSingle()
                .map(downloadAppViewModel -> appViewViewModel))
        .toObservable()
        .observeOn(viewScheduler)
        .doOnNext(appViewModel -> {
          if (appViewModel.hasError()) {
            view.handleError(appViewModel.getError());
          } else {
            view.showAppView(appViewModel);
          }
        })
        .doOnNext(model -> {
          if (!model.getEditorsChoice()
              .isEmpty()) {
            appViewManager.sendEditorsChoiceClickEvent(model.getPackageName(),
                model.getEditorsChoice());
          }
          appViewManager.sendAppViewOpenedFromEvent(model.getPackageName(), model.getDeveloper()
              .getName(), model.getMalware()
              .getRank()
              .name(), model.hasBilling(), model.hasAdvertising());
        })
        .flatMap(appViewModel -> {
          if (appViewModel.getOpenType() == AppViewFragment.OpenType.OPEN_AND_INSTALL) {

            return accountManager.accountStatus()
                .first()
                .observeOn(viewScheduler)
                .flatMapCompletable(account -> downloadApp(DownloadModel.Action.INSTALL,
                    appViewModel).doOnCompleted(
                    () -> appViewAnalytics.clickOnInstallButton(appViewModel.getPackageName(),
                        appViewModel.getDeveloper()
                            .getName(), DownloadModel.Action.INSTALL.toString()))
                    .doOnCompleted(() -> showRecommendsDialog(account.isLoggedIn(),
                        appViewModel.getPackageName()))
                    .observeOn(viewScheduler))
                .map(__ -> appViewModel);
          } else if (appViewModel.getOpenType()
              == AppViewFragment.OpenType.OPEN_WITH_INSTALL_POPUP) {
            return accountManager.accountStatus()
                .first()
                .observeOn(viewScheduler)
                .flatMap(account -> view.showOpenAndInstallDialog(appViewModel.getMarketName(),
                    appViewModel.getAppName())
                    .flatMapCompletable(action -> downloadApp(action, appViewModel).doOnCompleted(
                        () -> appViewAnalytics.clickOnInstallButton(appViewModel.getPackageName(),
                            appViewModel.getDeveloper()
                                .getName(), action.toString()))
                        .doOnCompleted(() -> showRecommendsDialog(account.isLoggedIn(),
                            appViewModel.getPackageName()))
                        .observeOn(viewScheduler)))
                .map(__ -> appViewModel);
          } else if (appViewModel.getOpenType() == AppViewFragment.OpenType.APK_FY_INSTALL_POPUP) {
            return accountManager.accountStatus()
                .first()
                .observeOn(viewScheduler)
                .flatMap(account -> view.showOpenAndInstallApkFyDialog(appViewModel.getMarketName(),
                    appViewModel.getAppName(), appViewModel.getAppc(), appViewModel.getRating()
                        .getAverage(), appViewModel.getIcon(), appViewModel.getPackageDownloads())
                    .flatMapCompletable(
                        action -> downloadApp(action, appViewModel).observeOn(viewScheduler)
                            .doOnCompleted(() -> appViewAnalytics.clickOnInstallButton(
                                appViewModel.getPackageName(), appViewModel.getDeveloper()
                                    .getName(), action.toString()))
                            .doOnCompleted(() -> showRecommendsDialog(account.isLoggedIn(),
                                appViewModel.getPackageName()))
                            .observeOn(viewScheduler)))
                .map(__ -> appViewModel);
          }
          return Observable.just(appViewModel);
        })
        .doOnNext(appViewViewModel -> view.recoverScrollViewState())
        .filter(model -> !model.hasError())
        .flatMap(appViewModel -> Observable.zip(updateSimilarAppsBundles(appViewModel),
            //LOAD WALLET PROMO HERE
            updateReviews(appViewModel),
            (similarAppsBundles, reviewsViewModel) -> Observable.just(appViewModel))
            .first()
            .map(__ -> appViewModel));
  }

  private Observable<List<SimilarAppsBundle>> updateSimilarAppsBundles(
      AppViewViewModel appViewViewModel) {
    return Observable.just(new ArrayList<SimilarAppsBundle>())
        .flatMap(list -> updateSuggestedAppcApps(appViewViewModel, list))
        .flatMap(list -> updateSuggestedApps(appViewViewModel, list))
        .flatMap(list -> sortSuggestedApps(appViewViewModel, list))
        .observeOn(viewScheduler)
        .doOnNext(list -> view.populateSimilar(list));
  }

  private Observable<List<SimilarAppsBundle>> sortSuggestedApps(AppViewViewModel appViewViewModel,
      List<SimilarAppsBundle> list) {
    return Observable.just(list)
        .map(__ -> {
          if (list.size() >= 2) {
            if (appViewViewModel.isAppCoinApp()) {
              if (list.get(0)
                  .getType() == SimilarAppsBundle.BundleType.APPS) {
                Collections.swap(list, 0, 1);
              }
            } else {
              if (list.get(0)
                  .getType() == SimilarAppsBundle.BundleType.APPC_APPS) {
                Collections.swap(list, 0, 1);
              }
            }
          }
          return list;
        });
  }

  private Observable<List<SimilarAppsBundle>> updateSuggestedAppcApps(AppViewViewModel appViewModel,
      List<SimilarAppsBundle> list) {
    return appViewManager.loadAppcSimilarAppsViewModel(appViewModel.getPackageName(),
        appViewModel.getMedia()
            .getKeywords())
        .map(appcAppsViewModel -> {
          if (appcAppsViewModel.hasSimilarApps()) {
            list.add(
                new SimilarAppsBundle(appcAppsViewModel, SimilarAppsBundle.BundleType.APPC_APPS));
          }
          return list;
        })
        .toObservable();
  }

  private Observable<List<SimilarAppsBundle>> updateSuggestedApps(AppViewViewModel appViewModel,
      List<SimilarAppsBundle> list) {
    return appViewManager.loadSimilarAppsViewModel(appViewModel.getPackageName(),
        appViewModel.getMedia()
            .getKeywords())
        .flatMap(similarAppsViewModel -> appViewManager.shouldLoadNativeAds()
            .doOnSuccess(similarAppsViewModel::setShouldLoadNativeAds)
            .map(__ -> similarAppsViewModel))
        .map(similarAppsViewModel -> {
          if (similarAppsViewModel.hasSimilarApps()) {
            list.add(
                new SimilarAppsBundle(similarAppsViewModel, SimilarAppsBundle.BundleType.APPS));
          }
          return list;
        })
        .toObservable();
  }

  private Observable<ReviewsViewModel> updateReviews(AppViewViewModel appViewModel) {
    return appViewManager.loadReviewsViewModel(appViewModel.getStore()
        .getName(), appViewModel.getPackageName(), view.getLanguageFilter())
        .observeOn(viewScheduler)
        .doOnError(__ -> view.hideReviews())
        .doOnSuccess(reviewsViewModel -> {
          if (reviewsViewModel.hasError()) {
            view.hideReviews();
          } else {
            view.populateReviews(reviewsViewModel, appViewModel);
          }
        })
        .toObservable();
  }

  private void cancelDownload() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(create -> view.cancelDownload()
            .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
            .doOnNext(app -> appViewAnalytics.sendDownloadCancelEvent(app.getPackageName()))
            .flatMapCompletable(
                app -> appViewManager.cancelDownload(app.getMd5(), app.getPackageName(),
                    app.getVersionCode()))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
        });
  }

  private void resumeDownload() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(create -> view.resumeDownload()
            .flatMap(__ -> permissionManager.requestDownloadAccess(permissionService)
                .flatMap(success -> permissionManager.requestExternalStoragePermission(
                    permissionService))
                .flatMapSingle(__1 -> appViewManager.loadAppViewViewModel())
                .flatMapCompletable(
                    app -> appViewManager.resumeDownload(app.getMd5(), app.getAppId()))
                .retry()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
        });
  }

  private void pauseDownload() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(create -> view.pauseDownload()
            .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
            .doOnNext(app -> appViewAnalytics.sendDownloadPauseEvent(app.getPackageName()))
            .flatMapCompletable(app -> appViewManager.pauseDownload(app.getMd5()))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
        });
  }

  private void handleInstallButtonClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(create -> accountManager.accountStatus())
        .first()
        .observeOn(viewScheduler)
        .flatMap(account -> view.installAppClick()
            .flatMapCompletable(action -> {
              Completable completable = null;
              switch (action) {
                case INSTALL:
                case UPDATE:
                  completable = appViewManager.loadAppViewViewModel()
                      .flatMapCompletable(
                          appViewModel -> downloadApp(action, appViewModel).observeOn(viewScheduler)
                              .doOnCompleted(() -> {
                                String conversionUrl = appViewModel.getCampaignUrl();
                                if (!conversionUrl.isEmpty()) {
                                  campaignAnalytics.sendCampaignConversionEvent(conversionUrl,
                                      appViewModel.getPackageName(), appViewModel.getVersionCode());
                                }
                                appViewAnalytics.clickOnInstallButton(appViewModel.getPackageName(),
                                    appViewModel.getDeveloper()
                                        .getName(), action.toString());
                              })
                              .doOnCompleted(() -> showRecommendsDialog(account.isLoggedIn(),
                                  appViewModel.getPackageName()))
                              .toSingleDefault(true)
                              .toCompletable());
                  break;
                case OPEN:
                  completable = appViewManager.loadAppViewViewModel()
                      .observeOn(viewScheduler)
                      .flatMapCompletable(
                          appViewViewModel -> openInstalledApp(appViewViewModel.getPackageName()));
                  break;
                case DOWNGRADE:
                  completable = appViewManager.loadAppViewViewModel()
                      .observeOn(viewScheduler)
                      .flatMapCompletable(
                          appViewViewModel -> downgradeApp(action, appViewViewModel));
                  break;
                case PAY:
                  completable = appViewManager.loadAppViewViewModel()
                      .observeOn(viewScheduler)
                      .flatMapCompletable(appViewViewModel -> payApp(appViewViewModel.getAppId()));
                  break;
                default:
                  completable =
                      Completable.error(new IllegalArgumentException("Invalid type of action"));
              }
              return completable;
            })
            .doOnError(throwable -> throwable.printStackTrace())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new IllegalStateException(error);
        });
  }

  private void showRecommendsDialog(boolean isLoggedIn, String packageName) {
    if (isLoggedIn && appViewManager.shouldShowRecommendsPreviewDialog()) {
      view.showRecommendsDialog();
      appViewAnalytics.sendLoggedInRecommendAppDialogShowEvent(packageName);
    } else if (!isLoggedIn && appViewManager.canShowNotLoggedInDialog()) {
      appViewNavigator.navigateToNotLoggedInShareFragmentForResult(packageName);
      appViewAnalytics.sendNotLoggedInRecommendAppDialogShowEvent(packageName);
    }
  }

  private Completable payApp(long appId) {
    return Completable.fromAction(() -> {
      appViewAnalytics.sendPaymentViewShowEvent();
      appViewNavigator.buyApp(appId);
    });
  }

  private Completable downgradeApp(DownloadModel.Action action, AppViewViewModel appViewModel) {
    return view.showDowngradeMessage()
        .filter(downgrade -> downgrade)
        .doOnNext(__ -> view.showDowngradingMessage())
        .flatMapCompletable(__ -> downloadApp(action, appViewModel))
        .toCompletable();
  }

  private Completable openInstalledApp(String packageName) {
    return Completable.fromAction(() -> view.openApp(packageName));
  }

  private Completable downloadApp(DownloadModel.Action action, AppViewViewModel appViewModel) {
    return Observable.defer(() -> {
      if (appViewManager.shouldShowRootInstallWarningPopup()) {
        return view.showRootInstallWarningPopup()
            .doOnNext(answer -> appViewManager.allowRootInstall(answer))
            .map(__ -> action);
      }
      return Observable.just(action);
    })
        .observeOn(viewScheduler)
        .flatMap(__ -> permissionManager.requestDownloadAccess(permissionService)
            .flatMap(
                success -> permissionManager.requestExternalStoragePermission(permissionService))
            .observeOn(Schedulers.io())
            .flatMapCompletable(__1 -> appViewManager.downloadApp(action, appViewModel.getAppId(),
                appViewModel.getMalware()
                    .getRank()
                    .name(), appViewModel.getEditorsChoice())))
        .toCompletable();
  }

  private void loadDownloadApp() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(created -> view.isAppViewReadyToDownload())
        .flatMap(create -> appViewManager.loadAppViewViewModel()
            .toObservable())
        .filter(app -> !app.isLoading())
        .flatMap(app -> appViewManager.loadDownloadAppViewModel(app.getMd5(), app.getPackageName(),
            app.getVersionCode(), app.isPaid(), app.getPay())
            .observeOn(viewScheduler)
            .doOnNext(model -> view.showDownloadAppModel(model, app.hasDonations())))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new OnErrorNotImplementedException(error);
        });
  }

  private void shareLoggedInRecommendsDialogClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(created -> view.shareLoggedInRecommendsDialogClick()
            .observeOn(Schedulers.io())
            .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
            .observeOn(viewScheduler)
            .flatMapCompletable(app -> appViewManager.shareOnTimeline(app.getPackageName(),
                app.getStore()
                    .getId(), "install")
                .doOnCompleted(() -> appViewAnalytics.sendTimelineLoggedInInstallRecommendEvents(
                    app.getPackageName()))
                .doOnCompleted(() -> view.showRecommendsThanksMessage()))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new OnErrorNotImplementedException(error);
        });
  }

  private void skipLoggedInRecommendsDialogClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(created -> view.skipLoggedInRecommendsDialogClick()
            .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
            .doOnNext(app -> appViewAnalytics.sendTimelineLoggedInInstallRecommendSkipEvents(
                app.getPackageName()))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new OnErrorNotImplementedException(error);
        });
  }

  private void dontShowAgainLoggedInRecommendsDialogClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(created -> view.dontShowAgainLoggedInRecommendsDialogClick()
            .flatMapCompletable(
                __ -> appViewManager.dontShowLoggedInInstallRecommendsPreviewDialog())
            .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
            .doOnNext(
                app -> appViewAnalytics.sendTimelineLoggedInInstallRecommendDontShowMeAgainEvents(
                    app.getPackageName()))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new OnErrorNotImplementedException(error);
        });
  }

  private void handleNotLoggedinShareResults() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(created -> appViewNavigator.notLoggedInViewResults()
            .filter(success -> success)
            .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
            .flatMapCompletable(app -> appViewManager.shareOnTimelineAsync(app.getPackageName(),
                app.getStore()
                    .getId())
                .doOnCompleted(() -> appViewAnalytics.sendSuccessShareEvent()))
            .doOnError(error -> {
              appViewAnalytics.sendFailedShareEvent();
              crashReport.log(error);
            })
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new OnErrorNotImplementedException(error);
        });
  }

  private void handleAppBought() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.appBought()
            .flatMap(appBoughClickEvent -> appViewManager.loadAppViewViewModel()
                .toObservable()
                .filter(appViewViewModel -> appViewViewModel.getAppId()
                    == appBoughClickEvent.getAppId())
                .map(__2 -> appBoughClickEvent))
            .first()
            .observeOn(viewScheduler)
            .flatMap(appBoughClickEvent -> appViewManager.loadAppViewViewModel()
                .flatMapCompletable(
                    appViewViewModel -> appViewManager.appBought(appBoughClickEvent.getPath())
                        .andThen(downloadApp(DownloadModel.Action.INSTALL, appViewViewModel)))
                .toObservable())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new OnErrorNotImplementedException(error);
        });
  }

  private void handleApkfyDialogPositiveClick() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.apkfyDialogPositiveClick())
        .doOnNext(appname -> view.showApkfyElement(appname))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new OnErrorNotImplementedException(error);
        });
  }

  private void handleClickOnTopDonorsDonate() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickTopDonorsDonateButton())
        .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
        .doOnNext(app -> {
          appViewAnalytics.sendDonateClickTopDonors();
          appViewNavigator.navigateToDonationsDialog(app.getPackageName(), TAG);
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new OnErrorNotImplementedException(error);
        });
  }

  private void handleDonateCardImpressions() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.installAppClick())
        .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
        .doOnNext(model -> {
          if (model.hasDonations()) {
            appViewAnalytics.sendDonateImpressionAfterInstall(model.getPackageName());
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new OnErrorNotImplementedException(error);
        });
  }

  private void handleDismissWalletPromotion() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.dismissWalletPromotionClick())
        .doOnNext(__ -> view.dismissWalletPromotionView())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new OnErrorNotImplementedException(error);
        });
  }
}

