package cm.aptoide.pt.view.share;

import android.content.Context;
import cm.aptoide.pt.presenter.GoogleLoginView;
import com.facebook.login.LoginResult;
import rx.Observable;

public interface NotLoggedInShareView extends GoogleLoginView {

  Observable<Void> closeClick();

  void closeFragment();

  void navigateToMainView();

  void showError(Throwable throwable);

  void showFacebookLogin();

  void showFacebookLoginError();

  void showFacebookCancelledError();

  Observable<LoginResult> facebookLoginClick();

  void showPermissionsRequiredMessage();

  void showLoading();

  void hideLoading();

  void hideFacebookLogin();

  Context getApplicationContext();

}
