package cm.aptoide.pt.view.account.user;

/**
 * Created by danielchen on 07/09/17.
 */

import cm.aptoide.pt.view.account.ImagePickerView;
import rx.Completable;
import rx.Observable;

public interface ManageUserView extends ImagePickerView {

    void setUserName(String name);

    Observable<ManageUserFragment.ViewModel> saveUserDataButtonClick();

    void showProgressDialog();

    void hideProgressDialog();

    Completable showErrorMessage(String error);

    void loadImageStateless(String pictureUri);
}
