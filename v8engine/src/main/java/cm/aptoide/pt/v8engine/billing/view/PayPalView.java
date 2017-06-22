package cm.aptoide.pt.v8engine.billing.view;

import cm.aptoide.pt.v8engine.presenter.View;
import rx.Observable;

interface PayPalView extends View {

  void showPayPal(String currency, String description, double amount);

  void showLoading();

  void hideLoading();

  Observable<String> paymentConfirmationId();

  void showNetworkError();

  void showUnknownError();

  void dismiss();
}
