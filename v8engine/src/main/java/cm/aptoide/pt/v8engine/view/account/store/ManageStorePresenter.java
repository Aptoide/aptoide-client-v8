package cm.aptoide.pt.v8engine.view.account.store;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import rx.Completable;

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

  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
  }

  public Completable handleSaveClick(ManageStoreViewModel storeModel) {
    return storeManager.createOrUpdate(storeModel.getStoreId(), storeModel.getStoreName(),
        storeModel.getStoreDescription(), storeModel.getStoreImagePath(), storeModel.hasNewAvatar(),
        storeModel.getStoreThemeName(), storeModel.storeExists())
        .onErrorResumeNext(err -> {
          // todo
          // handle errors here
          CrashReport.getInstance()
              .log(err);
          return Completable.complete();
        });
  }

  public Completable handleCancelClick() {
    return Completable.complete();
  }
}
