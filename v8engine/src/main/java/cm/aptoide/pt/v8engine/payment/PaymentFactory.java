/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.paypal.android.sdk.payments.PayPalConfiguration;

import cm.aptoide.pt.model.v3.PaymentService;
import cm.aptoide.pt.v8engine.BuildConfig;
import cm.aptoide.pt.v8engine.payment.paypal.PayPalConverter;
import cm.aptoide.pt.v8engine.payment.paypal.PayPalPayment;

/**
 * Created by marcelobenites on 8/10/16.
 */
public class PaymentFactory {

	public static final String PAYPAL = "paypal";

	public Payment create(Context context, PaymentService paymentService, Product product) {
		switch (paymentService.getShortName()) {
			case PAYPAL:
				return new PayPalPayment(context, paymentService.getId(), paymentService.getShortName(), paymentService.getName(), paymentService.getSign(),
						getPrice(paymentService.getPrice(), paymentService.getCurrency(), paymentService.getTaxRate()), getLocalBroadcastManager(context),
						getPayPalConfiguration(), getPaymentConverter(), product, paymentService.getTypes().get(0).getLabel());
			default:
				throw new IllegalArgumentException("Payment not supported: " + paymentService.getShortName());
		}
	}

	@NonNull
	private Price getPrice(double price, String currency, double taxRate) {
		return new Price
				(price,
				currency,
				taxRate);
	}

	private PayPalConverter getPaymentConverter() {
		return new PayPalConverter();
	}

	private PayPalConfiguration getPayPalConfiguration() {
		final PayPalConfiguration configuration = new PayPalConfiguration();
		configuration.environment(BuildConfig.PAYPAL_ENVIRONMENT);
		configuration.clientId(BuildConfig.PAYPAL_KEY);
		return configuration;
	}

	private LocalBroadcastManager getLocalBroadcastManager(Context context) {
		return LocalBroadcastManager.getInstance(context);
	}
}
