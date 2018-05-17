package cm.aptoide.pt.app.view;

import android.text.TextUtils;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.app.AppViewManager;
import cm.aptoide.pt.app.DownloadAppViewModel;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.share.ShareDialogs;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.Single;
import rx.exceptions.OnErrorNotImplementedException;

/**
 * Created by franciscocalado on 08/05/18.
 */

public class AppViewPresenter implements Presenter {

  private final PermissionManager permissionManager;
  private final PermissionService permissionService;
  private AppViewView view;
  private AccountNavigator accountNavigator;
  private AppViewAnalytics appViewAnalytics;
  private AppViewNavigator appViewNavigator;
  private AppViewManager appViewManager;
  private AptoideAccountManager accountManager;
  private Scheduler viewScheduler;
  private CrashReport crashReport;
  private long appId;
  private String packageName;

  public AppViewPresenter(AppViewView view, AccountNavigator accountNavigator,
      AppViewAnalytics appViewAnalytics, AppViewNavigator appViewNavigator,
      AppViewManager appViewManager, AptoideAccountManager accountManager, Scheduler viewScheduler,
      CrashReport crashReport, long appId, String packageName, PermissionManager permissionManager,
      PermissionService permissionService) {
    this.view = view;
    this.accountNavigator = accountNavigator;
    this.appViewAnalytics = appViewAnalytics;
    this.appViewNavigator = appViewNavigator;
    this.appViewManager = appViewManager;
    this.accountManager = accountManager;
    this.viewScheduler = viewScheduler;
    this.crashReport = crashReport;
    this.appId = appId;
    this.packageName = packageName;
    this.permissionManager = permissionManager;
    this.permissionService = permissionService;
  }

  @Override public void present() {
    handleFirstLoad();
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

    handleInstallButtonClick();
    pauseDownload();
    resumeDownload();
    cancelDownload();
    loadDownloadApp();
  }

  private void handleFirstLoad() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> view.showLoading())
        .flatMapSingle(__ -> appViewManager.getDetailedAppViewModel(appId, packageName)
            .flatMap(detailedAppViewModel -> appViewManager.getDownloadAppViewModel(
                detailedAppViewModel.getMd5(), detailedAppViewModel.getPackageName(),
                detailedAppViewModel.getVerCode())
                .first()
                .observeOn(viewScheduler)
                .doOnNext(downloadAppViewModel -> view.showDownloadAppModel(downloadAppViewModel))
                .doOnNext(downloadAppViewModel -> view.readyToDownload())
                .toSingle()
                .map(downloadAppViewModel -> detailedAppViewModel)))
        .observeOn(viewScheduler)
        .doOnNext(appViewModel -> view.populateAppDetails(appViewModel))
        .flatMapSingle(appViewModel -> Single.zip(appViewManager.getReviewsViewModel(
            appViewModel.getDetailedApp()
                .getStore()
                .getName(), packageName, 5, view.getLanguageFilter())
                .observeOn(viewScheduler), appViewManager.loadSimilarApps(packageName,
            appViewModel.getDetailedApp()
                .getMedia()
                .getKeywords(), 2)
                .observeOn(viewScheduler),
            (reviews, similar) -> view.populateReviewsAndAds(reviews, similar,
                appViewModel.getDetailedApp())))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
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
        .flatMapSingle(__ -> appViewManager.getDetailedAppViewModel(appId, packageName))
        .filter(app -> !TextUtils.isEmpty(app.getDetailedApp()
            .getDeveloper()
            .getWebsite()))
        .doOnNext(app -> view.navigateToDeveloperWebsite(app.getDetailedApp()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnDeveloperEmail() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickDeveloperEmail())
        .flatMapSingle(__ -> appViewManager.getDetailedAppViewModel(appId, packageName))
        .filter(app -> !TextUtils.isEmpty(app.getDetailedApp()
            .getDeveloper()
            .getEmail()))
        .doOnNext(app -> view.navigateToDeveloperEmail(app.getDetailedApp()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnDeveloperPrivacy() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickDeveloperPrivacy())
        .flatMapSingle(__ -> appViewManager.getDetailedAppViewModel(appId, packageName))
        .filter(app -> !TextUtils.isEmpty(app.getDetailedApp()
            .getDeveloper()
            .getPrivacy()))
        .doOnNext(app -> view.navigateToDeveloperPrivacy(app.getDetailedApp()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnDeveloperPermissions() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickDeveloperPermissions())
        .flatMapSingle(__ -> appViewManager.getDetailedAppViewModel(appId, packageName))
        .doOnNext(app -> view.navigateToDeveloperPermissions(app.getDetailedApp()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnStoreLayout() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickStoreLayout())
        .flatMapSingle(__ -> appViewManager.getDetailedAppViewModel(appId, packageName))
        .doOnNext(app -> appViewNavigator.navigateToStore(app.getDetailedApp()
            .getStore()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnFollowStore() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickFollowStore())
        .flatMapSingle(__ -> appViewManager.getDetailedAppViewModel(appId, packageName))
        .observeOn(viewScheduler)
        .flatMapCompletable(model -> {
          if (model.isStoreFollowed()) {
            view.setFollowButton(true);
            appViewAnalytics.sendOpenStoreEvent();
            appViewNavigator.navigateToStore(model.getDetailedApp()
                .getStore());
            return Completable.complete();
          } else {
            view.setFollowButton(false);
            appViewAnalytics.sendFollowStoreEvent();
            view.displayStoreFollowedSnack(model.getDetailedApp()
                .getStore()
                .getName());
            return appViewManager.subscribeStore(model.getDetailedApp()
                .getStore()
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
        .flatMapSingle(__ -> appViewManager.getDetailedAppViewModel(appId, packageName))
        .doOnNext(model -> {
          appViewAnalytics.sendOtherVersionsEvent();
          appViewNavigator.navigateToOtherVersions(model.getDetailedApp()
              .getName(), model.getDetailedApp()
              .getIcon(), model.getDetailedApp()
              .getPackageName());
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnTrustedBadge() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickTrustedBadge())
        .flatMapSingle(__ -> appViewManager.getDetailedAppViewModel(appId, packageName))
        .doOnNext(model -> view.showTrustedDialog(model.getDetailedApp()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnRateApp() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> Observable.merge(view.clickRateApp(), view.clickRateAppLarge(),
            view.clickRateAppLayout()))
        .flatMapSingle(__ -> appViewManager.getDetailedAppViewModel(appId, packageName))
        .observeOn(viewScheduler)
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
        .flatMapSingle(__ -> appViewManager.getDetailedAppViewModel(appId, packageName))
        .doOnNext(
            model -> appViewNavigator.navigateToRateAndReview(model.getAppId(), model.getAppName(),
                model.getStore()
                    .getName(), model.getPackageName(), model.getStore()
                    .getAppearance()
                    .getTheme()))
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
            .flatMapSingle(__ -> appViewManager.getDetailedAppViewModel(appId, packageName))
            .flatMapSingle(model -> appViewManager.addApkFlagRequestAction(model.getStore()
                .getName(), model.getMd5Sum(), type))
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
            appViewNavigator.navigateToAd(similarAppClickEvent.getSimilar()
                .getAd());
          } else {
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
        .flatMapSingle(__ -> appViewManager.getDetailedAppViewModel(appId, packageName))
        .observeOn(viewScheduler)
        .doOnNext(app -> view.defaultShare(app.getAppName(), app.getwUrls()))
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
        .flatMapSingle(__ -> appViewManager.getDetailedAppViewModel(appId, packageName))
        .observeOn(viewScheduler)
        .doOnNext(appModel -> view.recommendsShare(appModel.getPackageName(), appModel.getStore()
            .getId()))
        .subscribe(__ -> {
        }, e -> crashReport.log(e));
  }

  private void cancelDownload() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(create -> view.cancelDownload()
            .flatMapSingle(__ -> appViewManager.getDetailedAppViewModel(appId, packageName))
            .flatMapCompletable(
                app -> appViewManager.cancelDownload(app.getMd5(), packageName, app.getVerCode()))
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
                .flatMapSingle(__1 -> appViewManager.getDetailedAppViewModel(appId, packageName))
                .flatMapCompletable(
                    app -> appViewManager.resumeDownload(app.getMd5(), packageName, appId))
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
            .flatMapSingle(__ -> appViewManager.getDetailedAppViewModel(appId, packageName))
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
        .flatMap(create -> view.installAppClick()
            .flatMapCompletable(action -> {
              Completable completable = null;
              switch (action) {
                case INSTALL:
                case UPDATE:
                  completable = downloadApp(action);
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
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
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
    return Completable.fromAction(() -> view.openApp(packageName));
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
            .flatMapCompletable(__1 -> appViewManager.downloadApp(action, packageName, appId)))
        .toCompletable();
  }

  private void loadDownloadApp() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(created -> view.isAppViewReadyToDownload())
        .flatMap(create -> appViewManager.getDetailedAppViewModel(appId, packageName)
            .toObservable())
        .filter(app -> !app.isLoading())
        .flatMap(app -> appViewManager.getDownloadAppViewModel(app.getMd5(), packageName,
            app.getVerCode()))
        .observeOn(viewScheduler)
        .doOnNext(model -> view.showDownloadAppModel(model))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new IllegalStateException(error);
        });
  }
}

