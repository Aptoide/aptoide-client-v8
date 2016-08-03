/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 03/08/2016.
 */

package cm.aptoide.pt.v8engine.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.UiThread;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.PaymentPayload;
import cm.aptoide.pt.dataprovider.ws.v3.CheckProductPaymentRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v3.PaymentResponse;
import io.realm.Realm;
import lombok.Cleanup;

/**
 * Created by sithengineer on 03/08/16.
 * <p>
 * This @see{IntentService} iterates over all pending payments and sends them to the server.
 */
public class ValidatePaymentsService extends IntentService {

	private static final String TAG = ValidatePaymentsService.class.getSimpleName();

	private Handler handler;

	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 * <p>
	 * name Used to name the worker thread, important only for debugging.
	 */
	public ValidatePaymentsService() {
		super("Validate Payments Service");
	}

	public static Intent getIntent(Context context) {
		return new Intent(context, ValidatePaymentsService.class);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		handler = new Handler();

		@Cleanup
		Realm realm = Database.get();
		for (PaymentPayload paymentPayload : Database.PaymentPayloadQ.getAll(realm)) {
			CheckProductPaymentRequest.ofPayPal(paymentPayload).execute(paymentResponse -> {
				// to run this handler in the main thread
				handler.post(() -> handlePaymentResponse(realm, paymentPayload, paymentResponse));
			}, err -> logError(paymentPayload, err), true);
		}
	}

	@UiThread
	private void handlePaymentResponse(Realm realm, PaymentPayload paymentPayload, PaymentResponse paymentResponse) {
		if (paymentResponse != null && paymentResponse.getStatus() != null && paymentResponse.getStatus().equalsIgnoreCase("ok")) {
			Database.PaymentPayloadQ.delete(realm, paymentPayload);
			Logger.i(TAG, "Payment validated for product id: " + paymentPayload.getProductId());
		} else {
			logError(paymentPayload);
		}
	}

	private void logError(PaymentPayload paymentPayload) {
		Logger.e(TAG, "Unable to process payment for product id: " + paymentPayload.getProductId());
	}

	private void logError(PaymentPayload paymentPayload, Throwable t) {
		Logger.e(TAG, "Unable to process payment for product id: " + paymentPayload.getProductId(), t);
	}
}
