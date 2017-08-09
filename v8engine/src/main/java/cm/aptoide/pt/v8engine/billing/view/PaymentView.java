/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 19/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.view;

import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.presenter.View;
import java.util.List;
import rx.Observable;

public interface PaymentView extends View {

  Observable<PaymentMethodViewModel> selectPaymentEvent();

  Observable<Void> cancelEvent();

  Observable<Void> buyEvent();

  void selectPayment(PaymentMethodViewModel payment);

  void showPaymentLoading();

  void showPurchaseLoading();

  void showBuyLoading();

  void showPayments(List<PaymentMethodViewModel> paymentList);

  void showProduct(Product product);

  void hidePaymentLoading();

  void hidePurchaseLoading();

  void hideBuyLoading();

  void showPaymentsNotFoundMessage();

  void showNetworkError();

  void showUnknownError();

  class PaymentMethodViewModel {

    private final int id;
    private final String name;
    private final String description;

    public PaymentMethodViewModel(int id, String name, String description) {
      this.id = id;
      this.name = name;
      this.description = description;
    }

    public int getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public String getDescription() {
      return description;
    }
  }
}
