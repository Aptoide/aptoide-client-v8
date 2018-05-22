package cm.aptoide.pt.app.view;

import android.text.TextUtils;
import android.text.format.DateUtils;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.app.AppViewManager;
import cm.aptoide.pt.app.AppViewViewModel;
import cm.aptoide.pt.app.DownloadAppViewModel;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.share.ShareDialogs;
import cm.aptoide.pt.store.StoreAnalytics;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorNotImplementedException;

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
  private StoreAnalytics storeAnalytics;
  private AppViewNavigator appViewNavigator;
  private AppViewManager appViewManager;
  private AptoideAccountManager accountManager;
  private Scheduler viewScheduler;
  private CrashReport crashReport;

  public AppViewPresenter(AppViewView view, AccountNavigator accountNavigator,
      AppViewAnalytics appViewAnalytics, StoreAnalytics storeAnalytics,
      AppViewNavigator appViewNavigator, AppViewManager appViewManager,
      AptoideAccountManager accountManager, Scheduler viewScheduler, CrashReport crashReport,
      PermissionManager permissionManager, PermissionService permissionService) {
    this.view = view;
    this.accountNavigator = accountNavigator;
    this.appViewAnalytics = appViewAnalytics;
    this.storeAnalytics = storeAnalytics;
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
    handleClickReadComments();
    handleClickFlags();
    handleClickLoginSnack();
    handleClickOnSimilarApps();
    handleClickOnToolbar();
    handleDefaultShare();
    handleRecommendsShare();
    handleClickOnRetry();

    handleInstallButtonClick();
    pauseDownload();
    resumeDownload();
    cancelDownload();
    loadDownloadApp();
    continueRecommendsDialogClick();
    skipRecommendsDialogClick();
    dontShowAgainRecommendsDialogClick();
    handleNotLoggedinShareResults();
  }

  private void handleFirstLoad() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> view.showLoading())
        .flatMap(__ -> loadApp())
        .doOnNext(model -> appViewAnalytics.sendAppViewOpenedFromEvent(model.getPackageName(),
            model.getDeveloper()
                .getName(), model.getMalware()
                .getRank()
                .name()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleReviewAutoScroll() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.scrollReviewsResponse())
        .flatMap(reviews -> scheduleAnimations(reviews))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleClickOnScreenshot() {
    view.getLifecycle()
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
    view.getLifecycle()
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
    view.getLifecycle()
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
    view.getLifecycle()
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

  private void handleClickOnDeveloperEmail() {
    view.getLifecycle()
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
    view.getLifecycle()
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
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickDeveloperPermissions())
        .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
        .doOnNext(app -> view.navigateToDeveloperPermissions(app))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnStoreLayout() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickStoreLayout())
        .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
        .doOnNext(app -> {
          storeAnalytics.sendStoreOpenEvent("App View", app.getStore()
              .getName(), true);
          appViewAnalytics.sendOpenStoreEvent();
          appViewNavigator.navigateToStore(app.getStore());
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnFollowStore() {
    view.getLifecycle()
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
    view.getLifecycle()
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
    view.getLifecycle()
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
    view.getLifecycle()
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

  private void handleClickReadComments() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> Observable.merge(view.clickCommentsLayout(), view.clickReadAllComments()))
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
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> Observable.merge(view.clickVirusFlag(), view.clickLicenseFlag(),
            view.clickWorkingFlag(), view.clickFakeFlag()))
        .doOnNext(type -> view.disableFlags())
        .flatMap(type -> accountManager.accountStatus()
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
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickLoginSnack())
        .doOnNext(__ -> accountNavigator.navigateToAccountView(
            AccountAnalytics.AccountOrigins.APP_VIEW_FLAG))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnSimilarApps() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickSimilarApp())
        .doOnNext(similarAppClickEvent -> {
          if (similarAppClickEvent.getSimilar()
              .isAd()) {
            appViewAnalytics.sendSimilarAppsInteractEvent(similarAppClickEvent.getType());
            appViewNavigator.navigateToAd(similarAppClickEvent.getSimilar()
                .getAd());
          } else {
            appViewAnalytics.sendSimilarAppsInteractEvent(similarAppClickEvent.getType());
            appViewNavigator.navigateToAppView(similarAppClickEvent.getSimilar()
                .getApp()
                .getAppId(), similarAppClickEvent.getSimilar()
                .getApp()
                .getPackageName(), "");
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnToolbar() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickToolbar())
        .filter(menuItem -> menuItem != null)
        .map(menuItem -> menuItem.getItemId())
        .doOnNext(itemId -> {
          switch (itemId) {
            case R.id.menu_item_share:
              view.showShareDialog();
              break;

            case R.id.menu_remote_install:
              appViewAnalytics.sendRemoteInstallEvent();
              view.showShareOnTvDialog();
              break;
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void handleDefaultShare() {
    view.getLifecycle()
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
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.shareDialogResponse())
        .filter(response -> response == ShareDialogs.ShareResponse.SHARE_TIMELINE)
        .flatMap(__ -> accountManager.accountStatus())
        .observeOn(viewScheduler)
        .flatMap(account -> {
          if (account.isLoggedIn()) {
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
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> Observable.merge(view.clickNoNetworkRetry(), view.clickGenericRetry()))
        .doOnNext(__ -> view.showLoading())
        .flatMap(__ -> loadApp())
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private Observable<Integer> scheduleAnimations(int topReviewsCount) {
    if (topReviewsCount <= 1) {
      // not enough elements for animation
      Logger.w(TAG, "Not enough top reviews to do paging animation.");
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
        .flatMap(
            appViewViewModel -> appViewManager.getDownloadAppViewModel(appViewViewModel.getMd5(),
                appViewViewModel.getPackageName(), appViewViewModel.getVersionCode())
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
            view.populateAppDetails(appViewModel);
          }
        })
        .filter(model -> !model.hasError())
        .flatMapCompletable(appViewModel -> Completable.merge(updateReviews(appViewModel),
            updateSuggestedApps(appViewModel)));
  }

  private Completable updateSuggestedApps(AppViewViewModel appViewModel) {
    return appViewManager.loadSimilarApps(appViewModel.getPackageName(), appViewModel.getMedia()
        .getKeywords())
        .observeOn(viewScheduler)
        .doOnSuccess(adsViewModel -> {
          view.populateAds(adsViewModel);
        })
        .toCompletable();
  }

  private Completable updateReviews(AppViewViewModel appViewModel) {
    return appViewManager.loadReviewsViewModel(appViewModel.getStore()
        .getName(), view.getPackageName(), view.getLanguageFilter())
        .observeOn(viewScheduler)
        .doOnSuccess(reviewsViewModel -> {
          view.populateReviews(reviewsViewModel, appViewModel);
        })
        .toCompletable();
  }

  private void cancelDownload() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(create -> view.cancelDownload()
            .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
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
    view.getLifecycle()
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
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(create -> view.pauseDownload()
            .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
            .flatMapCompletable(app -> appViewManager.pauseDownload(app.getMd5()))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
        });
  }

  private void handleInstallButtonClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(create -> accountManager.accountStatus())
        .observeOn(viewScheduler)
        .flatMap(account -> view.installAppClick()
            .flatMapCompletable(action -> {
              Completable completable = null;
              switch (action) {
                case INSTALL:
                case UPDATE:
                  completable = appViewManager.loadAppViewViewModel()
                      .observeOn(viewScheduler)
                      .flatMapCompletable(
                          appViewViewModel -> downloadApp(action).observeOn(viewScheduler)
                              .doOnCompleted(() -> {
                                if (account.isLoggedIn()
                                    && appViewManager.shouldShowRecommendsPreviewDialog()) {
                                  view.showRecommendsDialog();
                                  appViewAnalytics.sendRecommendAppDialogShowEvent(
                                      appViewViewModel.getPackageName());
                                } else if (!account.isLoggedIn()
                                    && appViewManager.canShowNotLoggedInDialog()) {
                                  appViewNavigator.navigateToNotLoggedInShareFragmentForResult();
                                }
                              }));
                  break;
                case OPEN:
                  completable = openInstalledApp();
                  break;
                case DOWNGRADE:
                  completable = downgradeApp(action);
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



  private Completable downgradeApp(DownloadAppViewModel.Action action) {
    return view.showDowngradeMessage()
        .filter(downgrade -> downgrade)
        .doOnNext(__ -> view.showDowngradingMessage())
        .flatMapCompletable(__ -> downloadApp(action))
        .toCompletable();
  }

  private Completable openInstalledApp() {
    return Completable.fromAction(() -> view.openApp(""));
  }

  private Completable downloadApp(DownloadAppViewModel.Action action) {
    return Observable.defer(() -> {
      if (appViewManager.showRootInstallWarningPopup()) {
        return view.showRootInstallWarningPopup()
            .doOnNext(answer -> appViewManager.saveRootInstallWarning(answer))
            .map(__ -> action);
      }
      return Observable.just(action);
    })
        .flatMap(__ -> permissionManager.requestDownloadAccess(permissionService)
            .flatMap(
                success -> permissionManager.requestExternalStoragePermission(permissionService))
            .flatMapCompletable(__1 -> appViewManager.downloadApp(action, "", -1)))
        .toCompletable();
  }

  private void loadDownloadApp() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(created -> view.isAppViewReadyToDownload())
        .flatMap(create -> appViewManager.loadAppViewViewModel()
            .toObservable())
        .filter(app -> !app.isLoading())
        .flatMap(app -> appViewManager.getDownloadAppViewModel(app.getMd5(), app.getPackageName(),
            app.getVersionCode()))
        .observeOn(viewScheduler)
        .doOnNext(model -> view.showDownloadAppModel(model))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new IllegalStateException(error);
        });
  }

  private void continueRecommendsDialogClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(created -> view.continueRecommendsDialogClick()
            .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
            .flatMapCompletable(app -> appViewManager.shareOnTimeline(app.getPackageName(),
                app.getStore()
                    .getId(), "install"))
            .doOnNext(app -> appViewAnalytics.sendTimelineInstallRecommendContinueEvents(
                app.getPackageName()))
            .doOnNext(__ -> view.showRecommendsThanksMessage())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new IllegalStateException(error);
        });
  }

  private void skipRecommendsDialogClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(created -> view.skipRecommendsDialogClick()
            .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
            .doOnNext(app -> appViewAnalytics.sendTimelineInstallRecommendSkipEvents(
                app.getPackageName())))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new IllegalStateException(error);
        });
  }

  private void dontShowAgainRecommendsDialogClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(created -> view.dontShowAgainRecommendsDialogClick()
            .flatMapCompletable(__ -> appViewManager.dontShowInstallRecommendsPreviewDialog())
            .flatMapSingle(__ -> appViewManager.loadAppViewViewModel())
            .doOnNext(app -> appViewAnalytics.sendTimelineInstallRecommendDontShowMeAgainEvents(
                app.getPackageName())))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new IllegalStateException(error);
        });
  }

  private void handleNotLoggedinShareResults() {
    view.getLifecycle()
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
          throw new IllegalStateException(error);
        });
  }
}

