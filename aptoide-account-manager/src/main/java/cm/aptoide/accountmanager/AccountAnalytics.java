package cm.aptoide.accountmanager;

public interface AccountAnalytics {

  void loginSuccess();

  void signUp();

  void sendAptoideLoginButtonPressed();

  void sendGoogleLoginButtonPressed();

  void sendFacebookLoginButtonPressed();

  void sendAptoideLoginFailEvent();

  void sendGoogleSignUpFailEvent();

  void sendAptoideSignUpSuccessEvent();

  void sendAptoideSignUpFailEvent();

  void sendFacebookMissingPermissionsEvent();

  void sendFacebookUserCancelledEvent();

  void sendFacebookErrorEvent();
}
