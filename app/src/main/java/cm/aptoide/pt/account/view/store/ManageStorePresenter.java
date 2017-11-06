package cm.aptoide.pt.account.view.store;

import android.content.res.Resources;
import android.net.Uri;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.ErrorsMapper;
import cm.aptoide.pt.account.view.UriToPathResolver;
import cm.aptoide.pt.account.view.exception.InvalidImageException;
import cm.aptoide.pt.account.view.exception.SocialLinkException;
import cm.aptoide.pt.account.view.exception.StoreCreationException;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Completable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ManageStorePresenter implements Presenter {

  private final ManageStoreView view;
  private final CrashReport crashReport;
  private final StoreManager storeManager;
  private final Resources resources;
  private final UriToPathResolver uriToPathResolver;
  private final String applicationPackageName;
  private final ManageStoreNavigator navigator;
  private final boolean goBackToHome;

  public ManageStorePresenter(ManageStoreView view, CrashReport crashReport,
      StoreManager storeManager, Resources resources, UriToPathResolver uriToPathResolver,
      String applicationPackageName, ManageStoreNavigator navigator, boolean goBackToHome) {
    this.view = view;
    this.crashReport = crashReport;
    this.storeManager = storeManager;
    this.resources = resources;
    this.uriToPathResolver = uriToPathResolver;
    this.applicationPackageName = applicationPackageName;
    this.navigator = navigator;
    this.goBackToHome = goBackToHome;
  }

  @Override public void present() {
    handleSaveData();
    handleCancel();
    handleFacebookClick();
    handleTwitchClick();
    handleTwitterClick();
    handleYoutubeClick();
    handleFacebookEditTextFocus();
    handleTwitchEditTextFocus();
    handleTwitterEditTextFocus();
    handleYoutubeEditTextFocus();
  }

  private void handleYoutubeEditTextFocus() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.youtubeUserFocusChanged()
            .doOnNext(focusChanged -> view.changeYoutubeUI()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleTwitterEditTextFocus() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.twitterUserFocusChanged()
            .doOnNext(focusChanged -> view.changeTwitterUI()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleTwitchEditTextFocus() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.twitchUserFocusChanged()
            .doOnNext(focusChanged -> view.changeTwitchUI()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleFacebookEditTextFocus() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.facebookUserFocusChanged()
            .doOnNext(focusChanged -> view.changeFacebookUI()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleYoutubeClick() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.youtubeClick()
            .doOnNext(click -> view.manageYoutubeViews()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleTwitterClick() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.twitterClick()
            .doOnNext(click -> view.manageTwitterViews()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleTwitchClick() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.twitchClick()
            .doOnNext(click -> view.manageTwitchViews()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleFacebookClick() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.facebookClick()
            .doOnNext(click -> view.manageFacebookViews()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleCancel() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.cancelClick()
            .doOnNext(__2 -> {
              view.hideKeyboard();
              navigate();
            }))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleSaveData() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.saveDataClick()
            .flatMapCompletable(storeModel -> handleSaveClick(storeModel))
            .doOnError(err -> crashReport.log(err))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private Completable handleSaveClick(ManageStoreViewModel storeModel) {
    return Completable.fromAction(() -> {
      view.hideKeyboard();
      view.showWaitProgressBar();
    })
        .observeOn(Schedulers.io())
        .andThen(saveData(storeModel))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnCompleted(() -> view.dismissWaitProgressBar())
        .doOnCompleted(() -> navigate())
        .onErrorResumeNext(err -> Completable.fromAction(() -> view.dismissWaitProgressBar())
            .andThen(handleStoreCreationErrors(err)));
  }

  private Completable saveData(ManageStoreViewModel storeModel) {
    return Single.fromCallable(() -> {
      if (storeModel.hasNewAvatar()) {
        return uriToPathResolver.getMediaStoragePath(Uri.parse(storeModel.getPictureUri()));
      }
      return "";
    })
        .flatMapCompletable(
            mediaStoragePath -> storeManager.createOrUpdate(storeModel.getStoreName(),
                storeModel.getStoreDescription(), mediaStoragePath, storeModel.hasNewAvatar(),
                storeModel.getStoreTheme()
                    .getThemeName(), storeModel.storeExists(), storeModel.getStoreLinks()));
  }

  private void navigate() {
    if (goBackToHome) {
      navigator.goToHome();
      return;
    }
    navigator.goBack();
  }

  private Completable handleStoreCreationErrors(Throwable err) {
    if (err instanceof InvalidImageException) {
      InvalidImageException networkError = ((InvalidImageException) err);
      if (networkError.getImageErrors()
          .contains(InvalidImageException.ImageError.API_ERROR)) {
        return view.showError(R.string.ws_error_API_1);
      } else {
        return view.showError(
            ErrorsMapper.getWebServiceErrorMessageFromCode(networkError.getErrorCode(),
                applicationPackageName, resources));
      }
    } else if (err instanceof StoreCreationException) {
      StoreCreationException exception = ((StoreCreationException) err);
      if (exception.hasErrorCode()) {
        return view.showError(
            ErrorsMapper.getWebServiceErrorMessageFromCode(exception.getErrorCode(),
                applicationPackageName, resources));
      } else {
        return view.showError(R.string.ws_error_WOP_2);
      }
    } else if (err instanceof StoreValidationException) {
      StoreValidationException ex = (StoreValidationException) err;
      if (ex.getErrorCode() == StoreValidationException.EMPTY_NAME) {
        return view.showError(R.string.ws_error_WOP_2);
      }
      if (ex.getErrorCode() == StoreValidationException.EMPTY_AVATAR) {
        return view.showError(R.string.ws_error_API_1);
      }
    } else if (err instanceof SocialLinkException) {
      view.setViewLinkErrors(((SocialLinkException) err).getStoreLinks());
    }

    crashReport.log(err);
    return view.showGenericError();
  }
}
