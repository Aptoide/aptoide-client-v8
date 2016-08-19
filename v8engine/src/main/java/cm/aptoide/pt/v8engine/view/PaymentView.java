/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 19/08/2016.
 */

package cm.aptoide.pt.v8engine.view;

import java.util.List;

import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.view.View;
import rx.Observable;

/**
 * Created by marcelobenites on 8/19/16.
 */
public interface PaymentView extends View {

	Observable<Payment> paymentSelection();

	Observable<Void> cancellationSelection();

	void showLoading();

	void showPayments(List<Payment> paymentList);

	void showProduct(Product product);

	void removeLoading();

	void dismissWithSuccess();

	void dismissWithCancellation();

	void dismissWithFailure();

	void showPaymentCancellationError();

	void showPaymentFailureError();
}