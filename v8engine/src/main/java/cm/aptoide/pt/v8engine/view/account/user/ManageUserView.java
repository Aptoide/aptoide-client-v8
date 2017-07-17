package cm.aptoide.pt.v8engine.view.account.user;

import cm.aptoide.pt.v8engine.view.account.ImagePickerView;
import rx.Completable;
import rx.Observable;

public interface ManageUserView extends ImagePickerView {

  void setUserName(String name);

  Observable<ManageUserFragment.ViewModel> saveUserDataButtonClick();

  Observable<Void> cancelButtonClick();

  void showProgressDialog();

  void hideProgressDialog();

  Completable showErrorMessage(String error);

  void loadImageStateless(String pictureUri);
}
