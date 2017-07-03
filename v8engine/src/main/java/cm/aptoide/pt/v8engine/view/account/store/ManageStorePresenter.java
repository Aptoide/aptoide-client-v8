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

public class ManageStorePresenter implements Presenter {

  private final ManageStoreView view;
  private final CrashReport crashReport;
  private final StoreManager storeManager;
  private final Resources resources;
  private final UriToPathResolver uriToPathResolver;
  private final String applicationPackageName;
  private final ManageStoreNavigator navigator;

  public ManageStorePresenter(ManageStoreView view, CrashReport crashReport,
      StoreManager storeManager, Resources resources, UriToPathResolver uriToPathResolver,
      String applicationPackageName, ManageStoreNavigator navigator) {
    this.view = view;
    this.crashReport = crashReport;
    this.storeManager = storeManager;
    this.resources = resources;
    this.uriToPathResolver = uriToPathResolver;
    this.applicationPackageName = applicationPackageName;
    this.navigator = navigator;
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
            .doOnNext(__2 -> navigator.navigate()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleSaveData() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.saveDataClick()
            .flatMap(storeModel -> handleSaveClick(storeModel).toObservable())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private Completable handleSaveClick(ManageStoreFragment.ViewModel storeModel) {
    String mediaStoragePath = "";
    if (storeModel.hasNewAvatar()) {
      mediaStoragePath =
          uriToPathResolver.getMediaStoragePath(Uri.parse(storeModel.getPictureUri()));
    }

    Completable saveDataCompletable =
        storeManager.createOrUpdate(storeModel.getStoreId(), storeModel.getStoreName(),
            storeModel.getStoreDescription(), mediaStoragePath, storeModel.hasNewAvatar(),
            storeModel.getStoreThemeName(), storeModel.storeExists());

    return Completable.fromAction(() -> view.showWaitProgressBar())
        .andThen(saveDataCompletable)
        .doOnCompleted(() -> view.dismissWaitProgressBar())
        .doOnCompleted(() -> navigator.navigate())
        .onErrorResumeNext(err -> Completable.fromAction(() -> view.dismissWaitProgressBar())
            .andThen(handleStoreCreationErrors(err)));
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
    }

    crashReport.log(err);
    return view.showGenericError();
  }
}
