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
  private PromotionsView view;
  private PromotionsManager promotionsManager;
  private Scheduler viewScheduler;

  public PromotionsPresenter(PromotionsView view, PromotionsManager promotionsManager,
      PermissionManager permissionManager, PermissionService permissionService,
      Scheduler viewScheduler) {
    this.view = view;
    this.promotionsManager = promotionsManager;
    this.permissionManager = permissionManager;
    this.permissionService = permissionService;
    this.viewScheduler = viewScheduler;
  }

  @Override public void present() {
    getPromotionApps();

    installButtonClick();

    pauseDownload();
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
        .flatMap(__ -> view.installButtonClick())
        .filter(promotionViewApp -> promotionViewApp.getDownloadModel()
            .isDownloadable())
        .flatMapCompletable(promotionViewApp -> downloadApp(promotionViewApp))
        .observeOn(viewScheduler)
        .doOnError(throwable -> throwable.printStackTrace())
        .retry()
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
        .flatMap(__ -> promotionsManager.getPromotionApps())
        .flatMapIterable(promotionsList -> promotionsList)
        .flatMap(promotionViewApp -> promotionsManager.getDownload(promotionViewApp))
        .observeOn(viewScheduler)
        .doOnNext(promotionViewApp -> view.showPromotionApp(promotionViewApp))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }
}
