package cm.aptoide.pt.billing.view.login;

import cm.aptoide.pt.account.view.GooglePlayServicesView;
import rx.Observable;

public interface PaymentLoginView extends GooglePlayServicesView {

  Observable<Void> backButtonEvent();

  Observable<Void> upNavigationEvent();

  Observable<Void> facebookSignUpEvent();

  void showLoading();

  void hideLoading();

  void showError(String message);

  void showFacebookPermissionsRequiredError(Throwable throwable);
}
