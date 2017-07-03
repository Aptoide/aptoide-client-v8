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
import cm.aptoide.pt.v8engine.view.account.store.exception.InvalidImageException;
import cm.aptoide.pt.v8engine.view.account.store.exception.StoreCreationException;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import rx.Completable;

public class ManageStorePresenter implements Presenter {

  private final ManageStoreView view;
  private final CrashReport crashReport;
  private final boolean goBackToHome;
  private final StoreManager storeManager;
  private final FragmentNavigator fragmentNavigator;
  private final Resources resources;
  private final UriToPathResolver uriToPathResolver;
  private final String applicationPackageName;

  public ManageStorePresenter(ManageStoreView view, CrashReport crashReport, boolean goBackToHome,
      StoreManager storeManager, FragmentNavigator fragmentNavigator, Resources resources,
      UriToPathResolver uriToPathResolver, String applicationPackageName) {
    this.view = view;
    this.crashReport = crashReport;
    this.goBackToHome = goBackToHome;
    this.storeManager = storeManager;
    this.fragmentNavigator = fragmentNavigator;
    this.resources = resources;
    this.uriToPathResolver = uriToPathResolver;
    this.applicationPackageName = applicationPackageName;
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
              if (goBackToHome) {
                navigateHome();
                return;
              }
              navigateBack();
            }))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleSaveData() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.saveDataClick()
            .retry()
            .flatMap(storeModel -> handleSaveClick(storeModel).toObservable()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private Completable handleSaveClick(ManageStoreFragment.ViewModel storeModel) {
    final String mediaStoragePath =
        uriToPathResolver.getMediaStoragePath(Uri.parse(storeModel.getPictureUri()));

    Completable saveDataCompletable =
        storeManager.createOrUpdate(storeModel.getStoreId(), storeModel.getStoreName(),
            storeModel.getStoreDescription(), mediaStoragePath, storeModel.hasNewAvatar(),
            storeModel.getStoreThemeName(), storeModel.storeExists())
            .onErrorResumeNext(err -> Completable.fromAction(() -> view.dismissWaitProgressBar())
                .andThen(handleStoreCreationErrors(err)));

    return Completable.fromAction(() -> view.showWaitProgressBar())
        .andThen(saveDataCompletable)
        .doOnCompleted(() -> view.dismissWaitProgressBar())
        .doOnCompleted(() -> {
          if (goBackToHome) {
            navigateHome();
          } else {
            navigateBack();
          }
        });
  }

  private void navigateHome() {
    fragmentNavigator.navigateToHomeCleaningBackStack();
  }

  private void navigateBack() {
    fragmentNavigator.popBackStack();
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
