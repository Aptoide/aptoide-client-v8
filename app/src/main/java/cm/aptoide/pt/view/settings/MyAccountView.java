package cm.aptoide.pt.view.settings;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.aptoideviews.socialmedia.SocialMediaView;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStore;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.presenter.View;
import rx.Observable;

/**
 * Created by franciscocalado on 13/03/18.
 */

public interface MyAccountView extends View {

  void showAccount(Account account);

  Observable<Void> loginClick();

  Observable<Void> signOutClick();

  Observable<Void> storeClick();

  Observable<Void> userClick();

  Observable<Void> editStoreClick();

  Observable<Void> editUserProfileClick();

  Observable<Void> settingsClicked();

  Observable<GetStore> getStore();

  Observable<Void> aptoideTvCardViewClick();

  Observable<Void> aptoideUploaderCardViewClick();

  Observable<Void> aptoideBackupCardViewClick();

  void startAptoideTvWebView();

  void refreshUI(Store store);

  void showLoginAccountDisplayable();

  Observable<Void> createStoreClick();

  Observable<SocialMediaView.SocialMediaType> socialMediaClick();
}
