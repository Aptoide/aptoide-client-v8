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

import java.util.Locale;

import cm.aptoide.pt.v8engine.payment.Price;
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
	private final String type;
	private final String name;
	private final String sign;
	private final Price price;
	private final LocalBroadcastManager broadcastManager;
	private final PayPalConfiguration configuration;
	private final PayPalConverter converter;

	private PaymentConfirmationReceiver receiver;
	private PaymentConfirmationListener listener;
	private boolean processing;
	private final Product product;
	private String methodLabel;

	public PayPalPayment(Context context, int id, String type, String name, String sign, Price price, LocalBroadcastManager broadcastManager, PayPalConfiguration configuration, PayPalConverter converter, Product product, String methodLabel) {
		this.context = context;
		this.id = id;
		this.type = type;
		this.name = name;
		this.sign = sign;
		this.price = price;
		this.broadcastManager = broadcastManager;
		this.configuration = configuration;
		this.converter = converter;
		this.product = product;
		this.methodLabel = methodLabel;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public Price getPrice() {
		return price;
	}

	@Override
	public boolean isProcessing() {
		return processing;
	}

	@Override
	public String getDescription() {
		return String.format(Locale.getDefault(), "%s - %.2f %s", methodLabel, price.getAmount(), sign);
	}

	@Override
	public void removeListener() {
		broadcastManager.unregisterReceiver(receiver);
		receiver = null;
		listener = null;
		processing = false;
	}

	@Override
	public void process(PaymentConfirmationListener listener) {
		if (!processing) {
			processing = true;
			this.listener = listener;
			IntentFilter paymentResultFilter = new IntentFilter();
			paymentResultFilter.addAction(PaymentConfirmationReceiver.PAYMENT_RESULT_ACTION);
			receiver = new PaymentConfirmationReceiver();
			broadcastManager.registerReceiver(receiver, paymentResultFilter);
			context.startActivity(PayPalPaymentActivity.getIntent(context, converter.convertToPayPal(price.getAmount(), price.getCurrency(),
					product.getTitle()), configuration));
		}
	}

	@Override
	public Product getProduct() {
		return product;
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
								listener.onSuccess(converter.convertFromPayPal(payPalConfirmation, id, product, price));
							}
							break;
						case PAYMENT_STATUS_CANCELLED:
							listener.onError(new PaymentCancellationException("PayPal payment cancelled by user"));
							break;
						case PAYMENT_STATUS_FAILED:
						default:
							listener.onError(new PaymentFailureException("PayPal payment failed"));
							break;
					}
				}
			}
		}
	}
 }
