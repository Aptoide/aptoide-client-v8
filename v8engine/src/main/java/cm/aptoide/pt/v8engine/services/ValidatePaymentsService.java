/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 08/08/2016.
 */

package cm.aptoide.pt.v8engine.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.UiThread;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v3.CheckProductPaymentRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v3.ErrorResponse;
import cm.aptoide.pt.model.v3.PaymentPayload;
import cm.aptoide.pt.model.v3.PaymentResponse;

/**
 * Created by sithengineer on 03/08/16.
 * <p>
 * This @see{IntentService} iterates over all pending payments and sends them to the server.
 */
public class ValidatePaymentsService extends IntentService {

	public static final String PAYMENT_AS_JSON = "paymentAsJson";
	private static final String TAG = ValidatePaymentsService.class.getSimpleName();
	private Handler handler;
	private String data;

	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 * <p>
	 * name Used to name the worker thread, important only for debugging.
	 */
	public ValidatePaymentsService() {
		super("Validate Payments Service");
		handler = new Handler(Looper.getMainLooper());
	}

	public static Intent getIntent(Context context, PaymentPayload paymentPayload) {
		Intent intent = new Intent(context, ValidatePaymentsService.class);
		ObjectMapper mapper = new ObjectMapper();
		try {
			intent.putExtra(PAYMENT_AS_JSON, mapper.writeValueAsString(paymentPayload));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return intent;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (!AptoideAccountManager.isLoggedIn()) {
			Log.w(TAG, "user is not logged in. unable to process payments");
			reScheduleSync(intent);
			return;
		}

		data = intent.getStringExtra(PAYMENT_AS_JSON);
		ObjectMapper mapper = new ObjectMapper();
		try {
			final PaymentPayload paymentPayload = mapper.readValue(data, PaymentPayload.class);

			CheckProductPaymentRequest.ofPayPal(paymentPayload).execute(paymentResponse -> {
				// to run this handler in the main thread
				handler.post(() -> handlePaymentResponse(paymentPayload, paymentResponse));
			}, err -> {
				logError(paymentPayload.getAptoidePaymentId(), err);
				reScheduleSync(paymentPayload);
			});
		} catch (IOException e) {
			Logger.e(TAG, e);
		}
	}

	private void reScheduleSync(PaymentPayload payload) {
		if (payload.getAttempts() > 3) {
			// FIXME: 08/08/16 sithengineer what should i do if we get here?
		}
		payload.incrementAttempts();
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
	private void handlePaymentResponse(PaymentPayload paymentPayload, PaymentResponse paymentResponse) {
		if (paymentResponse != null && paymentResponse.getStatus() != null && paymentResponse.getStatus().equalsIgnoreCase("ok")) {
			Logger.i(TAG, "Payment validated for product id: " + paymentPayload.getAptoidePaymentId());
		} else {
			logError(paymentPayload.getAptoidePaymentId());
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
