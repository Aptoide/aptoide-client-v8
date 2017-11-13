package cm.aptoide.pt.account.view.store;

import android.support.annotation.StringRes;
import cm.aptoide.pt.account.view.ImagePickerView;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import rx.Completable;
import rx.Observable;

public interface ManageStoreView extends ImagePickerView {

  void loadImageStateless(String pictureUri);

  Observable<ManageStoreViewModel> saveDataClick();

  Observable<Void> cancelClick();

  Observable<Void> socialChannelClick(Store.SocialChannelType socialChannelType);

  Completable showError(@StringRes int errorMessage);

  Completable showGenericError();

  void showWaitProgressBar();

  void dismissWaitProgressBar();

  void hideKeyboard();

  void expandEditText(Store.SocialChannelType socialChannelType);

  void setViewLinkErrors(int error, BaseV7Response.Type type);

  Observable<Boolean> socialChannelFocusChanged(Store.SocialChannelType socialChannelType);

  void revertSocialChannelUIState(Store.SocialChannelType socialChannelType);
}
