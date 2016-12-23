/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 19/08/2016.
 */

package cm.aptoide.pt.v8engine.view;

import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.Purchase;
import cm.aptoide.pt.v8engine.payment.products.AptoideProduct;
import java.util.List;
import rx.Observable;

/**
 * Created by marcelobenites on 8/19/16.
 */
public interface PaymentView extends View {

  Observable<Payment> paymentSelection();

  Observable<Void> cancellationSelection();

  void showLoading();

  void showPayments(List<Payment> paymentList);

  void showProduct(AptoideProduct product);

  void removeLoading();

  void dismiss(Purchase purchase);

  void dismiss(Throwable throwable);

  void dismiss();

  void showPaymentsNotFoundMessage();
}