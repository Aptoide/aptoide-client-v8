/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.paypal.android.sdk.payments.PayPalConfiguration;

import cm.aptoide.pt.v8engine.payment.service.PaymentConfirmationConverter;
import cm.aptoide.pt.v8engine.payment.service.PaymentConverter;
import cm.aptoide.pt.v8engine.payment.service.paypal.PayPalPaymentService;

/**
 * Created by marcelobenites on 8/10/16.
 */
public class PaymentServiceFactory {

	public static final String PAYPAL = "PAYPAL";

	public PaymentService create(Context context, String id) {
		switch (id) {
			case PAYPAL:
				return new PayPalPaymentService(context, id, getLocalBroadcastManager(context), getPayPalConfiguration(), getPaymentConverter(), getConfirmationConverter());
			default:
				throw new IllegalArgumentException("Payment not supported: " + id);
		}
	}

	private PaymentConfirmationConverter getConfirmationConverter() {
		return new PaymentConfirmationConverter();
	}

	private PaymentConverter getPaymentConverter() {
		return new PaymentConverter();
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
