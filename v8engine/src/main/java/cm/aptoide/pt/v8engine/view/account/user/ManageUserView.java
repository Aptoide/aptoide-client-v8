package cm.aptoide.pt.v8engine.view.account.user;

import cm.aptoide.pt.v8engine.presenter.View;
import rx.Completable;
import rx.Observable;

public interface ManageUserView extends View {
  void showLoadImageDialog();

  void navigateBack();

  Observable<ManageUserFragment.ViewModel> saveUserDataButtonClick();

  Observable<Void> selectUserImageClick();

  Observable<Void> cancelButtonClick();

  void navigateToProfileStepOne();

  void navigateToHome();

  void showProgressDialog();

  void dismissProgressDialog();

  Completable showErrorMessage(String error);
}
