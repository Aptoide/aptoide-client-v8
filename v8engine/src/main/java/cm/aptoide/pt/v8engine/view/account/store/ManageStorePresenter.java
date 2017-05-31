package cm.aptoide.pt.v8engine.view.account.store;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import rx.Completable;
import rx.Observable;

public class ManageStorePresenter implements Presenter {

  private final ManageStoreView view;
  private final boolean goBackToHome;
  private final StoreManager storeManager;

  public ManageStorePresenter(ManageStoreView view, boolean goBackToHome,
      StoreManager storeManager) {
    this.view = view;
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
        .subscribe();
  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
  }

  private Completable handleSaveClick(ManageStoreViewModel storeModel) {
    return storeManager.createOrUpdate(storeModel.getStoreId(), storeModel.getStoreName(),
        storeModel.getStoreDescription(), storeModel.getStoreImagePath(), storeModel.hasNewAvatar(),
        storeModel.getStoreThemeName(), storeModel.storeExists())
        .onErrorResumeNext(err -> {

          if(err instanceof StoreManager.NetworkError){
            view.showError(((StoreManager.StoreCreationError) err).getError());
          } else if (err instanceof StoreManager.StoreCreationError) {
            view.showError(((StoreManager.StoreCreationError)err).getError());
          } else {
            view.showGenericError();
          }

          CrashReport.getInstance()
              .log(err);
          return Completable.complete();
        });
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
