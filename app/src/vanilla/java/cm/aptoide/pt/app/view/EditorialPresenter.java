package cm.aptoide.pt.app.view;

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

    handleInstallClick();
    pauseDownload();
    resumeDownload();
    cancelDownload();
    loadDownloadApp();
    handlePlaceHolderVisibilityChange();
  }

  private void onCreateLoadAppOfTheWeek() {
    view.getLifecycle()
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
            view.readyToDownload();
          }
        })
        .map(editorialViewModel -> editorialViewModel);
  }

  private void handleRetryClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> view.retryClicked()
            .observeOn(viewScheduler)
            .doOnNext(bottom -> view.showLoading())
            .flatMapSingle(__ -> loadEditorialViewModel()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, crashReporter::log);
  }

  private void handleClickOnMedia() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.mediaContentClicked())
        .doOnNext(editorialEvent -> editorialNavigator.navigateToUri(editorialEvent.getUrl()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, crashReporter::log);
  }

  private void handleClickOnAppCard() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.appCardClicked())
        .flatMapSingle(__ -> editorialManager.loadEditorialViewModel())
        .doOnNext(model -> {
          editorialNavigator.navigateToAppView(model.getAppId(), model.getPackageName());
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, crashReporter::log);
  }

  private void handleInstallClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.installButtonClick()
            .flatMapCompletable(action -> {
              Completable completable = null;
              switch (action) {
                case INSTALL:
                case UPDATE:
                  completable = editorialManager.loadEditorialViewModel()
                      .flatMapCompletable(viewModel -> downloadApp(action).observeOn(viewScheduler)
                          .doOnCompleted(() -> editorialAnalytics.clickOnInstallButton(
                              viewModel.getPackageName(), action.toString())));
                  break;
                case OPEN:
                  completable = editorialManager.loadEditorialViewModel()
                      .observeOn(viewScheduler)
                      .flatMapCompletable(
                          appViewViewModel -> openInstalledApp(appViewViewModel.getPackageName()));
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
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(create -> view.cancelDownload()
            .flatMapSingle(__ -> editorialManager.loadEditorialViewModel())
            .doOnNext(app -> editorialAnalytics.sendDownloadCancelEvent(app.getPackageName()))
            .flatMapCompletable(
                app -> editorialManager.cancelDownload(app.getMd5(), app.getPackageName(),
                    app.getVercode()))
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
                .flatMapSingle(__1 -> editorialManager.loadEditorialViewModel())
                .flatMapCompletable(
                    app -> editorialManager.resumeDownload(app.getMd5(), app.getPackageName(),
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
            .flatMapSingle(__ -> editorialManager.loadEditorialViewModel())
            .doOnNext(app -> editorialAnalytics.sendDownloadPauseEvent(app.getPackageName()))
            .flatMapCompletable(app -> editorialManager.pauseDownload(app.getMd5()))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
        });
  }

  private Completable downloadApp(DownloadModel.Action action) {
    return Observable.defer(() -> {
      if (editorialManager.shouldShowRootInstallWarningPopup()) {
        return view.showRootInstallWarningPopup()
            .doOnNext(editorialManager::allowRootInstall)
            .map(__ -> action);
      }
      return Observable.just(action);
    })
        .observeOn(viewScheduler)
        .flatMap(__ -> permissionManager.requestDownloadAccess(permissionService))
        .flatMap(success -> permissionManager.requestExternalStoragePermission(permissionService))
        .observeOn(Schedulers.io())
        .flatMapCompletable(__1 -> editorialManager.loadEditorialViewModel()
            .flatMapCompletable(viewModel -> editorialManager.downloadApp(action, viewModel)))
        .toCompletable();
  }

  private void loadDownloadApp() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(created -> view.isAppViewReadyToDownload())
        .flatMap(create -> editorialManager.loadEditorialViewModel()
            .toObservable())
        .flatMap(app -> editorialManager.loadDownloadModel(app.getMd5(), app.getPackageName(),
            app.getVercode(), false, null))
        .observeOn(viewScheduler)
        .doOnNext(view::showDownloadModel)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new OnErrorNotImplementedException(error);
        });
  }

  private Completable openInstalledApp(String packageName) {
    return Completable.fromAction(() -> view.openApp(packageName));
  }

  private void handlePlaceHolderVisibilityChange() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.placeHolderVisibilityChange()
            .flatMapSingle(scrollEvent -> editorialManager.loadEditorialViewModel()
                .observeOn(viewScheduler)
                .doOnSuccess(editorialViewModel -> {
                  if (scrollEvent.getItemShown() && scrollEvent.isScrollDown()) {
                    view.removeBottomCardAnimation(editorialViewModel);
                  } else if (!scrollEvent.getItemShown() && !scrollEvent.isScrollDown()) {
                    view.addBottomCardAnimation(editorialViewModel);
                  }
                }))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(bundles -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }
}
