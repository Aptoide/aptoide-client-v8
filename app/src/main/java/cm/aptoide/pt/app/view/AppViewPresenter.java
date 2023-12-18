package cm.aptoide.pt.app.view;

import android.text.TextUtils;
import android.text.format.DateUtils;
import androidx.annotation.VisibleForTesting;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.ads.WalletAdsOfferManager;
import cm.aptoide.pt.ads.data.ApplicationAd;
import cm.aptoide.pt.ads.data.AptoideNativeAd;
import cm.aptoide.pt.app.AppModel;
import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.app.AppViewManager;
import cm.aptoide.pt.app.AppViewModel;
import cm.aptoide.pt.app.AppViewSimilarApp;
import cm.aptoide.pt.app.CampaignAnalytics;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.app.PromotionViewModel;
import cm.aptoide.pt.app.ReviewsViewModel;
import cm.aptoide.pt.app.SimilarAppsViewModel;
import cm.aptoide.pt.app.view.similar.SimilarAppsBundle;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.download.InvalidAppException;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.promotions.ClaimDialogResultWrapper;
import cm.aptoide.pt.promotions.Promotion;
import cm.aptoide.pt.promotions.PromotionsNavigator;
import cm.aptoide.pt.promotions.WalletApp;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.wallet.WalletAppProvider;
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
  private final PromotionsNavigator promotionsNavigator;
  private final AppViewView view;
  private final AccountNavigator accountNavigator;
  private final AppViewAnalytics appViewAnalytics;
  private final CampaignAnalytics campaignAnalytics;
  private final AppViewNavigator appViewNavigator;
  private final AppViewManager appViewManager;
  private final AptoideAccountManager accountManager;
  private final Scheduler viewScheduler;
  private final CrashReport crashReport;
  private boolean openTypeAlreadyRegistered = false;
  private final WalletAppProvider walletAppProvider;

  public AppViewPresenter(AppViewView view, AccountNavigator accountNavigator,
      AppViewAnalytics appViewAnalytics, CampaignAnalytics campaignAnalytics,
      AppViewNavigator appViewNavigator, AppViewManager appViewManager,
      AptoideAccountManager accountManager, Scheduler viewScheduler, CrashReport crashReport,
      PermissionManager permissionManager, PermissionService permissionService,
      PromotionsNavigator promotionsNavigator, WalletAppProvider walletAppProvider) {
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
    this.promotionsNavigator = promotionsNavigator;
    this.walletAppProvider = walletAppProvider;
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
    handleClickOnBonusAppcFlair();
    handleClickOnAppcIabInfo();
    handleClickOnSimilarApps();
    handleClickOnToolbar();
    handleClickOnRetry();
    handleClickOnCatappultCard();
    handleOnSimilarAppsVisible();

    handleInstallButtonClick();
    pauseDownload();
    resumeDownload();
    cancelDownload();
    handleApkfyDialogPositiveClick();
    handleESkillsCardClick();
    handleDismissWalletPromotion();
    // handleEskillsWalletProgress();

    claimApp();
    handlePromotionClaimResult();
    resumeWalletDownload();
    cancelPromotionDownload();
    pauseWalletDownload();

    handleDownloadingSimilarApp();
    handleOutOfSpaceDialogResult();
  }

  private void handleESkillsCardClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.eSkillsCardClick())
        .doOnNext(result -> appViewNavigator.navigateToESkillsSectionOnAppCoinsInfoView())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  /*private void handleEskillsWalletProgress() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> appViewManager.getAppModel().toObservable())
        .filter(AppModel::isEskills)
        .flatMap(__ -> appViewManager.observeWalletInstallStatus())
        .doOnNext(view::showEskillsWalletView)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }*/

  private void handleOutOfSpaceDialogResult() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> appViewNavigator.outOfSpaceDialogResult())
        .filter(result -> result.getClearedSuccessfully())
        .flatMap(outOfSpaceResult -> {
          if (outOfSpaceResult.getPackageName()
              .equals("com.appcoins.wallet")) {
            return appViewManager.loadPromotionViewModel()
                .flatMapCompletable(promotionViewModel -> appViewManager.resumeDownload(
                    promotionViewModel.getWalletApp()
                        .getMd5sum(), promotionViewModel.getWalletApp()
                        .getId(), promotionViewModel.getWalletApp()
                        .getDownloadModel()
                        .getAction(), promotionViewModel.getWalletApp()
                        .getTrustedBadge(), false));
          } else {
            return appViewManager.getAppViewModel()
                .toObservable()
                .flatMapCompletable(appViewModel -> appViewManager.resumeDownload(
                    appViewModel.getAppModel()
                        .getMd5(), appViewModel.getAppModel()
                        .getAppId(), appViewModel.getDownloadModel()
                        .getAction(), appViewModel.getAppModel()
                        .getMalware()
                        .getRank()
                        .toString(), appViewModel.getAppModel()
                        .getOpenType() == AppViewFragment.OpenType.APK_FY_INSTALL_POPUP));
          }
        })
        .retry()
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private Observable<AppViewModel> loadAppView() {
    return appViewManager.getAppViewModel()
        .observeOn(viewScheduler)
        .doOnSuccess(this::showAppView)
        .doOnSuccess(this::sendAppViewLoadAnalytics)
        .toObservable()
        .filter(model -> !model.getAppModel()
            .hasError())
        .flatMap(appViewModel -> Observable.mergeDelayError(loadAds(appViewModel),
            handleAppViewOpenOptions(appViewModel), loadAppcPromotion(appViewModel),
            loadEskills(appViewModel), observePromotionDownloadErrors(appViewModel),
            observeDownloadApp(), observeDownloadErrors(),
            loadOtherAppViewComponents(appViewModel)));
  }

  private void showAppView(AppViewModel appViewModel) {
    if (appViewModel.getAppModel()
        .hasError()) {
      view.handleError(appViewModel.getAppModel()
          .getError());
    } else {
      view.setInstallButton(appViewModel.getAppCoinsViewModel());
      view.showAppView(appViewModel.getAppModel());
      view.showDownloadAppModel(appViewModel.getDownloadModel(),
          appViewModel.getAppCoinsViewModel(), appViewModel.getAppModel()
              .hasSplits());
      if (appViewModel.getAppCoinsViewModel()
          .hasAdvertising() || appViewModel.getAppCoinsViewModel()
          .hasBilling()) {
        view.setupAppcAppView(appViewModel.getAppCoinsViewModel()
            .hasBilling(), appViewModel.getAppCoinsViewModel()
            .getBonusAppcModel());
      }
      if (appViewModel.getAppModel().isEskills()) {
        view.setupEskillsAppView();
      }
      view.recoverScrollViewState();
    }
  }

  private void sendAppViewLoadAnalytics(AppViewModel appViewModel) {
    AppModel appModel = appViewModel.getAppModel();
    if (appModel.isFromEditorsChoice()) {
      appViewManager.sendEditorsAppOpenAnalytics(appModel.getPackageName(), appModel.getDeveloper()
          .getName(), appModel.getMalware()
          .getRank()
          .name(), appModel.hasBilling(), appModel.hasAdvertising(), appModel.getEditorsChoice());
    } else {
      appViewManager.sendAppOpenAnalytics(appModel.getPackageName(), appModel.getDeveloper()
          .getName(), appModel.getMalware()
          .getRank()
          .name(), appModel.hasBilling(), appModel.hasAdvertising());
    }
    if (appViewModel.getDownloadModel()
        .getAction()
        .equals(DownloadModel.Action.MIGRATE) && !appViewManager.isMigrationImpressionSent()) {
      appViewManager.setMigrationImpressionSent();
      appViewAnalytics.sendAppcMigrationAppOpen();
    }
  }

  public Observable<AppViewModel> loadAds(AppViewModel appViewModel) {
    return appViewManager.registerAppsFlyerImpression(appViewModel.getAppModel()
            .getPackageName())
        .doOnError(Throwable::printStackTrace)
        .toObservable()
        .subscribeOn(Schedulers.io())
        .flatMap(__ -> loadOrganicAds(appViewModel))
        .map(__ -> appViewModel)
        .onErrorReturn(throwable -> {
          crashReport.log(throwable);
          return appViewModel;
        });
  }

  private Observable<SearchAdResult> loadOrganicAds(AppViewModel appViewModel) {
    return Single.just(appViewModel.getAppModel()
            .getMinimalAd())
        .observeOn(Schedulers.io())
        .flatMap(adResult -> {
          if (adResult == null) {
            return appViewManager.loadAdsFromAppView()
                .doOnSuccess(ad -> {
                  appViewManager.setSearchAdResult(ad);
                  handleAdsLogic(appViewManager.getSearchAdResult());
                })
                .doOnError(throwable -> {
                  crashReport.log(throwable);
                });
          }
          return Single.just(adResult)
              .doOnSuccess(__ -> handleAdsLogic(adResult));
        })
        .onErrorReturn(__ -> null)
        .toObservable()
        .observeOn(viewScheduler);
  }

  @VisibleForTesting
  public Observable<AppViewModel> handleAppViewOpenOptions(AppViewModel appViewModel) {
    AppModel appModel = appViewModel.getAppModel();
    DownloadModel.Action action = appViewModel.getDownloadModel()
        .getAction();
    return handleOpenAppViewDialogInput(appViewModel.getAppModel()).filter(
            shouldDownload -> shouldDownload)
        .flatMapCompletable(__ -> appViewManager.getAdsVisibilityStatus()
            .doOnSuccess(status -> appViewAnalytics.clickOnInstallButton(appModel.getPackageName(),
                appModel.getDeveloper()
                    .getName(), action.toString(), appModel.hasSplits(), appModel.hasBilling(),
                action.equals(DownloadModel.Action.MIGRATE), appModel.getMalware()
                    .getRank()
                    .name(), status.toString()
                    .toLowerCase(), appModel.getOriginTag(), appModel.getStore()
                    .getName(),
                appModel.getOpenType() == AppViewFragment.OpenType.APK_FY_INSTALL_POPUP,
                appModel.getObb() != null))
            .flatMapCompletable(status -> downloadApp(action, appModel, status,
                appModel.getOpenType() == AppViewFragment.OpenType.APK_FY_INSTALL_POPUP).doOnError(
                    throwable -> {
                      if (throwable instanceof InvalidAppException) {
                        view.showInvalidAppInfoErrorDialog();
                      } else {
                        view.showGenericErrorDialog();
                      }
                    })
                .onErrorComplete()))
        .switchIfEmpty(Observable.just(false))
        .map(__ -> appViewModel)
        .onErrorReturn(throwable -> {
          crashReport.log(throwable);
          return appViewModel;
        });
  }

  private Observable<Boolean> handleOpenAppViewDialogInput(AppModel appModel) {
    if (!openTypeAlreadyRegistered) {
      openTypeAlreadyRegistered = true;
      if (appModel.getOpenType() == AppViewFragment.OpenType.OPEN_AND_INSTALL) {
        return Observable.just(true);
      } else if (appModel.getOpenType() == AppViewFragment.OpenType.OPEN_WITH_INSTALL_POPUP) {
        return view.showOpenAndInstallDialog(appModel.getMarketName(), appModel.getAppName())
            .map(__ -> true);
      } else if (appModel.getOpenType() == AppViewFragment.OpenType.APK_FY_INSTALL_POPUP) {
        return view.showOpenAndInstallApkFyDialog(appModel.getMarketName(), appModel.getAppName(),
                appModel.getAppc(), appModel.getRating()
                    .getAverage(), appModel.getIcon(), appModel.getPackageDownloads())
            .map(__ -> true);
      }
    }
    return Observable.just(false);
  }

  @VisibleForTesting
  public Observable<AppViewModel> loadOtherAppViewComponents(AppViewModel appViewModel) {
    return Observable.zip(updateSimilarAppsBundles(appViewModel.getAppModel()),
            updateReviews(appViewModel.getAppModel()),
            (similarAppsBundles, reviewsViewModel) -> Observable.just(appViewModel))
        .first()
        .map(__ -> appViewModel);
  }

  @VisibleForTesting public Observable<AppViewModel> loadEskills(AppViewModel appViewModel) {
    return Observable.just(appViewModel.getAppModel())
        .filter(AppModel::isEskills)
        .flatMap(__ -> appViewManager.observeWalletInstallStatus())
        .observeOn(viewScheduler)
        .doOnNext(walletApp -> view.showEskillsWalletView(appViewModel.getAppModel().getAppName(), walletApp))
        .map(__ -> appViewModel)
        .onErrorReturn(throwable -> {
          throwable.printStackTrace();
          crashReport.log(throwable);
          return appViewModel;
        });
  }

  @VisibleForTesting public Observable<AppViewModel> loadAppcPromotion(AppViewModel appViewModel) {
    return Observable.just(appViewModel.getAppModel())
        .filter(appModel -> appModel.hasBilling() || appModel.hasAdvertising())
        .flatMap(__ -> appViewManager.loadPromotionViewModel())
        .observeOn(viewScheduler)
        .doOnNext(promotionViewModel -> {
          if (promotionViewModel.getAppViewModel() == null) return;
          DownloadModel appDownloadModel = promotionViewModel.getAppViewModel()
              .getDownloadModel();
          AppModel app = promotionViewModel.getAppViewModel()
              .getAppModel();

          Promotion.ClaimAction action = Promotion.ClaimAction.INSTALL;
          if (appDownloadModel != null
              && appDownloadModel.getAction() == DownloadModel.Action.MIGRATE) {
            action = Promotion.ClaimAction.MIGRATE;
          } else if (promotionViewModel.getAppViewModel()
              .getMigrationModel()
              .isMigrated()
              && appDownloadModel != null
              && appDownloadModel.getAction() == DownloadModel.Action.OPEN) {
            action = Promotion.ClaimAction.MIGRATE;
          }
          Promotion promotion =
              appViewManager.getClaimablePromotion(promotionViewModel.getPromotions(), action);

          if (promotion != null && app != null && appDownloadModel != null) {
            view.showAppcWalletPromotionView(promotion, promotionViewModel.getWalletApp(), action,
                appDownloadModel);
            if (!appViewManager.isAppcPromotionImpressionSent()) {
              appViewAnalytics.sendPromotionImpression(promotion.getPromotionId());
              appViewManager.setAppcPromotionImpressionSent();
            }

            if (promotionViewModel.getWalletApp()
                .isInstalled() && appDownloadModel.getAction() == DownloadModel.Action.OPEN) {
              appViewManager.scheduleNotification(String.valueOf(promotion.getAppc()),
                  app.getIcon(), app.getPackageName(), app.getStore()
                      .getName());
            }
          }
        })
        .map(__ -> appViewModel)
        .onErrorReturn(throwable -> {
          throwable.printStackTrace();
          crashReport.log(throwable);
          return appViewModel;
        });
  }

  public Observable<AppViewModel> observePromotionDownloadErrors(AppViewModel appViewModel) {
    return Observable.merge(view.resumePromotionDownload(), view.installWalletButtonClick())
        .flatMap(__ -> appViewManager.loadPromotionViewModel()
            .filter(promotionViewModel -> promotionViewModel.getWalletApp()
                .getDownloadModel() != null && promotionViewModel.getWalletApp()
                .getDownloadModel()
                .hasError())
            .first())
        .doOnNext(promotionViewModel -> {
          if (promotionViewModel.getWalletApp()
              .getDownloadModel()
              .getDownloadState()
              .equals(DownloadModel.DownloadState.NOT_ENOUGH_STORAGE_ERROR)) {
            appViewNavigator.navigateToOutOfSpaceDialog(promotionViewModel.getWalletApp()
                .getSize(), promotionViewModel.getWalletApp()
                .getPackageName());
          } else {
            view.showDownloadError(promotionViewModel.getWalletApp()
                .getDownloadModel());
          }
        })
        .flatMap(this::verifyNotEnoughSpaceError)
        .map(__ -> appViewModel)
        .retry();
  }

  public Observable<AppViewModel> observeDownloadApp() {
    return appViewManager.observeAppViewModel()
        .observeOn(viewScheduler)
        .doOnNext(model -> view.showDownloadAppModel(model.getDownloadModel(),
            model.getAppCoinsViewModel(), model.getAppModel()
                .hasSplits()));
  }

  private Observable<AppViewModel> observeDownloadErrors() {
    return Observable.merge(view.installAppClick(), view.resumeDownload())
        .flatMap(__ -> appViewManager.observeAppViewModel()
            .filter(appViewModel -> appViewModel.getDownloadModel()
                .hasError())
            .first())
        .doOnNext(appViewModel -> {
          if (appViewModel.getDownloadModel()
              .getDownloadState()
              .equals(DownloadModel.DownloadState.NOT_ENOUGH_STORAGE_ERROR)) {
            appViewNavigator.navigateToOutOfSpaceDialog(appViewModel.getAppModel()
                .getSize(), appViewModel.getAppModel()
                .getPackageName());
          } else {
            view.showDownloadError(appViewModel.getDownloadModel());
          }
        })
        .flatMap(this::verifyNotEnoughSpaceError)
        .retry();
  }

  private Observable<AppViewModel> verifyNotEnoughSpaceError(AppViewModel appViewModel) {
    AppModel appModel = appViewModel.getAppModel();
    DownloadModel downloadModel = appViewModel.getDownloadModel();
    if (appViewModel.getDownloadModel()
        .getDownloadState() == DownloadModel.DownloadState.NOT_ENOUGH_STORAGE_ERROR) {
      return appViewManager.getAdsVisibilityStatus()
          .doOnSuccess(offerResponseStatus -> appViewAnalytics.sendNotEnoughSpaceErrorEvent(
              appModel.getMd5()))
          .toObservable()
          .map(__ -> appViewModel);
    }
    return Observable.just(appViewModel);
  }

  private Observable<PromotionViewModel> verifyNotEnoughSpaceError(
      PromotionViewModel promotionViewModel) {
    WalletApp walletApp = promotionViewModel.getWalletApp();
    DownloadModel downloadModel = walletApp.getDownloadModel();
    if (downloadModel != null
        && downloadModel.getDownloadState()
        == DownloadModel.DownloadState.NOT_ENOUGH_STORAGE_ERROR) {
      return appViewManager.getAdsVisibilityStatus()
          .doOnSuccess(offerResponseStatus -> appViewAnalytics.sendNotEnoughSpaceErrorEvent(
              walletApp.getMd5sum()))
          .toObservable()
          .map(__ -> promotionViewModel);
    }
    return Observable.just(promotionViewModel);
  }

  private void handleDownloadingSimilarApp() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(__ -> Observable.merge(view.installAppClick(), view.apkfyDialogPositiveClick()))
        .flatMap(__ -> downloadInRange(0, 100))
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.showDownloadingSimilarApps(
            appViewManager.getCachedAppcSimilarAppsViewModel()
                .hasSimilarApps() || appViewManager.getCachedSimilarAppsViewModel()
                .hasSimilarApps()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private Observable<DownloadModel> downloadInRange(int min, int max) {
    return appViewManager.downloadStarted()
        .filter(downloadModel -> downloadModel.isDownloading())
        .filter(downloadModel -> downloadModel.getProgress() >= min
            && downloadModel.getProgress() < max)
        .first();
  }

  private void sendSimilarAppcAppsImpressionEvent(SimilarAppsViewModel appcSimilarAppsViewModel) {
    if (appcSimilarAppsViewModel != null) {
      appViewAnalytics.similarAppcAppBundleImpression();
    }
  }

  private void sendSimilarAppsAdImpressionEvent(SimilarAppsViewModel similarAppsViewModel) {
    if (similarAppsViewModel != null
        && similarAppsViewModel.hasAd()
        && !similarAppsViewModel.hasRecordedAdImpression()) {
      similarAppsViewModel.setHasRecordedAdImpression(true);
      appViewAnalytics.similarAppBundleImpression(similarAppsViewModel.getAd()
          .getNetwork(), true);
    }
  }

  @VisibleForTesting public void handleFirstLoad() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> view.showLoading())
        .flatMap(__ -> loadAppView())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleOnSimilarAppsVisible() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(lifecycleEvent -> Observable.merge(view.scrollVisibleSimilarApps()
            .map(__ -> true), view.similarAppsVisibilityFromInstallClick()))
        .first()
        .observeOn(Schedulers.io())
        .doOnNext(__ -> {
          sendSimilarAppInteractEvent(appViewManager.getCachedSimilarAppsViewModel());
          sendSimilarAppcAppsImpressionEvent(appViewManager.getCachedAppcSimilarAppsViewModel());
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void sendSimilarAppInteractEvent(SimilarAppsViewModel similarAppsViewModel) {
    sendSimilarAppsAdImpressionEvent(similarAppsViewModel);
    appViewAnalytics.similarAppBundleImpression(null, false);
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
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
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
              readMoreClickEvent.getDescription(), readMoreClickEvent.hasAppc());
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnDeveloperWebsite() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickDeveloperWebsite())
        .flatMapSingle(__ -> appViewManager.getAppModel())
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
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void handleClickOnBonusAppcFlair() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickBonusAppcFlair())
        .doOnNext(click -> {
          appViewAnalytics.sendAppcInfoInteractEvent();
          appViewNavigator.navigateToAppCoinsInfo();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void handleClickOnAppcIabInfo() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.iabInfoClick())
        .doOnNext(click -> appViewNavigator.navigateToAppCoinsInfo())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void handleClickOnDeveloperEmail() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickDeveloperEmail())
        .flatMapSingle(__ -> appViewManager.getAppModel())
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
        .flatMapSingle(__ -> appViewManager.getAppModel())
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
        .flatMapSingle(__ -> appViewManager.getAppModel())
        .doOnNext(app -> view.navigateToDeveloperPermissions(app))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnStoreLayout() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickStoreLayout())
        .flatMapSingle(__ -> appViewManager.getAppModel())
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
        .flatMapSingle(__ -> appViewManager.getAppModel())
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
        .flatMapSingle(__ -> appViewManager.getAppModel())
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
        .flatMapSingle(__ -> appViewManager.getAppModel())
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
        .flatMapSingle(__ -> appViewManager.getAppModel())
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
        .flatMapSingle(__ -> appViewManager.getAppModel())
        .doOnNext(model -> {
          appViewAnalytics.sendReadAllEvent();
          appViewNavigator.navigateToRateAndReview(model.getAppId(), model.getAppName(),
              model.getStore()
                  .getName(), model.getPackageName(), "default");
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
            .flatMapSingle(__ -> appViewManager.getAppModel())
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
                  similarAppClickEvent.getType()
                      .getDescription());
            }
          } else {
            packageName = appViewSimilarApp.getApp()
                .getPackageName();
            appViewNavigator.navigateToAppView(appViewSimilarApp.getApp()
                .getAppId(), packageName, similarAppClickEvent.getType()
                .getDescription());
          }
          appViewAnalytics.sendSimilarAppsInteractEvent(similarAppClickEvent.getType()
              .getDescription());
          appViewAnalytics.similarAppClick(similarAppClickEvent.getType(), network, packageName,
              similarAppClickEvent.getPosition(), isAd);
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
            .flatMap(menuItem -> appViewManager.getAppModel()
                .toObservable()
                .filter(appViewViewModel -> menuItem != null)
                .observeOn(viewScheduler)
                .doOnNext(appViewViewModel -> {
                  switch (menuItem.getItemId()) {

                    case R.id.menu_item_share:
                      view.defaultShare(appViewViewModel.getAppName(),
                          appViewViewModel.getWebUrls());
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

  private void handleClickOnRetry() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickErrorRetry()
            .doOnNext(__1 -> view.showLoading())
            .flatMap(__2 -> loadAppView())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void handleClickOnCatappultCard() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickCatappultCard())
        .observeOn(viewScheduler)
        .doOnNext(__ -> appViewNavigator.navigateToCatappultWebsite())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReport::log);
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

  private Observable<List<SimilarAppsBundle>> updateSimilarAppsBundles(AppModel appModel) {
    return Observable.just(new ArrayList<SimilarAppsBundle>())
        .flatMap(list -> updateSuggestedAppcApps(appModel, list))
        .flatMap(list -> updateSuggestedApps(appModel, list))
        .flatMap(list -> sortSuggestedApps(appModel, list))
        .observeOn(viewScheduler)
        .doOnNext(list -> view.populateSimilar(list));
  }

  private Observable<List<SimilarAppsBundle>> sortSuggestedApps(AppModel appModel,
      List<SimilarAppsBundle> list) {
    return Observable.just(list)
        .map(__ -> {
          if (list.size() >= 2) {
            if (appModel.isAppCoinApp()) {
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

  private Observable<List<SimilarAppsBundle>> updateSuggestedAppcApps(AppModel appViewModel,
      List<SimilarAppsBundle> list) {
    return appViewManager.loadAppcSimilarAppsViewModel(appViewModel.getPackageName())
        .map(appcAppsViewModel -> {
          if (appcAppsViewModel.hasSimilarApps()) {
            list.add(
                new SimilarAppsBundle(appcAppsViewModel, SimilarAppsBundle.BundleType.APPC_APPS));
          }
          return list;
        })
        .toObservable();
  }

  private Observable<List<SimilarAppsBundle>> updateSuggestedApps(AppModel appViewModel,
      List<SimilarAppsBundle> list) {
    return appViewManager.loadSimilarAppsViewModel(appViewModel.getPackageName(),
            appViewModel.getMedia()
                .getKeywords())
        .map(similarAppsViewModel -> {
          if (similarAppsViewModel.hasSimilarApps()) {
            list.add(
                new SimilarAppsBundle(similarAppsViewModel, SimilarAppsBundle.BundleType.APPS));
          }
          return list;
        })
        .toObservable();
  }

  private Observable<ReviewsViewModel> updateReviews(AppModel appViewModel) {
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
            .flatMapSingle(__ -> appViewManager.getAppModel())
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
            .flatMap(
                success -> permissionManager.requestExternalStoragePermission(permissionService))
            .flatMapSingle(__1 -> appViewManager.getAppViewModel())
            .flatMapCompletable(app -> appViewManager.resumeDownload(app.getAppModel()
                    .getMd5(), app.getAppModel()
                    .getAppId(), app.getDownloadModel()
                    .getAction(), app.getAppModel()
                    .getMalware()
                    .getRank()
                    .toString(), app.getAppModel()
                    .getOpenType() == AppViewFragment.OpenType.APK_FY_INSTALL_POPUP)
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
            .flatMapSingle(__ -> appViewManager.getAppModel())
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
                  completable = appViewManager.getAppModel()
                      .flatMapCompletable(appModel -> appViewManager.getAdsVisibilityStatus()
                          .flatMapCompletable(status -> downloadApp(action, appModel, status,
                              appModel.getOpenType()
                                  == AppViewFragment.OpenType.APK_FY_INSTALL_POPUP)
                              .andThen(downloadEskillsWallet())
                              .observeOn(viewScheduler)
                              .doOnCompleted(() -> {
                                String conversionUrl = appModel.getCampaignUrl();
                                if (!conversionUrl.isEmpty()) {
                                  campaignAnalytics.sendCampaignConversionEvent(conversionUrl,
                                      appModel.getPackageName(), appModel.getVersionCode());
                                }
                                appViewAnalytics.clickOnInstallButton(appModel.getPackageName(),
                                    appModel.getDeveloper()
                                        .getName(), action.toString(), appModel.hasSplits(),
                                    appModel.hasBilling(), false, appModel.getMalware()
                                        .getRank()
                                        .name(), status.toString()
                                        .toLowerCase(), appModel.getOriginTag(), appModel.getStore()
                                        .getName(), appModel.getOpenType()
                                        == AppViewFragment.OpenType.APK_FY_INSTALL_POPUP,
                                    appModel.getObb() != null);

                                if (appViewManager.hasClaimablePromotion(
                                    Promotion.ClaimAction.INSTALL)) {
                                  appViewAnalytics.sendInstallPromotionApp();
                                }
                              })));
                  break;
                case OPEN:
                  completable = appViewManager.getAppModel()
                      .observeOn(viewScheduler)
                      .flatMapCompletable(
                          appViewViewModel -> openInstalledApp(appViewViewModel.getPackageName()));
                  break;
                case DOWNGRADE:
                  completable = appViewManager.getAppModel()
                      .flatMapCompletable(
                          appViewViewModel -> appViewManager.getAdsVisibilityStatus()
                              .observeOn(viewScheduler)
                              .flatMapCompletable(
                                  status -> downgradeApp(action, appViewViewModel, status,
                                      appViewViewModel.getOpenType()
                                          == AppViewFragment.OpenType.APK_FY_INSTALL_POPUP).doOnCompleted(
                                      () -> appViewAnalytics.clickOnInstallButton(
                                          appViewViewModel.getPackageName(),
                                          appViewViewModel.getDeveloper()
                                              .getName(), action.toString(),
                                          appViewViewModel.hasSplits(),
                                          appViewViewModel.hasBilling(), false,
                                          appViewViewModel.getMalware()
                                              .getRank()
                                              .name(), status.toString()
                                              .toLowerCase(), appViewViewModel.getOriginTag(),
                                          appViewViewModel.getStore()
                                              .getName(), appViewViewModel.getOpenType()
                                              == AppViewFragment.OpenType.APK_FY_INSTALL_POPUP,
                                          appViewViewModel.getObb() != null))));
                  break;
                case MIGRATE:
                  completable = appViewManager.getAppModel()
                      .flatMapCompletable(
                          appViewViewModel -> appViewManager.getAdsVisibilityStatus()
                              .observeOn(viewScheduler)
                              .flatMapCompletable(status -> {
                                if (appViewManager.hasClaimablePromotion(
                                    Promotion.ClaimAction.MIGRATE)) {
                                  appViewAnalytics.sendAppcMigrationUpdateClick();
                                }
                                appViewAnalytics.clickOnInstallButton(
                                    appViewViewModel.getPackageName(),
                                    appViewViewModel.getDeveloper()
                                        .getName(), "UPDATE TO APPC", appViewViewModel.hasSplits(),
                                    appViewViewModel.hasBilling(), true,
                                    appViewViewModel.getMalware()
                                        .getRank()
                                        .name(), status.toString()
                                        .toLowerCase(), appViewViewModel.getOriginTag(),
                                    appViewViewModel.getStore()
                                        .getName(), appViewViewModel.getOpenType()
                                        == AppViewFragment.OpenType.APK_FY_INSTALL_POPUP,
                                    appViewViewModel.getObb() != null);
                                return migrateApp(action, appViewViewModel, status,
                                    appViewViewModel.getOpenType()
                                        == AppViewFragment.OpenType.APK_FY_INSTALL_POPUP);
                              }));
                  break;
                default:
                  completable =
                      Completable.error(new IllegalArgumentException("Invalid type of action"));
              }
              return completable;
            })
            .doOnError(throwable -> {
              crashReport.log(throwable);
              if (throwable instanceof InvalidAppException) {
                view.showInvalidAppInfoErrorDialog();
              } else {
                view.showGenericErrorDialog();
              }
            })
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new IllegalStateException(error);
        });
  }

  private Completable downloadEskillsWallet() {
    return walletAppProvider.getWalletApp().first().flatMapCompletable(walletApp -> {
        if(!walletApp.isInstalled()) {
          return downloadWallet(walletApp);
        }
        return Completable.complete();
    }).toCompletable(); // TODO
  }

  private Completable downgradeApp(DownloadModel.Action action, AppModel appModel,
      WalletAdsOfferManager.OfferResponseStatus status, boolean isApkfy) {
    return view.showDowngradeMessage()
        .filter(downgrade -> downgrade)
        .doOnNext(__ -> view.showDowngradingMessage())
        .flatMapCompletable(__ -> downloadApp(action, appModel, status, isApkfy))
        .toCompletable();
  }

  private Completable migrateApp(DownloadModel.Action action, AppModel appModel,
      WalletAdsOfferManager.OfferResponseStatus status, boolean isApkfy) {
    return downloadApp(action, appModel, status, false);
  }

  private Completable openInstalledApp(String packageName) {
    return Completable.fromAction(() -> view.openApp(packageName));
  }

  private Completable downloadApp(DownloadModel.Action action, AppModel appModel,
      WalletAdsOfferManager.OfferResponseStatus status, boolean isApkfy) {
    return Observable.defer(() -> {
          if (appViewManager.shouldShowRootInstallWarningPopup()) {
            return view.showRootInstallWarningPopup()
                .doOnNext(answer -> appViewManager.allowRootInstall(answer))
                .map(__ -> action);
          }
          return Observable.just(action);
        })
        .observeOn(viewScheduler)
        .flatMap(__ -> permissionManager.requestDownloadAccessWithWifiBypass(permissionService,
                appModel.getSize())
            .flatMap(
                success -> permissionManager.requestExternalStoragePermission(permissionService))
            .observeOn(Schedulers.io())
            .flatMapCompletable(__1 -> appViewManager.downloadApp(action, appModel.getAppId(),
                appModel.getMalware()
                    .getRank()
                    .name(), appModel.getEditorsChoice(), status, isApkfy)))
        .toCompletable();
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

  private void handleDismissWalletPromotion() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.dismissWalletPromotionClick())
        .doOnNext(promotion -> {
          appViewAnalytics.sendClickOnNoThanksWallet(promotion.getPromotionId());
          view.dismissWalletPromotionView();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new OnErrorNotImplementedException(error);
        });
  }

  private void handleInstallWalletPromotion() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.installWalletButtonClick()
            .doOnNext(pair -> appViewAnalytics.sendInstallAppcWallet(pair.first.getPromotionId()))
            .flatMapCompletable(pair -> downloadWallet(pair.second))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new OnErrorNotImplementedException(error);
        });
  }

  private void resumeWalletDownload() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(create -> view.resumePromotionDownload()
            .flatMap(
                walletApp -> permissionManager.requestExternalStoragePermission(permissionService)
                    .flatMapCompletable(__ -> appViewManager.resumeDownload(walletApp.getMd5sum(),
                        walletApp.getId(), walletApp.getDownloadModel()
                            .getAction(), walletApp.getTrustedBadge(), false))
                    .retry()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
        });
  }

  private void cancelPromotionDownload() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(create -> view.cancelPromotionDownload()
            .flatMapCompletable(walletApp -> appViewManager.cancelDownload(walletApp.getMd5sum(),
                walletApp.getPackageName(), walletApp.getVersionCode()))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new OnErrorNotImplementedException(error);
        });
  }

  private void pauseWalletDownload() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.pausePromotionDownload()
            .flatMapCompletable(walletApp -> appViewManager.pauseDownload(walletApp.getMd5sum()))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new IllegalStateException(error);
        });
  }

  private void claimApp() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(create -> view.claimAppClick()
            .flatMap(promotion -> {
              appViewAnalytics.sendClickOnClaimAppViewPromotion(promotion.getPromotionId());
              return appViewManager.getAppModel()
                  .toObservable()
                  .doOnNext(app -> promotionsNavigator.navigateToClaimDialog(app.getPackageName(),
                      promotion.getPromotionId()));
            })
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new OnErrorNotImplementedException(error);
        });
  }

  private void handlePromotionClaimResult() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> promotionsNavigator.claimDialogResults())
        .filter(ClaimDialogResultWrapper::isOk)
        .doOnNext(result -> appViewManager.unscheduleNotificationSync())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private Completable downloadWallet(WalletApp walletApp) {
    return Observable.defer(() -> {
          if (appViewManager.shouldShowRootInstallWarningPopup()) {
            return view.showRootInstallWarningPopup()
                .doOnNext(answer -> appViewManager.allowRootInstall(answer));
          }
          return Observable.just(null);
        })
        .observeOn(viewScheduler)
        .flatMap(__ -> permissionManager.requestDownloadAccessWithWifiBypass(permissionService,
            walletApp.getSize()))
        .flatMap(success -> permissionManager.requestExternalStoragePermission(permissionService))
        .observeOn(Schedulers.io())
        .flatMapCompletable(__1 -> appViewManager.downloadApp(walletApp))
        .toCompletable();
  }
}

