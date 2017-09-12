package cm.aptoide.pt.view.share;

import cm.aptoide.pt.view.account.GooglePlayServicesView;
import rx.Observable;

public interface NotLoggedInShareView extends GooglePlayServicesView {

  Observable<Void> closeEvent();

  Observable<Void> facebookSignUpEvent();

  Observable<Void> googleSignUpEvent();

  Observable<Void> facebookSignUpWithRequiredPermissionsInEvent();

  void showError(Throwable throwable);

  void showFacebookPermissionsRequiredError(Throwable throwable);

  void showLoading();

  void hideLoading();

  void showFacebookLogin();

  void hideFacebookLogin();

  void showGoogleLogin();

  void hideGoogleLogin();
}
