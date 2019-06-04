package cm.aptoide.pt.promotions;

import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.exceptions.OnErrorNotImplementedException;
import rx.schedulers.Schedulers;

public class PromotionsPresenter implements Presenter {

  private final PermissionManager permissionManager;
  private final PermissionService permissionService;
  private final PromotionsView view;
  private final PromotionsManager promotionsManager;
  private final Scheduler viewScheduler;
  private final PromotionsNavigator promotionsNavigator;
  private final PromotionsAnalytics promotionsAnalytics;
  private final String promotionId;

  public PromotionsPresenter(PromotionsView view, PromotionsManager promotionsManager,
      PermissionManager permissionManager, PermissionService permissionService,
      Scheduler viewScheduler, PromotionsAnalytics promotionsAnalytics,
      PromotionsNavigator promotionsNavigator, String promotionId) {
    this.view = view;
    this.promotionsManager = promotionsManager;
    this.permissionManager = permissionManager;
    this.permissionService = permissionService;
    this.viewScheduler = viewScheduler;
    this.promotionsAnalytics = promotionsAnalytics;
    this.promotionsNavigator = promotionsNavigator;
    this.promotionId = promotionId;
  }

  @Override public void present() {
    getPromotionApps();
    installButtonClick();
    pauseDownload();
    cancelDownload();
    resumeDownload();
    claimApp();
    handlePromotionClaimResult();
    handleRetryClick();
    handlePromotionOverDialogClick();
  }

  private void claimApp() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(create -> view.claimAppClick()
            .doOnNext(promotionViewApp -> promotionsAnalytics.sendPromotionsAppInteractClaimEvent(
                promotionViewApp.getPackageName(), promotionViewApp.getAppcValue()))
            .doOnNext(promotionViewApp -> promotionsNavigator.navigateToClaimDialog(
                promotionViewApp.getPackageName(), promotionId))
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
                    .getAction()))
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
        .flatMap(__ -> promotionsManager.getPromotionsModel(promotionId))
        .doOnNext(__ -> promotionsAnalytics.sendOpenPromotionsFragmentEvent())
        .observeOn(viewScheduler)
        .flatMap(appsModel -> {
          if (appsModel.getAppsList()
              .isEmpty()) {
            view.showPromotionOverDialog();
            return Observable.empty();
          } else {
            view.showAppCoinsAmount((appsModel.getTotalAppcValue()));
            return handlePromotionApps(appsModel);
          }
        })
        .doOnError(__ -> view.showErrorView())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throwable.printStackTrace();
        });
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
            .flatMap(__ -> promotionsManager.getPromotionsModel(promotionId))
            .observeOn(viewScheduler)
            .flatMap(appsModel -> {
              if (appsModel.getAppsList()
                  .isEmpty()) {
                view.showPromotionOverDialog();
                return Observable.empty();
              } else {
                view.showAppCoinsAmount((appsModel.getTotalAppcValue()));
                return handlePromotionApps(appsModel);
              }
            })
            .doOnError(__ -> view.showErrorView())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
        });
  }

  private Observable<PromotionsModel> handlePromotionApps(PromotionsModel promotionsModel) {
    return Observable.just(promotionsModel)
        .flatMapIterable(promotionsModel1 -> promotionsModel.getAppsList())
        .filter(promotionApp -> promotionApp.getPackageName()
            .equals("com.appcoins.wallet"))
        .doOnNext(wallet -> view.lockPromotionApps(
            promotionsModel.isWalletInstalled() && wallet.isClaimed()))
        .map(promotionApp -> promotionsModel)
        .flatMap(__ -> Observable.just(promotionsModel)
            .flatMapIterable(promotionsModel1 -> promotionsModel.getAppsList())
            .flatMap(promotionViewApp -> promotionsManager.getDownload(promotionViewApp))
            .observeOn(viewScheduler)
            .doOnNext(promotionViewApp -> view.showPromotionApp(promotionViewApp,
                promotionsModel.isWalletInstalled()))
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
}
