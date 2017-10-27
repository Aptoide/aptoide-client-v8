package cm.aptoide.pt.account.view.store;

import android.support.annotation.StringRes;
import cm.aptoide.pt.account.view.ImagePickerView;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import java.util.List;
import rx.Completable;
import rx.Observable;

public interface ManageStoreView extends ImagePickerView {

  void loadImageStateless(String pictureUri);

  Observable<ManageStoreViewModel> saveDataClick();

  Observable<Void> cancelClick();

  Observable<Void> facebookClick();

  Observable<Void> twitchClick();

  Observable<Void> twitterClick();

  Observable<Void> youtubeClick();

  Completable showError(@StringRes int errorMessage);

  Completable showGenericError();

  void showWaitProgressBar();

  void dismissWaitProgressBar();

  void hideKeyboard();

  void manageFacebookViews();

  void manageTwitchViews();

  void manageTwitterViews();

  void manageYoutubeViews();

  void setViewLinkErrors(List<BaseV7Response.StoreLinks> storeLinks);

  Observable<Boolean> facebookUserFocusChanged();

  void changeFacebookUI();

  Observable<Boolean> twitchUserFocusChanged();

  void changeTwitchUI();
}
