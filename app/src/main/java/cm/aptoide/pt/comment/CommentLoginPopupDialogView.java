package cm.aptoide.pt.comment;

import com.google.android.gms.common.ConnectionResult;
import rx.Observable;

/**
 * Created by tiagopedrinho on 22/11/2018.
 */

public interface CommentLoginPopupDialogView {

  Observable<Void> facebookSignUpEvent();

  Observable<Void> googleSignUpEvent();

  Observable<Void> facebookSignUpWithRequiredPermissionsInEvent();

  void showFacebookPermissionsRequiredError(Throwable throwable);

  void showConnectionError(ConnectionResult connectionResult);

  void showFacebookLogin();

  void hideFacebookLogin();

  void showGoogleLogin();

  void hideGoogleLogin();

  void showLoading();

  void hideLoading();

  void showError();
}
