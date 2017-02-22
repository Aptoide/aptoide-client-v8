package cm.aptoide.pt.v8engine.view;

import cm.aptoide.pt.v8engine.viewModel.FacebookAccountViewModel;
import rx.Observable;

public interface JoinCommunityView extends GoogleLoginView {

  void showLoading();

  void hideLoading();

  void showError(Throwable throwable);

  void showFacebookLogin();

  Observable<Void> showAptoideLoginClick();

  Observable<Void> showSignUpClick();

  Observable<Void> successMessageShown();

  Observable<FacebookAccountViewModel> facebookLoginClick();

  void showSuccessMessage();

  void setLoginAreaVisible();

  void setSignUpAreaVisible();

  void showPermissionsRequiredMessage();

  void hideFacebookLogin();

  void navigateToMainView();
}
