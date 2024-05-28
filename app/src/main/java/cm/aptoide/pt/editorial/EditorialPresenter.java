package cm.aptoide.pt.editorial;

import androidx.annotation.VisibleForTesting;
import cm.aptoide.pt.UserFeedbackAnalytics;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.ads.MoPubAdsManager;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.reactions.network.LoadReactionModel;
import cm.aptoide.pt.reactions.network.ReactionsResponse;
import cm.aptoide.pt.socialmedia.SocialMediaAnalytics;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.Single;
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
  private final UserFeedbackAnalytics userFeedbackAnalytics;
  private final MoPubAdsManager moPubAdsManager;
  private final SocialMediaAnalytics socialMediaAnalytics;

  public EditorialPresenter(EditorialView view, EditorialManager editorialManager,
      Scheduler viewScheduler, CrashReport crashReporter, PermissionManager permissionManager,
      PermissionService permissionService, EditorialAnalytics editorialAnalytics,
      EditorialNavigator editorialNavigator, UserFeedbackAnalytics userFeedbackAnalytics,
      MoPubAdsManager moPubAdsManager, SocialMediaAnalytics socialMediaAnalytics) {
    this.view = view;
    this.editorialManager = editorialManager;
    this.viewScheduler = viewScheduler;
    this.crashReporter = crashReporter;
    this.permissionManager = permissionManager;
    this.permissionService = permissionService;
    this.editorialAnalytics = editorialAnalytics;
    this.editorialNavigator = editorialNavigator;
    this.userFeedbackAnalytics = userFeedbackAnalytics;
    this.moPubAdsManager = moPubAdsManager;
    this.socialMediaAnalytics = socialMediaAnalytics;
  }

  @Override public void present() {
    onCreateLoadAppOfTheWeek();
    handleRetryClick();
    handleClickOnMedia();
    handleClickOnAppCard();

    handleInstallClick();
    observeDownloadErrors();
    pauseDownload();
    resumeDownload();
    cancelDownload();
    loadDownloadApp();

    handlePlaceHolderVisibilityChange();
    handlePlaceHolderVisibility();
    handleMediaListDescriptionVisibility();
    handleClickActionButtonCard();
    handleMovingCollapse();

    handleReactionButtonClick();
    handleUserReaction();
    handleLongPressReactionButton();
    handleSnackLogInClick();
    onCreateLoadReactionModel();
    handleSocialMediaPromotionClick();
    handleOutOfSpaceDialogResult();
  }

  private void handleOutOfSpaceDialogResult() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> editorialNavigator.outOfSpaceDialogResult())
        .filter(result -> result.getClearedSuccessfully())
        .flatMapSingle(__ -> editorialManager.loadEditorialViewModel())
        .flatMapCompletable(editorialViewModel -> editorialManager.resumeDownload(
            editorialViewModel.getBottomCardMd5(), editorialViewModel.getBottomCardPackageName(),
            editorialViewModel.getBottomCardAppId(), view.getAction(),
            editorialViewModel.getBdsFlags()))
        .retry()
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReporter.log(throwable));
  }

  private void handleSocialMediaPromotionClick() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.socialMediaClick())
        .doOnNext(socialMediaType -> {
          editorialNavigator.navigateToSocialMedia(socialMediaType);
          socialMediaAnalytics.sendPromoteSocialMediaClickEvent(socialMediaType);
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReporter::log);
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
                                  editorialDownloadEvent.getPackageName(), action.toString(),
                                  viewModel.hasSplits(), viewModel.hasAppc(), false,
                                  viewModel.getRank(), null, viewModel.getStoreName(),
                                  viewModel.getBottomCardObb() != null,
                                  editorialDownloadEvent.getBdsFlags())));
                  break;
                case OPEN:
                  completable = editorialManager.loadEditorialViewModel()
                      .observeOn(viewScheduler)
                      .flatMapCompletable(appViewViewModel -> openInstalledApp(
                          editorialDownloadEvent.getPackageName()).doOnCompleted(
                          () -> editorialAnalytics.clickOnInstallButton(
                              editorialDownloadEvent.getPackageName(), action.toString(),
                              appViewViewModel.hasSplits(), appViewViewModel.hasAppc(), false,
                              appViewViewModel.getRank(), null, appViewViewModel.getStoreName(),
                              appViewViewModel.getBottomCardObb() != null,
                              editorialDownloadEvent.getBdsFlags())));
                  break;
                case DOWNGRADE:
                  completable = editorialManager.loadEditorialViewModel()
                      .observeOn(viewScheduler)
                      .flatMapCompletable(
                          appViewViewModel -> downgradeApp(editorialDownloadEvent).doOnCompleted(
                              () -> editorialAnalytics.clickOnInstallButton(
                                  editorialDownloadEvent.getPackageName(), action.toString(),
                                  appViewViewModel.hasSplits(), appViewViewModel.hasAppc(), false,
                                  appViewViewModel.getRank(), null, appViewViewModel.getStoreName(),
                                  appViewViewModel.getBottomCardObb() != null,
                                  editorialDownloadEvent.getBdsFlags())));
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
                    editorialEvent.getPackageName(), editorialEvent.getAppId(),
                    editorialEvent.getAction()
                        .toString(), editorialEvent.getBdsFlags()))
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
                    editorialContent.getPackageName(), editorialContent.getVerCode(),
                    editorialContent.getPosition())
                .observeOn(viewScheduler)
                .doOnNext(view::showDownloadModel))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, crashReporter::log);
  }

  public void observeDownloadErrors() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(created -> setUpViewModelOnViewReady())
        .flatMap(editorialViewModel -> Observable.merge(view.installButtonClick(editorialViewModel),
                view.resumeDownload(editorialViewModel))
            .map(__ -> editorialViewModel))
        .flatMapIterable(EditorialViewModel::getPlaceHolderContent)
        .flatMap(
            editorialContent -> editorialManager.loadDownloadModel(editorialContent.getMd5sum(),
                    editorialContent.getPackageName(), editorialContent.getVerCode(),
                    editorialContent.getPosition())
                .filter(DownloadModel::hasError)
                .first()
                .flatMap(editorialDownloadModel -> verifyNotEnoughSpaceError(editorialContent,
                    editorialDownloadModel))
                .observeOn(viewScheduler)
                .doOnNext(editorialDownloadModel -> {
                  if (editorialDownloadModel.getDownloadState()
                      .equals(DownloadModel.DownloadState.NOT_ENOUGH_STORAGE_ERROR)) {
                    editorialNavigator.navigateToOutOfSpaceDialog(editorialContent.getSize(),
                        editorialContent.getPackageName());
                  } else {
                    view.showDownloadError(editorialDownloadModel);
                  }
                }))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, crashReporter::log);
  }

  private Observable<EditorialDownloadModel> verifyNotEnoughSpaceError(
      EditorialContent editorialContent, EditorialDownloadModel downloadModel) {
    if (downloadModel.getDownloadState() == DownloadModel.DownloadState.NOT_ENOUGH_STORAGE_ERROR) {
      return moPubAdsManager.getAdsVisibilityStatus()
          .doOnSuccess(offerResponseStatus -> editorialAnalytics.sendNotEnoughSpaceErrorEvent(
              editorialContent.getMd5sum()))
          .toObservable()
          .map(__ -> downloadModel);
    }
    return Observable.just(downloadModel);
  }

  @VisibleForTesting public void handlePlaceHolderVisibility() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(created -> view.isViewReady())
        .observeOn(viewScheduler)
        .doOnNext(model -> view.managePlaceHolderVisibity())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReporter.log(error));
  }

  private Completable openInstalledApp(String packageName) {
    return Completable.fromAction(() -> view.openApp(packageName));
  }

  @VisibleForTesting public void handlePlaceHolderVisibilityChange() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.placeHolderVisibilityChange())
        .doOnNext(scrollEvent -> {
          if (scrollEvent.getItemShown()) {
            view.removeBottomCardAnimation();
          } else if (!scrollEvent.getItemShown()) {
            view.addBottomCardAnimation();
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReporter.log(throwable));
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
        }, crashReporter::log);
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
        }, throwable -> crashReporter.log(throwable));
  }

  private Observable<LoadReactionModel> handleSinglePressReactionButton(
      EditorialViewModel editorialViewModel) {
    return editorialManager.isFirstReaction(editorialViewModel.getCardId(),
            editorialViewModel.getGroupId())
        .flatMapObservable(firstReaction -> {
          if (firstReaction) {
            editorialAnalytics.sendReactionButtonClickEvent();
            view.showReactionsPopup(editorialViewModel.getCardId(),
                editorialViewModel.getGroupId());
            return Observable.just(new LoadReactionModel());
          } else {
            return editorialManager.deleteReaction(editorialViewModel.getCardId(),
                    editorialViewModel.getGroupId())
                .toObservable()
                .doOnNext(reactionsResponse -> handleReactionsResponse(reactionsResponse, true))
                .filter(ReactionsResponse::wasSuccess)
                .flatMapSingle(__ -> loadReactionModel(editorialViewModel.getCardId(),
                    editorialViewModel.getGroupId()));
          }
        });
  }

  private Single<LoadReactionModel> loadReactionModel(String cardId, String groupId) {
    return editorialManager.loadReactionModel(cardId, groupId)
        .observeOn(viewScheduler)
        .doOnSuccess(reactionModel -> view.showTopReactions(reactionModel.getMyReaction(),
            reactionModel.getTopReactionList(), reactionModel.getTotal()));
  }

  private void handleReactionsResponse(ReactionsResponse reactionsResponse, boolean isDelete) {
    if (reactionsResponse.wasSuccess()) {
      if (isDelete) {
        editorialAnalytics.sendDeletedEvent();
      } else {
        editorialAnalytics.sendReactedEvent();
      }
    } else if (reactionsResponse.reactionsExceeded()) {
      view.showLoginDialog();
    } else if (reactionsResponse.wasNetworkError()) {
      view.showNetworkErrorToast();
    } else if (reactionsResponse.wasGeneralError()) {
      view.showGenericErrorToast();
    }
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
            .toObservable())
        .observeOn(viewScheduler);
  }

  @VisibleForTesting public void handleReactionButtonClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.reactionsButtonClicked())
        .flatMapSingle(click -> editorialManager.loadEditorialViewModel())
        .observeOn(viewScheduler)
        .flatMap(editorialViewModel -> handleSinglePressReactionButton(editorialViewModel))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(lifecycleEvent -> {
        }, throwable -> crashReporter.log(throwable));
  }

  @VisibleForTesting public void handleUserReaction() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.reactionClicked())
        .doOnNext(__ -> userFeedbackAnalytics.sendReactionEvent())
        .flatMap(reactionEvent -> editorialManager.setReaction(reactionEvent.getCardId(),
                reactionEvent.getGroupId(), reactionEvent.getReactionType())
            .toObservable()
            .filter(ReactionsResponse::differentReaction)
            .observeOn(viewScheduler)
            .doOnNext(reactionsResponse -> handleReactionsResponse(reactionsResponse, false))
            .filter(ReactionsResponse::wasSuccess)
            .flatMapSingle(
                __ -> loadReactionModel(reactionEvent.getCardId(), reactionEvent.getGroupId())))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(lifecycleEvent -> {
        }, crashReporter::log);
  }

  @VisibleForTesting public void handleLongPressReactionButton() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.reactionsButtonLongPressed())
        .flatMap(click -> editorialManager.loadEditorialViewModel()
            .toObservable())
        .doOnNext(editorialViewModel -> {
          editorialAnalytics.sendReactionButtonClickEvent();
          view.showReactionsPopup(editorialViewModel.getCardId(), editorialViewModel.getGroupId());
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(lifecycleEvent -> {
        }, crashReporter::log);
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

  @VisibleForTesting public void onCreateLoadReactionModel() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> setUpViewModelOnViewReady())
        .flatMapSingle(editorialViewModel -> loadReactionModel(editorialViewModel.getCardId(),
            editorialViewModel.getGroupId()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(lifecycleEvent -> {
        }, crashReporter::log);
  }
}
