package cm.aptoide.pt.v8engine.view.account.store;

import android.support.annotation.StringRes;
import cm.aptoide.pt.v8engine.presenter.View;
import rx.Completable;
import rx.Observable;

public interface ManageStoreView extends View {
  Observable<Void> selectStoreImageClick();

  Observable<ManageStoreFragment.ViewModel> saveDataClick();

  Observable<Void> cancelClick();

  void showLoadImageDialog();

  Completable showError(@StringRes int errorMessage);

  Completable showGenericError();

  void showWaitProgressBar();

  void dismissWaitProgressBar();
}
