/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.payment.method;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.exception.PaymentCancellationException;
import cm.aptoide.pt.v8engine.payment.PaymentMethod;
import cm.aptoide.pt.v8engine.payment.exception.PaymentFailureException;

/**
 * Created by marcelobenites on 8/10/16.
 */
public class PayPalPaymentMethod implements PaymentMethod {

	private String id;
	private final Context context;
	private final LocalBroadcastManager broadcastManager;
	private final PayPalConfiguration configuration;
	private final PayPalPaymentConverter converter;

	private Payment currentPayment;
	private PaymentConfirmationListener listener;
	private PaymentConfirmationReceiver receiver;

	public PayPalPaymentMethod(Context context, String id, LocalBroadcastManager broadcastManager, PayPalConfiguration configuration, PayPalPaymentConverter converter) {
		this.id = id;
		this.context = context;
		this.broadcastManager = broadcastManager;
		this.configuration = configuration;
		this.converter = converter;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean isProcessingPayment() {
		return currentPayment != null;
	}

	@Override
	public void stopPaymentProcess() {
		broadcastManager.unregisterReceiver(receiver);
		receiver = null;
		currentPayment = null;
		listener = null;
	}

	@Override
	public void processPayment(Payment payment, PaymentConfirmationListener listener) {
		if (currentPayment == null) {
			this.listener = listener;
			currentPayment = payment;
			IntentFilter paymentResultFilter = new IntentFilter();
			paymentResultFilter.addAction(PaymentConfirmationReceiver.PAYMENT_RESULT_ACTION);
			receiver = new PaymentConfirmationReceiver();
			broadcastManager.registerReceiver(receiver, paymentResultFilter);
			context.startActivity(PayPalPaymentActivity.getIntent(context, converter.convertToPayPal(currentPayment), configuration));
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
							if (payPalConfirmation != null && currentPayment.getPaymentId().equals(payPalConfirmation.getProofOfPayment().getPaymentId())) {
								listener.onSuccess(converter.convertFromPayPal(payPalConfirmation, currentPayment));
								stopPaymentProcess();
							}
							break;
						case PAYMENT_STATUS_CANCELLED:
							listener.onError(new PaymentCancellationException("PayPal payment cancelled by user"));
							stopPaymentProcess();
							break;
						case PAYMENT_STATUS_FAILED:
						default:
							listener.onError(new PaymentFailureException("PayPal payment failed"));
							stopPaymentProcess();
							break;
					}
				}
			}
		}
	}
 }
