package cm.aptoide.pt.app.view;

import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.text.format.DateUtils;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.R;
import cm.aptoide.pt.abtesting.experiments.SimilarAdExperiment;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.ads.model.ApplicationAd;
import cm.aptoide.pt.ads.model.AptoideNativeAd;
import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.app.AppViewManager;
import cm.aptoide.pt.app.AppViewSimilarApp;
import cm.aptoide.pt.app.AppViewSimilarAppAnalytics;
import cm.aptoide.pt.app.AppViewViewModel;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.app.ReviewsViewModel;
import cm.aptoide.pt.app.SimilarAppsViewModel;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.share.ShareDialogs;
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
  private SimilarAppsViewModel similarAppsViewModel;
  private AccountNavigator accountNavigator;
  private AppViewAnalytics appViewAnalytics;
  private AppViewSimilarAppAnalytics similarAppAnalytics;
  private AppViewNavigator appViewNavigator;
  private AppViewManager appViewManager;
  private AptoideAccountManager accountManager;
  private Scheduler viewScheduler;
  private CrashReport crashReport;

  private SimilarAdExperiment similarAdExperiment;

  public AppViewPresenter(AppViewView view, AccountNavigator accountNavigator,
      AppViewAnalytics appViewAnalytics, AppViewSimilarAppAnalytics similarAppAnalytics,
      AppViewNavigator appViewNavigator, AppViewManager appViewManager,
      AptoideAccountManager accountManager, Scheduler viewScheduler, CrashReport crashReport,
      PermissionManager permissionManager, PermissionService permissionService,
      SimilarAdExperiment similarAdExperiment) {
    this.view = view;
    this.accountNavigator = accountNavigator;
    this.appViewAnalytics = appViewAnalytics;
    this.similarAppAnalytics = similarAppAnalytics;
    this.appViewNavigator = appViewNavigator;
    this.appViewManager = appViewManager;
    this.accountManager = accountManager;
    this.viewScheduler = viewScheduler;
    this.crashReport = crashReport;
    this.permissionManager = permissionManager;
    this.permissionService = permissionService;
    this.similarAdExperiment = similarAdExperiment;
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
  }

  @VisibleForTesting public void handleFirstLoad() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> view.showLoading())
        .flatMap(__ -> loadApp().flatMap(
            appViewViewModel -> manageOrganicAds(appViewViewModel.getMinimalAd()).toObservable()
                .map(__1 -> appViewViewModel)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private Completable manageOrganicAds(SearchAdResult searchAdResult) {
    if (searchAdResult == null) {
      return appViewManager.loadAdsFromAppView()
          .doOnSuccess(ad -> {
            appViewManager.setSearchAdResult(ad);
            handleAdsLogic(appViewManager.getSearchAdResult());
          })
          .doOnError(throwable -> crashReport.log(throwable))
          .toCompletable();
    }
    return Completable.complete()
        .doOnCompleted(() -> handleAdsLogic(searchAdResult));
  }

  private void handleOnScroll() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(lifecycleEvent -> view.scrollVisibleSimilarApps())
        .takeUntil(__ -> view.isSimilarAppsVisible())
        .observeOn(Schedulers.io())
        .flatMap(__ -> {
          if (similarAppsViewModel != null && similarAppsViewModel.getAd() != null) {
            similarAppAnalytics.similarAppBundleImpression(similarAppsViewModel.getAd()
                .getNetwork(), true);
            return similarAdExperiment.recordAdImpression();
          }
          similarAppAnalytics.similarAppBundleImpression(null, false);
          return Observable.empty();
        })
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
        .observeOn(Schedulers.io())
        .flatMap(similarAppClickEvent -> {
          if(similarAppClickEvent.getSimilar().isAd()){
            return similarAdExperiment.recordAdClick().map(__ -> similarAppClickEvent);
          }
          return Observable.just(similarAppClickEvent);
        })
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
              appViewNavigator.navigateToAd(
                  ((AptoideNativeAd) appViewSimilarApp.getAd()).getMinimalAd(),
                  similarAppClickEvent.getType());
            }
          } else {
            packageName = appViewSimilarApp.getApp()
                .getPackageName();
            appViewNavigator.navigateToAppView(appViewSimilarApp.getApp()
                .getAppId(), packageName, similarAppClickEvent.getType());
          }
          appViewAnalytics.sendSimilarAppsInteractEvent(similarAppClickEvent.getType());
          similarAppAnalytics.similarAppClick(network, packageName,
              similarAppClickEvent.getPosition(), isAd);
          return Observable.just(isAd);
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));

    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> appViewManager.appNextAdClick()
            .observeOn(Schedulers.io())
            .flatMap(result -> similarAdExperiment.recordAdClick()))
        .retry()
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
                .doOnNext(downloadAppViewModel -> view.showDownloadAppModel(downloadAppViewModel))
                .doOnNext(downloadAppViewModel -> view.readyToDownload())
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
          if (appViewModel.getOpenType() == NewAppViewFragment.OpenType.OPEN_AND_INSTALL) {

            return accountManager.accountStatus()
                .first()
                .observeOn(viewScheduler)
                .flatMapCompletable(account -> downloadApp(DownloadModel.Action.INSTALL,
                    appViewModel.getPackageName(), appViewModel.getAppId()).doOnCompleted(
                    () -> appViewAnalytics.clickOnInstallButton(appViewModel.getPackageName(),
                        appViewModel.getDeveloper()
                            .getName(), DownloadModel.Action.INSTALL.toString()))
                    .doOnCompleted(() -> showRecommendsDialog(account.isLoggedIn(),
                        appViewModel.getPackageName()))
                    .observeOn(viewScheduler))
                .map(__ -> appViewModel);
          } else if (appViewModel.getOpenType()
              == NewAppViewFragment.OpenType.OPEN_WITH_INSTALL_POPUP) {
            return accountManager.accountStatus()
                .first()
                .observeOn(viewScheduler)
                .flatMap(account -> view.showOpenAndInstallDialog(appViewModel.getMarketName(),
                    appViewModel.getAppName())
                    .flatMapCompletable(action -> downloadApp(action, appViewModel.getPackageName(),
                        appViewModel.getAppId()).doOnCompleted(
                        () -> appViewAnalytics.clickOnInstallButton(appViewModel.getPackageName(),
                            appViewModel.getDeveloper()
                                .getName(), action.toString()))
                        .doOnCompleted(() -> showRecommendsDialog(account.isLoggedIn(),
                            appViewModel.getPackageName()))
                        .observeOn(viewScheduler)))
                .map(__ -> appViewModel);
          } else if (appViewModel.getOpenType()
              == NewAppViewFragment.OpenType.APK_FY_INSTALL_POPUP) {
            return accountManager.accountStatus()
                .first()
                .observeOn(viewScheduler)
                .flatMap(account -> view.showOpenAndInstallApkFyDialog(appViewModel.getMarketName(),
                    appViewModel.getAppName(), appViewModel.getAppc(), appViewModel.getRating()
                        .getAverage(), appViewModel.getIcon(), appViewModel.getPackageDownloads())
                    .flatMapCompletable(action -> downloadApp(action, appViewModel.getPackageName(),
                        appViewModel.getAppId()).observeOn(viewScheduler)
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
        .flatMap(appViewModel -> Observable.zip(updateSuggestedApps(appViewModel),
            updateReviews(appViewModel),
            (similarAppsViewModel, reviewsViewModel) -> Observable.just(appViewModel))
            .first()
            .map(__ -> appViewModel));
  }

  private Observable<SimilarAppsViewModel> updateSuggestedApps(AppViewViewModel appViewModel) {
    return appViewManager.loadSimilarAppsViewModel(appViewModel.getPackageName(),
        appViewModel.getMedia()
            .getKeywords())
        .observeOn(viewScheduler)
        .doOnError(__ -> view.hideSimilarApps())
        .doOnSuccess(adsViewModel -> {
          this.similarAppsViewModel = adsViewModel;
          if (!adsViewModel.hasSimilarApps()) {
            view.hideSimilarApps();
          } else if (adsViewModel.hasError()) {
            if (adsViewModel.hasRecommendedAppsError()) view.hideSimilarApps();
            if (adsViewModel.hasAdError()) view.populateSimilarWithoutAds(adsViewModel);
          } else {
            view.populateSimilar(adsViewModel);
          }
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
                    app -> appViewManager.resumeDownload(app.getMd5(), app.getPackageName(),
                        app.getAppId()))
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
                          appViewModel -> downloadApp(action, appViewModel.getPackageName(),
                              appViewModel.getAppId()).observeOn(viewScheduler)
                              .doOnCompleted(() -> appViewAnalytics.clickOnInstallButton(
                                  appViewModel.getPackageName(), appViewModel.getDeveloper()
                                      .getName(), action.toString()))
                              .doOnCompleted(() -> showRecommendsDialog(account.isLoggedIn(),
                                  appViewModel.getPackageName())));
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
                      .flatMapCompletable(appViewViewModel -> downgradeApp(action,
                          appViewViewModel.getPackageName(), appViewViewModel.getAppId()));
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

  private Completable downgradeApp(DownloadModel.Action action, String packageName, long appId) {
    return view.showDowngradeMessage()
        .filter(downgrade -> downgrade)
        .doOnNext(__ -> view.showDowngradingMessage())
        .flatMapCompletable(__ -> downloadApp(action, packageName, appId))
        .toCompletable();
  }

  private Completable openInstalledApp(String packageName) {
    return Completable.fromAction(() -> view.openApp(packageName));
  }

  private Completable downloadApp(DownloadModel.Action action, String packageName, long appId) {
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
            .flatMapCompletable(__1 -> appViewManager.downloadApp(action, packageName, appId)))
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
            app.getVersionCode(), app.isPaid(), app.getPay()))
        .observeOn(viewScheduler)
        .doOnNext(model -> view.showDownloadAppModel(model))
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
                        .andThen(downloadApp(DownloadModel.Action.INSTALL,
                            appViewViewModel.getPackageName(), appViewViewModel.getAppId())))
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
}

