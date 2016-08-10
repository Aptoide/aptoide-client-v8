/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.v8engine.payment.service;

import com.paypal.android.sdk.payments.PayPalPayment;

import cm.aptoide.pt.v8engine.payment.Payment;

/**
 * Created by marcelobenites on 8/10/16.
 */
public class PaymentConverter {

	public PayPalPayment convertToPayPal(Payment payment) {
		return new PayPalPayment(payment.getPrice(), payment.getCurrency(), payment.getPaymentId(), PayPalPayment.PAYMENT_INTENT_SALE);
	}
}