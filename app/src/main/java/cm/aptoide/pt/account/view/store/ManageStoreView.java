package cm.aptoide.pt.account.view.store;

import cm.aptoide.pt.account.view.ImagePickerView;
import rx.Observable;

public interface ManageStoreView extends ImagePickerView {

  void loadImageStateless(String pictureUri);

  Observable<ManageStoreViewModel> saveDataClick();

  Observable<ManageStoreViewModel> cancelClick();

  void showWaitProgressBar();

  void dismissWaitProgressBar();

  void showFacebookError(String error);

  void showTwitterError(String error);

  void showTwitchError(String error);

  void showYoutubeError(String error);

  void showError(String error);

  void showSuccessMessage();
}
