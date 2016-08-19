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
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.v8engine.payment.PaymentFactory;
import cm.aptoide.pt.v8engine.payment.ProductFactory;
import cm.aptoide.pt.v8engine.repository.AppRepository;
import cm.aptoide.pt.v8engine.repository.InAppBillingRepository;
import cm.aptoide.pt.v8engine.repository.PaymentRepository;

/**
 * Created by sithengineer on 03/08/16.
 * <p>
 * This @see{IntentService} iterates over all pending payments and sends them to the server.
 */
public class PaymentConfirmationSyncService extends IntentService {

	private static final String TAG = PaymentConfirmationSyncService.class.getSimpleName();

	private PaymentRepository paymentRepository;
	private AlarmManager alarmManager;

	public static Intent getIntent(Context context) {
		return new Intent(context, PaymentConfirmationSyncService.class);
	}

	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 * <p>
	 * name Used to name the worker thread, important only for debugging.
	 */
	public PaymentConfirmationSyncService() {
		super("Validate Payments Service");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		final NetworkOperatorManager operatorManager = new NetworkOperatorManager((TelephonyManager) getSystemService(TELEPHONY_SERVICE));
		final ProductFactory productFactory = new ProductFactory();
		final PaymentFactory paymentFactory = new PaymentFactory();
		paymentRepository = new PaymentRepository(new AppRepository(operatorManager, productFactory, paymentFactory),
				new InAppBillingRepository(operatorManager, productFactory, paymentFactory), operatorManager, productFactory);
		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (!AptoideAccountManager.isLoggedIn()) {
			Log.w(TAG, "user is not logged in. unable to process payments");
			reScheduleSync();
			return;
		}

		try {
			paymentRepository.syncPaymentConfirmations().toBlocking().first();
		} catch (Exception exception) {
			reScheduleSync();
		}
	}

	private void reScheduleSync() {
//		if (payload.getAttempts() > 3) {
			// FIXME: 08/08/16 sithengineer what should i do if we get here?
//		}
//		payload.incrementAttempts();
		// re-schedule sending this payment info to server
		PendingIntent alarmIntent = PendingIntent.getService(this, 0, getIntent(this), PendingIntent.FLAG_ONE_SHOT);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 60 * 1000, alarmIntent);
	}
}
