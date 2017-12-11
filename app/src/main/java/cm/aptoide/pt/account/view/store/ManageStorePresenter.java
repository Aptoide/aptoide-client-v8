package cm.aptoide.pt.account.view.store;

import android.net.Uri;
import cm.aptoide.pt.account.view.UriToPathResolver;
import cm.aptoide.pt.account.view.exception.InvalidImageException;
import cm.aptoide.pt.account.view.exception.SocialLinkException;
import cm.aptoide.pt.account.view.exception.StoreCreationException;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Completable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static cm.aptoide.pt.account.view.store.StoreValidationException.FACEBOOK_1;
import static cm.aptoide.pt.account.view.store.StoreValidationException.FACEBOOK_2;
import static cm.aptoide.pt.account.view.store.StoreValidationException.TWITCH_1;
import static cm.aptoide.pt.account.view.store.StoreValidationException.TWITCH_2;
import static cm.aptoide.pt.account.view.store.StoreValidationException.TWITTER_1;
import static cm.aptoide.pt.account.view.store.StoreValidationException.TWITTER_2;
import static cm.aptoide.pt.account.view.store.StoreValidationException.YOUTUBE_1;
import static cm.aptoide.pt.account.view.store.StoreValidationException.YOUTUBE_2;

public class ManageStorePresenter implements Presenter {

  private final ManageStoreView view;
  private final CrashReport crashReport;
  private final StoreManager storeManager;
  private final UriToPathResolver uriToPathResolver;
  private final String applicationPackageName;
  private final ManageStoreNavigator navigator;
  private final boolean goBackToHome;
  private final ManageStoreErrorMapper errorMapper;

  public ManageStorePresenter(ManageStoreView view, CrashReport crashReport,
      StoreManager storeManager, UriToPathResolver uriToPathResolver, String applicationPackageName,
      ManageStoreNavigator navigator, boolean goBackToHome, ManageStoreErrorMapper errorMapper) {
    this.view = view;
    this.crashReport = crashReport;
    this.storeManager = storeManager;
    this.uriToPathResolver = uriToPathResolver;
    this.applicationPackageName = applicationPackageName;
    this.navigator = navigator;
    this.goBackToHome = goBackToHome;
    this.errorMapper = errorMapper;
  }

  @Override public void present() {
    handleSaveData();
    handleCancel();
  }

  private void handleCancel() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.cancelClick()
            .doOnNext(__2 -> {
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
    return Completable.fromAction(() -> view.showWaitProgressBar())
        .observeOn(Schedulers.io())
        .andThen(saveData(storeModel))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnCompleted(() -> view.dismissWaitProgressBar())
        .doOnCompleted(() -> view.showSuccessMessage())
        .doOnCompleted(() -> navigate())
        .onErrorResumeNext(err -> Completable.fromAction(() -> {
          view.dismissWaitProgressBar();
          handleStoreCreationErrors(err);
        }));
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
                    .getThemeName(), storeModel.storeExists(), storeModel.getSocialLinks(),
                storeModel.getSocialDeleteLinks()));
  }

  private void navigate() {
    if (goBackToHome) {
      navigator.goToHome();
      return;
    }
    navigator.goBack();
  }

  private void handleStoreCreationErrors(Throwable err) {
    if (err instanceof InvalidImageException) {
      InvalidImageException exception = ((InvalidImageException) err);
      if (exception.getImageErrors()
          .contains(InvalidImageException.ImageError.API_ERROR)) {
        view.showError(errorMapper.getImageError());
        return;
      }
      view.showError(errorMapper.getNetworkError(exception.getErrorCode(), applicationPackageName));
      return;
    }

    if (err instanceof StoreCreationException) {
      StoreCreationException exception = ((StoreCreationException) err);
      if (exception.hasErrorCode()) {
        view.showError(
            errorMapper.getNetworkError(exception.getErrorCode(), applicationPackageName));
        return;
      }

      view.showError(errorMapper.getInvalidStoreError());
      return;
    }

    if (err instanceof StoreValidationException) {
      StoreValidationException ex = (StoreValidationException) err;
      if (ex.getErrorCode() == StoreValidationException.EMPTY_NAME) {
        view.showError(errorMapper.getInvalidStoreError());
        return;
      }
      if (ex.getErrorCode() == StoreValidationException.EMPTY_AVATAR) {
        view.showError(errorMapper.getImageError());
        return;
      }
    }

    if (err instanceof SocialLinkException) {
      for (BaseV7Response.StoreLinks storeLink : ((SocialLinkException) err).getStoreLinks()) {
        final String error = errorMapper.getError(getErrorMessage(storeLink.getType()
            .toString()));

        switch (storeLink.getType()) {
          case FACEBOOK_1:
          case FACEBOOK_2:
            view.showFacebookError(error);
            break;
          case TWITTER_1:
          case TWITTER_2:
            view.showTwitterError(error);
            break;
          case TWITCH_1:
          case TWITCH_2:
            view.showTwitchError(error);
            break;
          case YOUTUBE_1:
          case YOUTUBE_2:
            view.showYoutubeError(error);
            break;
        }
      }
      return;
    }

    crashReport.log(err);
    view.showError(errorMapper.getGenericError());
  }

  private ManageStoreErrorMapper.SocialErrorType getErrorMessage(String type) {
    switch (type) {
      case TWITCH_1:
      case FACEBOOK_1:
      case TWITTER_1:
      case YOUTUBE_1:
        return ManageStoreErrorMapper.SocialErrorType.INVALID_URL_TEXT;
      case TWITCH_2:
      case YOUTUBE_2:
        return ManageStoreErrorMapper.SocialErrorType.LINK_CHANNEL_ERROR;
      case FACEBOOK_2:
      case TWITTER_2:
        return ManageStoreErrorMapper.SocialErrorType.PAGE_DOES_NOT_EXIST;
    }
    return ManageStoreErrorMapper.SocialErrorType.GENERIC_ERROR;
  }
}
