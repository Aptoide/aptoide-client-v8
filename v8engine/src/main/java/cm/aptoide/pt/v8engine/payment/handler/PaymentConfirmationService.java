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
import android.telephony.TelephonyManager;
import android.util.Log;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.PaymentFactory;
import cm.aptoide.pt.v8engine.payment.ProductFactory;
import cm.aptoide.pt.v8engine.repository.AppRepository;
import cm.aptoide.pt.v8engine.repository.InAppBillingRepository;
import cm.aptoide.pt.v8engine.repository.PaymentRepository;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by sithengineer on 03/08/16.
 * <p>
 * This @see{IntentService} iterates over all pending payments and sends them to the server.
 */
public class PaymentConfirmationService extends IntentService {

	public static final String EXTRA_PAYMENT_CONFIRMATION = "cm.aptoide.pt.v8engine.payment.handler.intent.extra.PAYMENT_CONFIRMATION";
	private static final String TAG = PaymentConfirmationService.class.getSimpleName();

	private Handler handler;
	private PaymentConfirmation paymentConfirmation;
	private NetworkOperatorManager operatorManager;
	private PaymentRepository paymentRepository;
	private CompositeSubscription subscription;
	private AlarmManager alarmManager;

	public static Intent getIntent(Context context, PaymentConfirmation paymentConfirmation) {
		return new Intent(context, PaymentConfirmationService.class).putExtra(EXTRA_PAYMENT_CONFIRMATION, paymentConfirmation);
	}

	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 * <p>
	 * name Used to name the worker thread, important only for debugging.
	 */
	public PaymentConfirmationService() {
		super("Validate Payments Service");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		operatorManager = new NetworkOperatorManager((TelephonyManager) getSystemService(TELEPHONY_SERVICE));
		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		final ProductFactory productFactory = new ProductFactory();
		final PaymentFactory paymentFactory = new PaymentFactory();
		paymentRepository = new PaymentRepository(new AppRepository(operatorManager, productFactory, paymentFactory),
				new InAppBillingRepository(operatorManager, productFactory, paymentFactory), operatorManager, productFactory);
		subscription = new CompositeSubscription();
		handler = new Handler(Looper.getMainLooper());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (!AptoideAccountManager.isLoggedIn()) {
			Log.w(TAG, "user is not logged in. unable to process payments");
			reScheduleSync(intent);
			return;
		}
		paymentConfirmation = intent.getParcelableExtra(EXTRA_PAYMENT_CONFIRMATION);
		subscription.add(paymentRepository.savePaymentConfirmation(paymentConfirmation)
				.subscribe(
						success -> Logger.i(TAG, "Payment validated for product id: " + paymentConfirmation.getProduct().getId()),
						throwable -> {
							Logger.e(TAG, "Unable to process payment for product id: " + paymentConfirmation.getProduct().getId(), throwable);
							reScheduleSync(paymentConfirmation);
						}));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		subscription.unsubscribe();
	}

	private void reScheduleSync(PaymentConfirmation payload) {
//		if (payload.getAttempts() > 3) {
			// FIXME: 08/08/16 sithengineer what should i do if we get here?
//		}
//		payload.incrementAttempts();
		reScheduleSync(getIntent(this, payload));
	}

	private void reScheduleSync(Intent intent) {
		// re-schedule sending this payment info to server
		PendingIntent alarmIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 60 * 1000, alarmIntent);
	}
}
