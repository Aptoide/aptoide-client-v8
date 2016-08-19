/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import com.paypal.android.sdk.payments.PayPalConfiguration;

import cm.aptoide.pt.v8engine.payment.paypal.PayPalConverter;
import cm.aptoide.pt.v8engine.payment.paypal.PayPalPayment;

/**
 * Created by marcelobenites on 8/10/16.
 */
public class PaymentFactory {

	public static final String PAYPAL = "paypal";

	public Payment create(Context context, String type, int id, String name, String sign, double price, String currency, double taxRate, Product product) {
		switch (type) {
			case PAYPAL:
				return new PayPalPayment(context, id, name, sign, new Price(price, currency, taxRate), getLocalBroadcastManager(context), getPayPalConfiguration(), getPaymentConverter(), product);
			default:
				throw new IllegalArgumentException("Payment not supported: " + type);
		}
	}

	private PayPalConverter getPaymentConverter() {
		return new PayPalConverter();
	}

	private PayPalConfiguration getPayPalConfiguration() {
		final PayPalConfiguration configuration = new PayPalConfiguration();
		configuration.environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK);
		configuration.clientId("AW47wxAycZoTcXd5KxcJPujXWwImTLi-GNe3XvUUwFavOw8Nq4ZnlDT1SZIY");
		return configuration;
	}

	private LocalBroadcastManager getLocalBroadcastManager(Context context) {
		return LocalBroadcastManager.getInstance(context);
	}
}
