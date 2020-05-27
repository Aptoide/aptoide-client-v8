package cm.aptoide.pt.account.view.store;

import cm.aptoide.pt.account.view.ImagePickerView;
import rx.Observable;

public interface ManageStoreView extends ImagePickerView {

  void loadImageStateless(String pictureUri);

  Observable<ManageStoreViewModel> saveDataClick();

  Observable<ManageStoreViewModel> cancelClick();

  void showWaitProgressBar();

  void dismissWaitProgressBar();

  void showError(String error);

  void showSuccessMessage();
}
