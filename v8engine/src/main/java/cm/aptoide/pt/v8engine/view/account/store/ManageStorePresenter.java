package cm.aptoide.pt.v8engine.view.account.store;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import rx.Completable;

public class ManageStorePresenter implements Presenter {

  private final boolean goBackToHome;

  public ManageStorePresenter(ManageStoreView view, boolean goBackToHome) {
    this.goBackToHome = goBackToHome;
  }

  @Override public void present() {

  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
  }

  public Completable handleSaveClick(ManageStoreModel storeModel){
    return new UpdateStoreUseCase(storeModel).execute().toCompletable();
  }

  public Completable handleCancelClick(){
    return  Completable.complete();
  }

}
