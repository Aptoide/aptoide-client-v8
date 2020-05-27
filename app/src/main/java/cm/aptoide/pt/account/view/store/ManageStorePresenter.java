package cm.aptoide.pt.account.view.store;

import android.net.Uri;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.UriToPathResolver;
import cm.aptoide.pt.account.view.exception.InvalidImageException;
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
  private final UriToPathResolver uriToPathResolver;
  private final String applicationPackageName;
  private final ManageStoreNavigator navigator;
  private final boolean goBackToHome;
  private final ManageStoreErrorMapper errorMapper;
  private final AptoideAccountManager accountManager;
  private final int requestCode;
  private AccountAnalytics accountAnalytics;

  public ManageStorePresenter(ManageStoreView view, CrashReport crashReport,
      UriToPathResolver uriToPathResolver, String applicationPackageName,
      ManageStoreNavigator navigator, boolean goBackToHome, ManageStoreErrorMapper errorMapper,
      AptoideAccountManager accountManager, int requestCode, AccountAnalytics accountAnalytics) {
    this.view = view;
    this.crashReport = crashReport;
    this.uriToPathResolver = uriToPathResolver;
    this.applicationPackageName = applicationPackageName;
    this.navigator = navigator;
    this.goBackToHome = goBackToHome;
    this.errorMapper = errorMapper;
    this.accountManager = accountManager;
    this.requestCode = requestCode;
    this.accountAnalytics = accountAnalytics;
  }

  @Override public void present() {
    handleSaveData();
    handleCancel();
  }

  private void handleCancel() {
    view.getLifecycleEvent()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.cancelClick()
            .doOnNext(storeModel -> {
              if (goBackToHome) {
                accountAnalytics.createStore(storeModel.hasPicture(),
                    AccountAnalytics.CreateStoreAction.SKIP);
              }
              navigate(false);
            }))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleSaveData() {
    view.getLifecycleEvent()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.saveDataClick()
            .flatMapCompletable(this::handleSaveClick)
            .doOnError(crashReport::log)
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private Completable handleSaveClick(ManageStoreViewModel storeModel) {
    return Completable.fromAction(view::showWaitProgressBar)
        .observeOn(Schedulers.io())
        .andThen(saveData(storeModel))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnCompleted(() -> {
          if (goBackToHome) {
            accountAnalytics.createStore(storeModel.hasPicture(),
                AccountAnalytics.CreateStoreAction.CREATE);
          }
        })
        .doOnCompleted(() -> view.dismissWaitProgressBar())
        .doOnCompleted(() -> view.showSuccessMessage())
        .doOnCompleted(() -> navigate(true))
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
            mediaStoragePath -> accountManager.createOrUpdate(storeModel.getStoreName(),
                storeModel.getStoreDescription(), mediaStoragePath, storeModel.hasNewAvatar(),
                storeModel.getStoreTheme()
                    .getThemeName(), storeModel.storeExists()));
  }

  private void navigate(boolean success) {
    if (goBackToHome) {
      navigator.goToHome();
      return;
    }
    navigator.popViewWithResult(requestCode, success);
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

    crashReport.log(err);
    view.showError(errorMapper.getGenericError());
  }
}
