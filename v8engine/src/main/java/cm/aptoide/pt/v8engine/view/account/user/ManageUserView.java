package cm.aptoide.pt.v8engine.view.account.user;

import cm.aptoide.pt.v8engine.presenter.View;
import rx.Completable;
import rx.Observable;

public interface ManageUserView extends View {
  void showLoadImageDialog();

  Observable<ManageUserFragment.ViewModel> saveUserDataButtonClick();

  Observable<Void> selectUserImageClick();

  Observable<Void> cancelButtonClick();

  void showProgressDialog();

  void dismissProgressDialog();

  Completable showErrorMessage(String error);
}
