package cm.aptoide.pt.v8engine.billing.view;

import cm.aptoide.pt.v8engine.presenter.View;
import rx.Observable;

public interface PayPalView extends View {

  void showPayPal(String currency, String description, double amount);

  void showLoading();

  void hideLoading();

  Observable<PayPalResult> results();

  void showNetworkError();

  void showUnknownError();

  Observable<Void> errorDismisses();

  void dismiss();

  class PayPalResult {

    public static final int SUCCESS = 0;
    public static final int ERROR = 1;
    public static final int CANCELLED = 2;

    private final int status;
    private final String paymentConfirmationId;

    public PayPalResult(int status, String paymentConfirmationId) {
      this.status = status;
      this.paymentConfirmationId = paymentConfirmationId;
    }

    public int getStatus() {
      return status;
    }

    public String getPaymentConfirmationId() {
      return paymentConfirmationId;
    }
  }
}
