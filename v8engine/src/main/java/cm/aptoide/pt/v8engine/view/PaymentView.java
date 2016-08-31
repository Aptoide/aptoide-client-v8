/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 19/08/2016.
 */

package cm.aptoide.pt.v8engine.view;

import java.io.IOException;
import java.util.List;

import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.Purchase;
import cm.aptoide.pt.v8engine.payment.product.AptoideProduct;
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

	void dismiss(Purchase purchase) throws IOException;

	void dismiss(Throwable throwable);

	void dismiss();

	void showPaymentsNotFoundMessage();

}