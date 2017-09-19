package cm.aptoide.pt.billing.view.login;

import cm.aptoide.accountmanager.AptoideCredentials;
import cm.aptoide.pt.account.view.GooglePlayServicesView;
import rx.Observable;

public interface PaymentLoginView extends GooglePlayServicesView {

  Observable<Void> backButtonEvent();

  Observable<Void> upNavigationEvent();

  Observable<Void> facebookSignUpEvent();

  Observable<Void> googleSignUpEvent();

  Observable<Void> recoverPasswordEvent();

  Observable<AptoideCredentials> aptoideLoginEvent();

  Observable<AptoideCredentials> aptoideSignUpEvent();

  void showLoading();

  void hideLoading();

  void showError(String message);

  void showFacebookPermissionsRequiredError();

}
