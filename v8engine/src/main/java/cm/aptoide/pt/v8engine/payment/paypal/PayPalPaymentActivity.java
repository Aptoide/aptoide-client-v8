/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.payment.paypal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;

import cm.aptoide.pt.v8engine.payment.exception.PaymentException;

/**
 * Created by marcelobenites on 8/10/16.
 */
public class PayPalPaymentActivity extends AppCompatActivity {

	private static final int PAY_APP_REQUEST_CODE = 12;
	private static final String PAYPAL_PAYMENT_EXTRA = "cm.aptoide.pt.v8engine.payment.service.extra.PAYPAL_PAYMENT";
	private static final String PAYPAL_CONFIGURATION_EXTRA = "cm.aptoide.pt.v8engine.payment.service.extra.PAYPAL_CONFIGURATION";

	private LocalBroadcastManager broadcastManager;
	private com.paypal.android.sdk.payments.PayPalPayment payment;
	private PayPalConfiguration configuration;
	private Intent serviceIntent;

	public static Intent getIntent(Context context, com.paypal.android.sdk.payments.PayPalPayment payment, PayPalConfiguration configuration) {
		return new Intent(context, PayPalPaymentActivity.class).putExtra(PAYPAL_PAYMENT_EXTRA, payment).putExtra(PAYPAL_CONFIGURATION_EXTRA, configuration);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getIntent().hasExtra(PAYPAL_PAYMENT_EXTRA)) {
			payment = getIntent().getParcelableExtra(PAYPAL_PAYMENT_EXTRA);
			configuration = getIntent().getParcelableExtra(PAYPAL_CONFIGURATION_EXTRA);
		} else {
			throw new IllegalStateException(new PaymentException("Payment and PayPal configuration must be provided!"));
		}

		broadcastManager = LocalBroadcastManager.getInstance(this);

		serviceIntent = new Intent(this, PayPalService.class);
		serviceIntent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configuration);
		startService(serviceIntent);

		Intent activityIntent = new Intent(this, PaymentActivity.class);
		activityIntent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configuration);
		activityIntent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
		startActivityForResult(activityIntent, PAY_APP_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PAY_APP_REQUEST_CODE) {
			final Intent result = new Intent(PayPalPayment.PaymentConfirmationReceiver.PAYMENT_RESULT_ACTION);
			switch (resultCode) {
				case Activity.RESULT_OK:
					broadcastManager.sendBroadcast(result
							.putExtra(PayPalPayment.PaymentConfirmationReceiver.PAYMENT_STATUS_EXTRA,
									PayPalPayment.PaymentConfirmationReceiver.PAYMENT_STATUS_OK)
							.putExtra(PayPalPayment.PaymentConfirmationReceiver.PAYMENT_CONFIRMATION_EXTRA,
									(Parcelable) data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION)));
					break;
				case Activity.RESULT_CANCELED:
					broadcastManager.sendBroadcast(result.putExtra(PayPalPayment.PaymentConfirmationReceiver.PAYMENT_STATUS_EXTRA,
							PayPalPayment.PaymentConfirmationReceiver.PAYMENT_STATUS_CANCELLED));
					break;
				case PaymentActivity.RESULT_EXTRAS_INVALID:
				default:
					broadcastManager.sendBroadcast(result.putExtra(PayPalPayment.PaymentConfirmationReceiver.PAYMENT_STATUS_EXTRA,
							PayPalPayment.PaymentConfirmationReceiver.PAYMENT_STATUS_FAILED));
					break;
			}
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopService(serviceIntent);
	}
}
