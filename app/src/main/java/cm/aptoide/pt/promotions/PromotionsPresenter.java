package cm.aptoide.pt.promotions;

import androidx.annotation.NonNull;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.ads.MoPubAdsManager;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.Single;
import rx.exceptions.OnErrorNotImplementedException;
import rx.schedulers.Schedulers;

import static cm.aptoide.pt.AptoideApplication.APPCOINS_WALLET_PACKAGE_NAME;

public class PromotionsPresenter implements Presenter {

  private final PermissionManager permissionManager;
  private final PermissionService permissionService;
  private final PromotionsView view;
  private final PromotionsManager promotionsManager;
  private final Scheduler viewScheduler;
  private final PromotionsNavigator promotionsNavigator;
  private final PromotionsAnalytics promotionsAnalytics;
  private final String promotionType;
  private final MoPubAdsManager moPubAdsManager;
  private String promotionId;

  public PromotionsPresenter(PromotionsView view, PromotionsManager promotionsManager,
      PermissionManager permissionManager, PermissionService permissionService,
      Scheduler viewScheduler, PromotionsAnalytics promotionsAnalytics,
      PromotionsNavigator promotionsNavigator, String promotionType,
      MoPubAdsManager moPubAdsManager) {
    this.view = view;
    this.promotionsManager = promotionsManager;
    this.permissionManager = permissionManager;
    this.permissionService = permissionService;
    this.viewScheduler = viewScheduler;
    this.promotionsAnalytics = promotionsAnalytics;
    this.promotionsNavigator = promotionsNavigator;
    this.promotionType = promotionType;
    this.moPubAdsManager = moPubAdsManager;
  }

  @Override public void present() {
    getPromotionApps();
    installButtonClick();
    handleClickOnAppCard();
    pauseDownload();
    cancelDownload();
    resumeDownload();
    claimApp();
    handlePromotionClaimResult();
    handleRetryClick();
    handlePromotionOverDialogClick();
    handleOutOfSpaceDialog();
  }

  private void handleOutOfSpaceDialog() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> promotionsNavigator.outOfSpaceDialogResult())
        .filter(result -> result.getClearedSuccessfully())
        .flatMap(outOfSpaceResult -> promotionsManager.getPromotionApps(promotionId)
            .toObservable()
            .flatMapIterable(promotionApps -> promotionApps)
            .filter(promotionApp -> promotionApp.getPackageName()
                .equals(outOfSpaceResult.getPackageName()))
            .flatMapCompletable(
                app -> promotionsManager.resumeDownload(app.getMd5(), app.getPackageName(),
                    app.getAppId())))
        .retry()
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> throwable.printStackTrace());
  }

  private void claimApp() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(create -> view.claimAppClick()
            .doOnNext(promotionViewApp -> promotionsAnalytics.sendPromotionsAppInteractClaimEvent(
                promotionViewApp.getPackageName(), promotionViewApp.getAppcValue()))
            .flatMapSingle(this::showClaimView)
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
        });
  }

  @NonNull private Single<? extends String> showClaimView(PromotionViewApp promotionViewApp) {
    if (promotionId != null) {
      return Single.just(promotionId)
          .doOnSuccess(promotionsModel -> promotionsNavigator.navigateToClaimDialog(
              promotionViewApp.getPackageName(), promotionId));
    } else {
      return promotionsManager.getPromotionsModel(promotionType)
          .map(PromotionsModel::getPromotionId)
          .doOnSuccess(promotionsModel -> promotionsNavigator.navigateToClaimDialog(
              promotionViewApp.getPackageName(), promotionId));
    }
  }

  private void resumeDownload() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(create -> view.resumeDownload()
            .flatMap(promotionViewApp -> permissionManager.requestDownloadAccess(permissionService)
                .flatMap(success -> permissionManager.requestExternalStoragePermission(
                    permissionService))
                .flatMapCompletable(
                    __ -> promotionsManager.resumeDownload(promotionViewApp.getMd5(),
                        promotionViewApp.getPackageName(), promotionViewApp.getAppId()))
                .retry()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
        });
  }

  private void cancelDownload() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(create -> view.cancelDownload()
            .flatMapCompletable(
                app -> promotionsManager.cancelDownload(app.getMd5(), app.getPackageName(),
                    app.getVersionCode()))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
        });
  }

  private void pauseDownload() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.pauseDownload()
            .flatMapCompletable(
                promotionViewApp -> promotionsManager.pauseDownload(promotionViewApp.getMd5()))
            .retry())
        .observeOn(viewScheduler)
        .doOnError(throwable -> throwable.printStackTrace())
        .retry()
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new IllegalStateException(error);
        });
  }

  private void installButtonClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.installButtonClick()
            .filter(promotionViewApp -> promotionViewApp.getDownloadModel()
                .isDownloadable())
            .doOnNext(promotionViewApp -> promotionsAnalytics.sendPromotionsAppInteractInstallEvent(
                promotionViewApp.getPackageName(), promotionViewApp.getAppcValue(),
                promotionViewApp.getDownloadModel()
                    .getAction(), promotionViewApp.hasSplits(), promotionViewApp.hasAppc(),
                promotionViewApp.getRank(), null, promotionViewApp.getStoreName(),
                promotionViewApp.getObb() != null, promotionViewApp.getBdsFlags()))
            .flatMapCompletable(promotionViewApp -> downloadApp(promotionViewApp))
            .observeOn(viewScheduler)
            .doOnError(throwable -> throwable.printStackTrace())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new IllegalStateException(error);
        });
  }

  private void handleClickOnAppCard() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.appCardClick())
        .doOnNext(promotionAppClick -> promotionsNavigator.navigateToAppView(
            promotionAppClick.getApp()
                .getAppId(), promotionAppClick.getApp()
                .getPackageName()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new IllegalStateException(error);
        });
  }

  private void handlePromotionOverDialogClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.promotionOverDialogClick())
        .doOnNext(__ -> promotionsNavigator.navigateToHome())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
        });
  }

  private Completable downloadApp(PromotionViewApp promotionViewApp) {
    return Observable.defer(() -> {
          if (promotionsManager.shouldShowRootInstallWarningPopup()) {
            return view.showRootInstallWarningPopup()
                .doOnNext(answer -> promotionsManager.allowRootInstall(answer));
          }
          return Observable.just(null);
        })
        .observeOn(viewScheduler)
        .flatMap(__ -> permissionManager.requestDownloadAccess(permissionService))
        .flatMap(success -> permissionManager.requestExternalStoragePermission(permissionService))
        .observeOn(Schedulers.io())
        .flatMapCompletable(__1 -> promotionsManager.downloadApp(promotionViewApp))
        .toCompletable();
  }

  private void getPromotionApps() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> view.showLoading())
        .flatMapSingle(__ -> promotionsManager.getPromotionsModel(promotionType))
        .doOnNext(promotionsModel -> promotionId = promotionsModel.getPromotionId())
        .doOnNext(__ -> promotionsAnalytics.sendOpenPromotionsFragmentEvent())
        .observeOn(viewScheduler)
        .flatMap(this::showPromotions)
        .doOnError(__ -> view.showErrorView())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, Throwable::printStackTrace);
  }

  private Observable<? extends PromotionsModel> showPromotions(PromotionsModel appsModel) {
    if (appsModel.getAppsList()
        .isEmpty()) {
      view.showPromotionOverDialog();
      return Observable.empty();
    } else {
      view.setPromotionMessage((appsModel.getDescription()));
      view.showPromotionTitle(appsModel.getTitle());
      view.showPromotionFeatureGraphic(appsModel.getFeatureGraphic());
      return Observable.mergeDelayError(handlePromotionApps(appsModel),
          handleDownloadErrors(appsModel));
    }
  }

  private void handlePromotionClaimResult() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> promotionsNavigator.claimDialogResults())
        .filter(ClaimDialogResultWrapper::isOk)
        .doOnNext(result -> view.updateClaimStatus(result.getPackageName()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void handleRetryClick() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(event -> view.retryClicked()
            .doOnNext(__ -> view.showLoading())
            .flatMapSingle(__ -> promotionsManager.getPromotionsModel(promotionType))
            .doOnNext(promotionsModel -> promotionId = promotionsModel.getPromotionId())
            .observeOn(viewScheduler)
            .flatMap(this::showPromotions)
            .doOnError(__ -> view.showErrorView())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, Throwable::printStackTrace);
  }

  private Observable<PromotionsModel> handlePromotionApps(PromotionsModel promotionsModel) {
    return Observable.just(promotionsModel)
        .flatMapIterable(promotionsModel1 -> promotionsModel.getAppsList())
        .filter(promotionApp -> promotionApp.getPackageName()
            .equals(APPCOINS_WALLET_PACKAGE_NAME))
        .doOnNext(wallet -> view.lockPromotionApps(
            promotionsModel.isWalletInstalled() && wallet.isClaimed()))
        .map(promotionApp -> promotionsModel)
        .flatMap(__ -> Observable.just(promotionsModel)
            .flatMapIterable(promotionsModel1 -> promotionsModel.getAppsList())
            .flatMap(promotionViewApp -> promotionsManager.getDownload(promotionViewApp))
            .observeOn(viewScheduler)
            .doOnNext(promotionViewApp -> view.showPromotionApp(promotionViewApp, true))
            .filter(promotionViewApp -> promotionViewApp.getDownloadModel()
                .getAction()
                .equals(DownloadModel.Action.UPDATE))
            .flatMap(promotionViewApp -> promotionsManager.getPackageSignature(
                    promotionViewApp.getPackageName())
                .observeOn(viewScheduler)
                .map(signature -> promotionViewApp.getSignature()
                    .equals(signature))
                .doOnNext(signatureMatch -> promotionsAnalytics.sendValentineMigratorEvent(
                    promotionViewApp.getPackageName(), signatureMatch))
                .map(__2 -> promotionsModel)));
  }

  private Observable<PromotionsModel> handleDownloadErrors(PromotionsModel promotionsModel) {
    return Observable.merge(view.installButtonClick(), view.resumeDownload())
        .flatMap(__ -> Observable.from(promotionsModel.getAppsList())
            .flatMap(promotionApp -> promotionsManager.getDownload(promotionApp)
                .filter(download -> download.getDownloadModel()
                    .hasError())
                .first()
                .observeOn(viewScheduler)
                .doOnNext(promotionViewApp -> {
                  if (promotionViewApp.getDownloadModel()
                      .getDownloadState()
                      .equals(DownloadModel.DownloadState.NOT_ENOUGH_STORAGE_ERROR)) {
                    promotionsNavigator.navigateToOutOfSpaceDialog(promotionViewApp.getSize(),
                        promotionViewApp.getPackageName());
                  } else {
                    view.showDownloadError(promotionViewApp);
                  }
                })
                .flatMap(this::verifyNotEnoughSpaceError)))
        .map(__ -> promotionsModel);
  }

  private Observable<PromotionViewApp> verifyNotEnoughSpaceError(
      PromotionViewApp promotionViewApp) {
    DownloadModel downloadModel = promotionViewApp.getDownloadModel();
    if (downloadModel.getDownloadState() == DownloadModel.DownloadState.NOT_ENOUGH_STORAGE_ERROR) {
      return moPubAdsManager.getAdsVisibilityStatus()
          .doOnSuccess(offerResponseStatus -> promotionsAnalytics.sendNotEnoughSpaceErrorEvent(
              promotionViewApp.getMd5()))
          .toObservable()
          .map(__ -> promotionViewApp);
    }
    return Observable.just(promotionViewApp);
  }
}
