/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.payment.paypal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.exception.PaymentCancellationException;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.exception.PaymentFailureException;

/**
 * Created by marcelobenites on 8/10/16.
 */
public class PayPalPayment implements Payment {

	private final Context context;
	private final int id;
	private final int icon;
	private final double price;
	private final String currency;
	private final double taxRate;
	private final LocalBroadcastManager broadcastManager;
	private final PayPalConfiguration configuration;
	private final PayPalConverter converter;

	private PaymentConfirmationReceiver receiver;
	private PaymentConfirmationListener listener;
	private boolean processing;
	private Product product;

	public PayPalPayment(Context context, int id, int icon, double price, String currency, double taxRate, LocalBroadcastManager broadcastManager,
	                     PayPalConfiguration configuration, PayPalConverter converter) {
		this.context = context;
		this.id = id;
		this.icon = icon;
		this.price = price;
		this.currency = currency;
		this.taxRate = taxRate;
		this.broadcastManager = broadcastManager;
		this.configuration = configuration;
		this.converter = converter;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public int getIcon() {
		return icon;
	}

	@Override
	public double getPrice() {
		return price;
	}

	@Override
	public String getCurrency() {
		return currency;
	}

	@Override
	public double getTaxRate() {
		return taxRate;
	}

	@Override
	public boolean isProcessing() {
		return processing;
	}

	@Override
	public void cancel() {
		broadcastManager.unregisterReceiver(receiver);
		receiver = null;
		listener = null;
		processing = false;
		product = null;
	}

	@Override
	public void process(Product product, PaymentConfirmationListener listener) {
		if (!processing) {
			this.product = product;
			processing = true;
			this.listener = listener;
			IntentFilter paymentResultFilter = new IntentFilter();
			paymentResultFilter.addAction(PaymentConfirmationReceiver.PAYMENT_RESULT_ACTION);
			receiver = new PaymentConfirmationReceiver();
			broadcastManager.registerReceiver(receiver, paymentResultFilter);
			context.startActivity(PayPalPaymentActivity.getIntent(context, converter.convertToPayPal(price, currency, product.getDescription()), configuration));
		}
	}

	private void startPayPalActivity() {
		Intent intent = new Intent(context, PayPalService.class);
		intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configuration);
		context.startService(intent);
	}

	public class PaymentConfirmationReceiver extends BroadcastReceiver {

		public static final String PAYMENT_RESULT_ACTION = "cm.aptoide.pt.v8engine.payment.service.action.PAYMENT_RESULT";
		public static final String PAYMENT_CONFIRMATION_EXTRA = "cm.aptoide.pt.v8engine.payment.service.extra.PAYMENT_CONFIRMATION";
		public static final String PAYMENT_STATUS_EXTRA = "cm.aptoide.pt.v8engine.payment.service.extra.PAYMENT_STATUS";

		public static final int PAYMENT_STATUS_OK = 0;
		public static final int PAYMENT_STATUS_FAILED = 1;
		public static final int PAYMENT_STATUS_CANCELLED = 2;

		@Override
		public void onReceive(Context context, Intent intent) {
			final PaymentConfirmation payPalConfirmation;
			if (listener != null) {
				if (intent != null && PAYMENT_RESULT_ACTION.equals(intent.getAction()) && intent.hasExtra(PAYMENT_STATUS_EXTRA)) {
					switch (intent.getIntExtra(PAYMENT_STATUS_EXTRA, PAYMENT_STATUS_FAILED)) {
						case PAYMENT_STATUS_OK:
							payPalConfirmation = intent.getParcelableExtra(PAYMENT_CONFIRMATION_EXTRA);
							if (payPalConfirmation != null) {
								listener.onSuccess(converter.convertFromPayPal(payPalConfirmation, product));
								cancel();
							}
							break;
						case PAYMENT_STATUS_CANCELLED:
							listener.onError(new PaymentCancellationException("PayPal payment cancelled by user"));
							cancel();
							break;
						case PAYMENT_STATUS_FAILED:
						default:
							listener.onError(new PaymentFailureException("PayPal payment failed"));
							cancel();
							break;
					}
				}
			}
		}
	}
 }
