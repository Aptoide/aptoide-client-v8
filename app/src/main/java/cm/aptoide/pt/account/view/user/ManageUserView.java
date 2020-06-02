package cm.aptoide.pt.account.view.user;

import cm.aptoide.pt.account.view.ImagePickerView;
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
