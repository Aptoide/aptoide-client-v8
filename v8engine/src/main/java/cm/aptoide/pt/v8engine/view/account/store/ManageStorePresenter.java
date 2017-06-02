package cm.aptoide.pt.v8engine.view.account.store;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.account.ErrorsMapper;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import rx.Completable;
import rx.Observable;

public class ManageStorePresenter implements Presenter {

  private final ManageStoreView view;
  private final CrashReport crashReport;
  private final boolean goBackToHome;
  private final StoreManager storeManager;

  public ManageStorePresenter(ManageStoreView view, CrashReport crashReport, boolean goBackToHome,
      StoreManager storeManager) {
    this.view = view;
    this.crashReport = crashReport;
    this.goBackToHome = goBackToHome;
    this.storeManager = storeManager;
  }

  @Override public void present() {
    Observable<Void> handleSaveDataClick = view.saveDataClick()
        .flatMap(storeModel -> handleSaveClick(storeModel).toObservable());

    Observable<Void> handleCancelClick = view.cancelClick()
        .flatMap(__ -> handleCancelClick().toObservable());

    Observable<Void> handleLoadStoreImageClick = view.selectStoreImageClick()
        .flatMap(__ -> handleSelectStoreImageClick().toObservable());

    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.RESUME)
        .flatMap(__ -> Observable.merge(handleSaveDataClick, handleCancelClick,
            handleLoadStoreImageClick))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
  }

  private Completable handleSaveClick(ManageStoreViewModel storeModel) {
    Completable saveDataCompletable =
        storeManager.createOrUpdate(storeModel.getStoreId(), storeModel.getStoreName(),
            storeModel.getStoreDescription(), storeModel.getStoreImagePath(),
            storeModel.hasNewAvatar(), storeModel.getStoreThemeName(), storeModel.storeExists())
            .onErrorResumeNext(err -> handleStoreCreationErrors(err));

    return Completable.fromAction(() -> view.showWaitProgressBar())
        .andThen(saveDataCompletable)
        .doOnCompleted(() -> view.dismissWaitProgressBar())
        .doOnCompleted(() -> {
          if (goBackToHome) {
            view.navigateHome();
          } else {
            view.navigateBack();
          }
        });
  }

  private Completable handleStoreCreationErrors(Throwable err) {
    if (err instanceof StoreManager.NetworkError) {
      StoreManager.NetworkError networkError = ((StoreManager.NetworkError) err);
      if (networkError.isApiError()) {
        view.showError(R.string.ws_error_API_1);
      } else {
        view.showError(ErrorsMapper.getWebServiceErrorMessageFromCode(networkError.getError()));
      }
    } else if (err instanceof StoreManager.StoreCreationErrorWithCode) {
      view.showError(ErrorsMapper.getWebServiceErrorMessageFromCode(
          ((StoreManager.StoreCreationErrorWithCode) err).getErrorCode()));
    } else if (err instanceof StoreManager.StoreCreationError) {
      view.showError(R.string.ws_error_WOP_2);
    } else {
      view.showGenericError();
    }

    crashReport.log(err);
    return Completable.complete();
  }

  private Completable handleSelectStoreImageClick() {
    return Completable.fromAction(() -> view.showLoadImageDialog());
  }

  private Completable handleCancelClick() {
    return Completable.fromAction(() -> {
      if (goBackToHome) {
        view.navigateHome();
        return;
      }
      view.navigateBack();
    });
  }
}
