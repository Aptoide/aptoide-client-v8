package cm.aptoide.pt.view.share;

import cm.aptoide.pt.view.account.GooglePlayServicesView;
import rx.Observable;

public interface NotLoggedInShareView extends GooglePlayServicesView {

  Observable<Void> closeClick();

  void closeFragment();

  void navigateToMainView();

  void showError(Throwable throwable);

  Observable<Void> facebookSignInEvent();

  Observable<Void> googleSignInEvent();

  void showFacebookPermissionsRequiredError(Throwable throwable);

  Observable<Void> facebookSignInWithRequiredPermissionsInEvent();

  void showLoading();

  void hideLoading();

  void showFacebookLogin();

  void hideFacebookLogin();

  void showGoogleLogin();

  void hideGoogleLogin();
}
