package cm.aptoide.pt.account.view.store;

import android.support.annotation.StringRes;
import cm.aptoide.pt.account.view.ImagePickerView;
import rx.Completable;
import rx.Observable;

public interface ManageStoreView extends ImagePickerView {

  void loadImageStateless(String pictureUri);

  Observable<ManageStoreFragment.ViewModel> saveDataClick();

  Observable<Void> cancelClick();

  Completable showError(@StringRes int errorMessage);

  Completable showGenericError();

  void showWaitProgressBar();

  void dismissWaitProgressBar();

  void hideKeyboard();
}
