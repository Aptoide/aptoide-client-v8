package cm.aptoide.pt.editorial;

import android.support.annotation.VisibleForTesting;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.Single;
import rx.exceptions.OnErrorNotImplementedException;
import rx.schedulers.Schedulers;

/**
 * Created by D01 on 27/08/2018.
 */

public class EditorialPresenter implements Presenter {

  private final PermissionManager permissionManager;
  private final PermissionService permissionService;
  private final EditorialView view;
  private final EditorialManager editorialManager;
  private final Scheduler viewScheduler;
  private final CrashReport crashReporter;
  private final EditorialAnalytics editorialAnalytics;
  private final EditorialNavigator editorialNavigator;

  public EditorialPresenter(EditorialView view, EditorialManager editorialManager,
      Scheduler viewScheduler, CrashReport crashReporter, PermissionManager permissionManager,
      PermissionService permissionService, EditorialAnalytics editorialAnalytics,
      EditorialNavigator editorialNavigator) {
    this.view = view;
    this.editorialManager = editorialManager;
    this.viewScheduler = viewScheduler;
    this.crashReporter = crashReporter;
    this.permissionManager = permissionManager;
    this.permissionService = permissionService;
    this.editorialAnalytics = editorialAnalytics;
    this.editorialNavigator = editorialNavigator;
  }

  @Override public void present() {
    onCreateLoadAppOfTheWeek();
    handleRetryClick();
    handleClickOnMedia();
    handleClickOnAppCard();
    handlePaletteColor();

    handleInstallClick();
    pauseDownload();
    resumeDownload();
    cancelDownload();
    loadDownloadApp();

    handlePlaceHolderVisibilityChange();
    handlePlaceHolderVisibility();
    handleMediaListDescriptionVisibility();
    handleClickActionButtonCard();
    handleMovingCollapse();
  }

  @VisibleForTesting public void onCreateLoadAppOfTheWeek() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .doOnNext(created -> view.showLoading())
        .flatMapSingle(created -> loadEditorialViewModel())
        .subscribe(__ -> {
        }, crashReporter::log);
  }

  private Single<EditorialViewModel> loadEditorialViewModel() {
    return editorialManager.loadEditorialViewModel()
        .observeOn(viewScheduler)
        .doOnSuccess(editorialViewModel -> {
          if (!editorialViewModel.isLoading()) {
            view.hideLoading();
          }
          if (editorialViewModel.hasError()) {
            view.showError(editorialViewModel.getError());
          } else {
            view.populateView(editorialViewModel);
          }
        })
        .map(editorialViewModel -> editorialViewModel);
  }

  @VisibleForTesting public void handleRetryClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> view.retryClicked()
            .observeOn(viewScheduler)
            .doOnNext(bottom -> view.showLoading())
            .flatMapSingle(__ -> loadEditorialViewModel()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, crashReporter::log);
  }

  @VisibleForTesting public void handleClickOnMedia() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.mediaContentClicked())
        .doOnNext(editorialEvent -> editorialNavigator.navigateToUri(editorialEvent.getUrl()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, crashReporter::log);
  }

  @VisibleForTesting public void handleClickOnAppCard() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> setUpViewModelOnViewReady())
        .flatMap(view::appCardClicked)
        .doOnNext(editorialEvent -> {
          editorialNavigator.navigateToAppView(editorialEvent.getId(),
              editorialEvent.getPackageName());
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReporter::log);
  }

  @VisibleForTesting public void handleClickActionButtonCard() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.actionButtonClicked())
        .doOnNext(editorialEvent -> editorialNavigator.navigateToUri(editorialEvent.getUrl()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReporter::log);
  }

  private void handleInstallClick() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> setUpViewModelOnViewReady())
        .flatMap(editorialViewModel -> view.installButtonClick(editorialViewModel)
            .flatMapCompletable(editorialDownloadEvent -> {
              Completable completable = null;
              DownloadModel.Action action = editorialDownloadEvent.getAction();
              switch (action) {
                case INSTALL:
                case UPDATE:
                  completable = editorialManager.loadEditorialViewModel()
                      .flatMapCompletable(
                          viewModel -> downloadApp(editorialDownloadEvent).observeOn(viewScheduler)
                              .doOnCompleted(() -> editorialAnalytics.clickOnInstallButton(
                                  editorialDownloadEvent.getPackageName(), action.toString())));
                  break;
                case OPEN:
                  completable = editorialManager.loadEditorialViewModel()
                      .observeOn(viewScheduler)
                      .flatMapCompletable(appViewViewModel -> openInstalledApp(
                          editorialDownloadEvent.getPackageName()));
                  break;
                case DOWNGRADE:
                  completable = editorialManager.loadEditorialViewModel()
                      .observeOn(viewScheduler)
                      .flatMapCompletable(__ -> downgradeApp(editorialDownloadEvent));
                  break;
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

  private void cancelDownload() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(__ -> setUpViewModelOnViewReady())
        .flatMap(editorialViewModel -> view.cancelDownload(editorialViewModel)
            .doOnNext(editorialEvent -> editorialAnalytics.sendDownloadCancelEvent(
                editorialEvent.getPackageName()))
            .flatMapCompletable(
                editorialEvent -> editorialManager.cancelDownload(editorialEvent.getMd5(),
                    editorialEvent.getPackageName(), editorialEvent.getVerCode()))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
        });
  }

  private void resumeDownload() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(__ -> setUpViewModelOnViewReady())
        .flatMap(editorialViewModel -> view.resumeDownload(editorialViewModel)
            .flatMap(editorialEvent -> permissionManager.requestDownloadAccess(permissionService)
                .flatMap(success -> permissionManager.requestExternalStoragePermission(
                    permissionService))
                .flatMapCompletable(__ -> editorialManager.resumeDownload(editorialEvent.getMd5(),
                    editorialEvent.getPackageName(), editorialEvent.getAppId()))
                .retry()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
        });
  }

  private void pauseDownload() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(__ -> setUpViewModelOnViewReady())
        .flatMap(editorialViewModel -> view.pauseDownload(editorialViewModel)
            .doOnNext(editorialEvent -> editorialAnalytics.sendDownloadPauseEvent(
                editorialEvent.getPackageName()))
            .flatMapCompletable(
                editorialEvent -> editorialManager.pauseDownload(editorialEvent.getMd5()))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
        });
  }

  private Completable downloadApp(EditorialDownloadEvent editorialDownloadEvent) {
    return Observable.defer(() -> {
      if (editorialManager.shouldShowRootInstallWarningPopup()) {
        return view.showRootInstallWarningPopup()
            .doOnNext(editorialManager::allowRootInstall)
            .map(__ -> editorialDownloadEvent);
      }
      return Observable.just(editorialDownloadEvent);
    })
        .observeOn(viewScheduler)
        .flatMap(__ -> permissionManager.requestDownloadAccess(permissionService))
        .flatMap(success -> permissionManager.requestExternalStoragePermission(permissionService))
        .observeOn(Schedulers.io())
        .flatMapCompletable(viewModel -> editorialManager.downloadApp(editorialDownloadEvent))
        .toCompletable();
  }

  @VisibleForTesting public void loadDownloadApp() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(created -> view.isViewReady())
        .flatMap(create -> editorialManager.loadEditorialViewModel()
            .toObservable())
        .flatMapIterable(editorialViewModel -> editorialViewModel.getPlaceHolderContent())
        .flatMap(
            editorialContent -> editorialManager.loadDownloadModel(editorialContent.getMd5sum(),
                editorialContent.getPackageName(), editorialContent.getVerCode(), false, null,
                editorialContent.getPosition()))
        .observeOn(viewScheduler)
        .doOnNext(view::showDownloadModel)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new OnErrorNotImplementedException(error);
        });
  }

  @VisibleForTesting public void handlePlaceHolderVisibility() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(created -> view.isViewReady())
        .observeOn(viewScheduler)
        .doOnNext(model -> view.managePlaceHolderVisibity())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new OnErrorNotImplementedException(error);
        });
  }

  private Completable openInstalledApp(String packageName) {
    return Completable.fromAction(() -> view.openApp(packageName));
  }

  @VisibleForTesting public void handlePlaceHolderVisibilityChange() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.placeHolderVisibilityChange())
        .doOnNext(scrollEvent -> {
          if (scrollEvent.getItemShown() && scrollEvent.isScrollDown()) {
            view.removeBottomCardAnimation();
          } else if (!scrollEvent.getItemShown() && !scrollEvent.isScrollDown()) {
            view.addBottomCardAnimation();
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @VisibleForTesting public void handlePaletteColor() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(created -> view.paletteSwatchExtracted())
        .observeOn(viewScheduler)
        .doOnNext(paletteSwatch -> view.applyPaletteSwatch(paletteSwatch))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          view.applyPaletteSwatch(null);
          throw new OnErrorNotImplementedException(error);
        });
  }

  @VisibleForTesting public void handleMediaListDescriptionVisibility() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.mediaListDescriptionChanged())
        .observeOn(viewScheduler)
        .filter(editorialEvent -> editorialEvent.getFirstVisiblePosition() >= 0)
        .doOnNext(editorialEvent -> {
          int firstVisiblePosition = editorialEvent.getFirstVisiblePosition();
          if (isOnlyOneMediaVisible(firstVisiblePosition,
              editorialEvent.getLastVisibleItemPosition())) {
            view.manageMediaListDescriptionAnimationVisibility(editorialEvent);
          } else {
            view.setMediaListDescriptionsVisible(editorialEvent);
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @VisibleForTesting public void handleMovingCollapse() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.handleMovingCollapse())
        .observeOn(viewScheduler)
        .doOnNext(isItemShown -> {
          if (isItemShown) {
            view.removeBottomCardAnimation();
          } else {
            view.addBottomCardAnimation();
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private boolean isOnlyOneMediaVisible(int firstVisiblePosition, int lastVisiblePosition) {
    return firstVisiblePosition == lastVisiblePosition;
  }

  private Completable downgradeApp(EditorialDownloadEvent downloadEvent) {
    return view.showDowngradeMessage()
        .filter(downgrade -> downgrade)
        .flatMapCompletable(__ -> downloadApp(downloadEvent))
        .toCompletable();
  }

  private Observable<EditorialViewModel> setUpViewModelOnViewReady() {
    return view.isViewReady()
        .flatMap(__ -> editorialManager.loadEditorialViewModel()
            .toObservable());
  }
}
