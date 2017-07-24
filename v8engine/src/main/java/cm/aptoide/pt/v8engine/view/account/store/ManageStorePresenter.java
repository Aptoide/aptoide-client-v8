package cm.aptoide.pt.v8engine.view.account.store;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.account.ErrorsMapper;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import cm.aptoide.pt.v8engine.view.account.UriToPathResolver;
import cm.aptoide.pt.v8engine.view.account.exception.InvalidImageException;
import cm.aptoide.pt.v8engine.view.account.exception.StoreCreationException;
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
  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
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

  private Completable handleSaveClick(ManageStoreFragment.ViewModel storeModel) {
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

  private Completable saveData(ManageStoreFragment.ViewModel storeModel) {
    return Single.fromCallable(() -> {
      if (storeModel.hasNewAvatar()) {
        return uriToPathResolver.getMediaStoragePath(Uri.parse(storeModel.getPictureUri()));
      }
      return "";
    })
        .flatMapCompletable(mediaStoragePath -> storeManager.createOrUpdate(storeModel.getStoreId(),
            storeModel.getStoreName(), storeModel.getStoreDescription(), mediaStoragePath,
            storeModel.hasNewAvatar(), storeModel.getStoreTheme()
                .getThemeName(), storeModel.storeExists()));
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
    }

    crashReport.log(err);
    return view.showGenericError();
  }
}
