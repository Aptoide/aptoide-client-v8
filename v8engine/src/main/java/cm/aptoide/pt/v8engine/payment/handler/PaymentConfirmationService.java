/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 17/08/2016.
 */

package cm.aptoide.pt.v8engine.payment.handler;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.UiThread;
import android.telephony.TelephonyManager;
import android.util.Log;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.v3.CheckProductPaymentRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v3.ErrorResponse;
import cm.aptoide.pt.model.v3.PaymentResponse;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.product.InAppBillingProduct;
import cm.aptoide.pt.v8engine.payment.product.PaidAppProduct;

/**
 * Created by sithengineer on 03/08/16.
 * <p>
 * This @see{IntentService} iterates over all pending payments and sends them to the server.
 */
public class PaymentConfirmationService extends IntentService {

	public static final String PAYMENT_CONFIRMATION_EXTRA = "paymentAsJson";
	private static final String TAG = PaymentConfirmationService.class.getSimpleName();
	private Handler handler;
	private PaymentConfirmation paymentConfirmation;
	private NetworkOperatorManager operatorManager;

	public static Intent getIntent(Context context, PaymentConfirmation paymentConfirmation) {
		return new Intent(context, PaymentConfirmationService.class).putExtra(PAYMENT_CONFIRMATION_EXTRA, paymentConfirmation);
	}

	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 * <p>
	 * name Used to name the worker thread, important only for debugging.
	 */
	public PaymentConfirmationService() {
		super("Validate Payments Service");
		handler = new Handler(Looper.getMainLooper());
	}

	@Override
	public void onCreate() {
		super.onCreate();
		operatorManager = new NetworkOperatorManager((TelephonyManager) getSystemService(TELEPHONY_SERVICE));
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (!AptoideAccountManager.isLoggedIn()) {
			Log.w(TAG, "user is not logged in. unable to process payments");
			reScheduleSync(intent);
			return;
		}

		paymentConfirmation = intent.getParcelableExtra(PAYMENT_CONFIRMATION_EXTRA);
			final CheckProductPaymentRequest request;
			if (paymentConfirmation.getProduct().getType().equals("iab")) {
				final InAppBillingProduct product = (InAppBillingProduct) paymentConfirmation.getProduct();
				request = CheckProductPaymentRequest.of(paymentConfirmation.getPaymentConfirmationId(), product.getId(), product.getPackageName(), product
						.getApiVersion(), product.getCurrency(), product.getDeveloperPayload(), product.getTaxRate(), product.getPrice(), operatorManager);
			} else {
				final PaidAppProduct product = (PaidAppProduct) paymentConfirmation.getProduct();
				request = CheckProductPaymentRequest.of(paymentConfirmation.getPaymentConfirmationId(), product.getId(), product.getStoreName(),
						product.getCurrency(), product.getTaxRate(), product.getPrice(), operatorManager);
			}

			request.execute(paymentResponse -> {
				// to run this handler in the main thread
				handler.post(() -> handlePaymentResponse(paymentConfirmation, paymentResponse));
			}, err -> {
				logError(paymentConfirmation.getProduct().getId(), err);
				reScheduleSync(paymentConfirmation);
			});
	}

	private void reScheduleSync(PaymentConfirmation payload) {
//		if (payload.getAttempts() > 3) {
			// FIXME: 08/08/16 sithengineer what should i do if we get here?
//		}
//		payload.incrementAttempts();
		reScheduleSync(getIntent(getBaseContext(), payload));
	}

	private void reScheduleSync(Intent intent) {
		// re-schedule sending this payment info to server
		Context baseContext = getBaseContext();
		AlarmManager alarmMgr = (AlarmManager) baseContext.getSystemService(Context.ALARM_SERVICE);
		PendingIntent alarmIntent = PendingIntent.getService(baseContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
		alarmMgr.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 60 * 1000, alarmIntent);
	}

	@UiThread
	private void handlePaymentResponse(PaymentConfirmation paymentPayload, PaymentResponse paymentResponse) {
		if (paymentResponse != null && paymentResponse.getStatus() != null && paymentResponse.getStatus().equalsIgnoreCase("ok")) {
			Logger.i(TAG, "Payment validated for product id: " + paymentPayload.getProduct().getId());
		} else {
			logError(paymentPayload.getProduct().getId());
			if (paymentResponse.hasErrors()) {
				for (final ErrorResponse errorResponse : paymentResponse.getErrors()) {
					logError(errorResponse.msg);
				}
			}
			reScheduleSync(paymentPayload);
		}
	}

	private void logError(long productId) {
		logError("Unable to process payment for product id: " + productId);
	}

	private void logError(String errorMsg) {
		Logger.e(TAG, errorMsg);
	}

	private void logError(long productId, Throwable t) {
		Logger.e(TAG, "Unable to process payment for product id: " + productId, t);
	}
}
