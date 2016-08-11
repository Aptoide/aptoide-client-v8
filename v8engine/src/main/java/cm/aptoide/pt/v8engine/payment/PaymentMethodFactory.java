/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import com.paypal.android.sdk.payments.PayPalConfiguration;

import cm.aptoide.pt.v8engine.payment.method.PayPalPaymentConverter;
import cm.aptoide.pt.v8engine.payment.method.PayPalPaymentMethod;

/**
 * Created by marcelobenites on 8/10/16.
 */
public class PaymentMethodFactory {

	public static final String PAYPAL = "PAYPAL";

	public PaymentMethod create(Context context, String id) {
		switch (id) {
			case PAYPAL:
				return new PayPalPaymentMethod(context, id, getLocalBroadcastManager(context), getPayPalConfiguration(), getPaymentConverter());
			default:
				throw new IllegalArgumentException("Payment not supported: " + id);
		}
	}

	private PayPalPaymentConverter getPaymentConverter() {
		return new PayPalPaymentConverter();
	}

	private PayPalConfiguration getPayPalConfiguration() {
		final PayPalConfiguration configuration = new PayPalConfiguration();
		configuration.environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION);
		configuration.clientId("AW47wxAycZoTcXd5KxcJPujXWwImTLi-GNe3XvUUwFavOw8Nq4ZnlDT1SZIY");
		return configuration;
	}

	private LocalBroadcastManager getLocalBroadcastManager(Context context) {
		return LocalBroadcastManager.getInstance(context);
	}
}
