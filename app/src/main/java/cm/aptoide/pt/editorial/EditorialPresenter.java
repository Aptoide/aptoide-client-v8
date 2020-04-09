package cm.aptoide.pt.editorial;

import androidx.annotation.VisibleForTesting;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import java.util.ArrayList;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;
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
    firstLoad();
    handleRetryClick();
    handleClickOnMedia();

    handleBottomCardVisibilityChange();
    handleClickActionButtonCard();

    handleSnackLogInClick();
  }

  @VisibleForTesting public void firstLoad() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .doOnNext(created -> view.showLoading())
        .flatMap(__ -> loadEditorial())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReporter::log);
  }

  @VisibleForTesting public Observable<EditorialViewModel> loadEditorial() {
    return editorialManager.loadEditorialViewModel()
        .toObservable()
        .observeOn(viewScheduler)
        .doOnNext(this::populateView)
        .filter(editorialViewModel -> !editorialViewModel.hasError())
        .flatMap(
            editorialViewModel -> Observable.mergeDelayError(observeAppsState(editorialViewModel),
                handleClickOnAppCard(editorialViewModel), handleInstallClick(editorialViewModel),
                pauseDownload(editorialViewModel), resumeDownload(editorialViewModel),
                cancelDownload(editorialViewModel))
                .map(__ -> editorialViewModel));
  }

  public Observable<EditorialViewModel> observeAppsState(EditorialViewModel editorialViewModel) {
    List<EditorialContent> appContent = editorialViewModel.getPlaceHolderContent();
    if (appContent != null && appContent.size() > 0) {
      return loadDownloadModels(editorialViewModel).observeOn(viewScheduler)
          .doOnNext(view::populateCardContent)
          .doOnError(e -> e.printStackTrace());
    }
    return Observable.just(editorialViewModel)
        .observeOn(viewScheduler)
        .doOnNext(view::populateCardContent);
  }

  private Observable<EditorialViewModel> loadDownloadModels(EditorialViewModel editorialViewModel) {
    ArrayList<Observable<EditorialDownloadModel>> downloadModels = new ArrayList<>();
    for (int i = 0; i < editorialViewModel.getPlaceHolderContent()
        .size(); i++) {
      EditorialAppModel app = editorialViewModel.getPlaceHolderContent()
          .get(i)
          .getApp();
      downloadModels.add(editorialManager.loadDownloadModel(app.getMd5sum(), app.getPackageName(),
          app.getVerCode(), editorialViewModel.getPlaceHolderContent()
              .get(i)
              .getPosition()));
    }
    return Observable.combineLatest(downloadModels, args -> {
      // Deep copy the model with the update download models
      ArrayList<EditorialContent> content = new ArrayList<>(editorialViewModel.getContentList());
      ArrayList<EditorialContent> placeHolderContent = new ArrayList<>();
      for (int i = 0; i < args.length; i++) {
        EditorialDownloadModel model = (EditorialDownloadModel) args[i];
        placeHolderContent.add(
            new EditorialContent(editorialViewModel.getContent(model.getPosition()),
                new EditorialDownloadModel(model)));
        content.set(model.getPosition(),
            new EditorialContent(editorialViewModel.getContent(model.getPosition()),
                new EditorialDownloadModel(model)));
      }
      return deepCopyEditorial(editorialViewModel, content, placeHolderContent);
    });
  }

  private EditorialViewModel deepCopyEditorial(EditorialViewModel editorialViewModel,
      List<EditorialContent> content, List<EditorialContent> placeholderContent) {
    EditorialAppModel bottomAppModel = null;
    if (placeholderContent != null && placeholderContent.size() > 0) {
      bottomAppModel = placeholderContent.get(0)
          .getApp();
    }
    return new EditorialViewModel(content, editorialViewModel.getTitle(),
        editorialViewModel.getCaption(), editorialViewModel.getBackgroundImage(),
        editorialViewModel.getPlaceHolderPositions(), placeholderContent,
        editorialViewModel.shouldHaveAnimation(), editorialViewModel.getCardId(),
        editorialViewModel.getGroupId(), editorialViewModel.getCaptionColor(), bottomAppModel);
  }

  private void populateView(EditorialViewModel editorialViewModel) {
    if (!editorialViewModel.isLoading()) {
      view.hideLoading();
    }
    if (editorialViewModel.hasError()) {
      view.showError(editorialViewModel.getError());
    } else {
      view.populateView(editorialViewModel);
    }
  }

  @VisibleForTesting public void handleRetryClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> view.retryClicked()
            .observeOn(viewScheduler)
            .doOnNext(bottom -> view.showLoading())
            .flatMap(__ -> loadEditorial()))
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

  @VisibleForTesting
  public Observable<EditorialEvent> handleClickOnAppCard(EditorialViewModel editorialViewModel) {
    return view.appCardClicked(editorialViewModel)
        .doOnNext(editorialEvent -> {
          editorialNavigator.navigateToAppView(editorialEvent.getId(),
              editorialEvent.getPackageName());
        });
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

  private Observable<EditorialDownloadEvent> handleInstallClick(
      EditorialViewModel editorialViewModel) {
    return view.installButtonClick(editorialViewModel)
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
                              editorialDownloadEvent.getPackageName(), action.toString(),
                              viewModel.getBottomCardAppModel()
                                  .hasSplits(), viewModel.getBottomCardAppModel()
                                  .hasAppc(), false, viewModel.getBottomCardAppModel()
                                  .getRank(), null, viewModel.getBottomCardAppModel()
                                  .getStoreName())));
              break;
            case OPEN:
              completable = editorialManager.loadEditorialViewModel()
                  .observeOn(viewScheduler)
                  .flatMapCompletable(appViewViewModel -> openInstalledApp(
                      editorialDownloadEvent.getPackageName()).doOnCompleted(
                      () -> editorialAnalytics.clickOnInstallButton(
                          editorialDownloadEvent.getPackageName(), action.toString(),
                          appViewViewModel.getBottomCardAppModel()
                              .hasSplits(), appViewViewModel.getBottomCardAppModel()
                              .hasAppc(), false, appViewViewModel.getBottomCardAppModel()
                              .getRank(), null, appViewViewModel.getBottomCardAppModel()
                              .getStoreName())));
              break;
            case DOWNGRADE:
              completable = editorialManager.loadEditorialViewModel()
                  .observeOn(viewScheduler)
                  .flatMapCompletable(
                      appViewViewModel -> downgradeApp(editorialDownloadEvent).doOnCompleted(
                          () -> editorialAnalytics.clickOnInstallButton(
                              editorialDownloadEvent.getPackageName(), action.toString(),
                              appViewViewModel.getBottomCardAppModel()
                                  .hasSplits(), appViewViewModel.getBottomCardAppModel()
                                  .hasAppc(), false, appViewViewModel.getBottomCardAppModel()
                                  .getRank(), null, appViewViewModel.getBottomCardAppModel()
                                  .getStoreName())));
              break;
          }
          return completable;
        })
        .doOnError(Throwable::printStackTrace)
        .retry();
  }

  private Observable<EditorialDownloadEvent> cancelDownload(EditorialViewModel editorialViewModel) {
    return view.cancelDownload(editorialViewModel)
        .doOnNext(editorialEvent -> editorialAnalytics.sendDownloadCancelEvent(
            editorialEvent.getPackageName()))
        .flatMapCompletable(
            editorialEvent -> editorialManager.cancelDownload(editorialEvent.getMd5(),
                editorialEvent.getPackageName(), editorialEvent.getVerCode()))
        .retry();
  }

  private Observable<Void> resumeDownload(EditorialViewModel editorialViewModel) {
    return view.resumeDownload(editorialViewModel)
        .flatMap(editorialEvent -> permissionManager.requestDownloadAccess(permissionService)
            .flatMap(
                success -> permissionManager.requestExternalStoragePermission(permissionService))
            .flatMapCompletable(__ -> editorialManager.resumeDownload(editorialEvent.getMd5(),
                editorialEvent.getPackageName(), editorialEvent.getAppId(),
                editorialEvent.getAction()
                    .toString()))
            .retry());
  }

  private Observable<EditorialDownloadEvent> pauseDownload(EditorialViewModel editorialViewModel) {
    return view.pauseDownload(editorialViewModel)
        .doOnNext(editorialEvent -> editorialAnalytics.sendDownloadPauseEvent(
            editorialEvent.getPackageName()))
        .flatMapCompletable(
            editorialEvent -> editorialManager.pauseDownload(editorialEvent.getMd5()))
        .retry();
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

  private Completable openInstalledApp(String packageName) {
    return Completable.fromAction(() -> view.openApp(packageName));
  }

  @VisibleForTesting public void handleBottomCardVisibilityChange() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.bottomCardVisibilityChange())
        .distinctUntilChanged()
        .doOnNext(shouldSetToVisible -> {
          if (shouldSetToVisible) {
            view.addBottomCardAnimation();
          } else {
            view.removeBottomCardAnimation();
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReporter::log);
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

  @VisibleForTesting public void handleSnackLogInClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.snackLoginClick())
        .doOnNext(homeEvent -> editorialNavigator.navigateToLogIn())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(lifecycleEvent -> {
        }, crashReporter::log);
  }
}
