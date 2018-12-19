package cm.aptoide.pt.promotions;

import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
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

  public PromotionsPresenter(PromotionsView view, PromotionsManager promotionsManager,
      PermissionManager permissionManager, PermissionService permissionService,
      Scheduler viewScheduler, PromotionsAnalytics promotionsAnalytics,
      PromotionsNavigator promotionsNavigator) {
    this.view = view;
    this.promotionsManager = promotionsManager;
    this.permissionManager = permissionManager;
    this.permissionService = permissionService;
    this.viewScheduler = viewScheduler;
    this.promotionsAnalytics = promotionsAnalytics;
    this.promotionsNavigator = promotionsNavigator;
  }

  @Override public void present() {
    getPromotionApps();
    installButtonClick();
    pauseDownload();
    cancelDownload();
    resumeDownload();
    claimApp();
    handlePromotionClaimResult();
  }

  private void claimApp() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(create -> view.claimAppClick()
            .doOnNext(promotionViewApp -> promotionsAnalytics.sendPromotionsAppInteractClaimEvent(
                promotionViewApp.getPackageName(), promotionViewApp.getAppcValue()))
            .doOnNext(promotionViewApp -> promotionsNavigator.navigateToClaimDialog(
                promotionViewApp.getPackageName()))
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
        .flatMap(__ -> promotionsManager.getPromotionsModel())
        .doOnNext(promotionsModel -> promotionsAnalytics.sendOpenPromotionsFragmentEvent())
        .observeOn(viewScheduler)
        .doOnNext(promotionsModel -> view.showAppCoinsAmount((promotionsModel.getTotalAppcValue())))
        .doOnNext(promotionsModel -> view.lockPromotionApps(promotionsModel.isWalletInstalled()))
        .flatMapIterable(promotionsModel -> promotionsModel.getAppsList())
        .flatMap(promotionViewApp -> promotionsManager.getDownload(promotionViewApp))
        .observeOn(viewScheduler)
        .doOnNext(promotionViewApp -> view.showPromotionApp(promotionViewApp))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
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
}
