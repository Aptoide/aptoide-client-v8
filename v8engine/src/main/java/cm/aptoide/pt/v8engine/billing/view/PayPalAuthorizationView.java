package cm.aptoide.pt.v8engine.billing.view;

import cm.aptoide.pt.v8engine.presenter.View;
import rx.Observable;

interface PayPalAuthorizationView extends View {

  void showPayPalAuthorization();

  void showLoading();

  void hideLoading();

  Observable<String> authorizationCode();

  void showNetworkError();

  void showUnknownError();

  void dismiss();
}
