/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import cm.aptoide.pt.v8engine.payment.exception.PaymentException;

/**
 * Created by marcelobenites on 8/10/16.
 */
public interface PaymentMethod {

	public String getId();

	public void stopPaymentProcess();

	public boolean isProcessingPayment();

	public void processPayment(Payment payment, PaymentConfirmationListener listener);

	public static interface PaymentConfirmationListener {

		void onSuccess(PaymentConfirmation paymentConfirmation);

		void onError(PaymentException exception);
	}
}