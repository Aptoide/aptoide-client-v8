/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 19/08/2016.
 */

package cm.aptoide.pt.billing.view;

import cm.aptoide.pt.billing.PaymentMethod;
import cm.aptoide.pt.billing.Product;
import cm.aptoide.pt.presenter.View;
import java.util.List;
import rx.Observable;

public interface PaymentView extends View {

  Observable<PaymentMethod> selectPaymentEvent();

  Observable<Void> cancelEvent();

  Observable<Void> buyEvent();

  void selectPayment(PaymentMethod payment);

  void showPaymentLoading();

  void showPurchaseLoading();

  void showBuyLoading();

  void showPayments(List<PaymentMethod> paymentList);

  void showProduct(Product product);

  void hidePaymentLoading();

  void hidePurchaseLoading();

  void hideBuyLoading();

  void showPaymentsNotFoundMessage();

  void showNetworkError();

  void showUnknownError();
}
