/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.v8engine.payment.method;

import com.paypal.android.sdk.payments.PayPalPayment;

import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;

/**
 * Created by marcelobenites on 8/10/16.
 */
public class PayPalPaymentConverter {

	public PayPalPayment convertToPayPal(Payment payment) {
		return new PayPalPayment(payment.getPrice(), payment.getCurrency(), payment.getPaymentId(), PayPalPayment.PAYMENT_INTENT_SALE);
	}

	public PaymentConfirmation convertFromPayPal(com.paypal.android.sdk.payments.PaymentConfirmation payPalConfirmation, Payment payment) {
		return new PaymentConfirmation(payment, payPalConfirmation.getProofOfPayment().getTransactionId());
	}
}