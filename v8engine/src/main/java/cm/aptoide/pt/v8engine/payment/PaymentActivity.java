/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;

import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.v8engine.payment.handler.PaymentConfirmationHandler;
import cm.aptoide.pt.v8engine.repository.AppRepository;
import cm.aptoide.pt.v8engine.repository.InAppBillingRepository;
import cm.aptoide.pt.v8engine.repository.PaymentRepository;
import rx.android.schedulers.AndroidSchedulers;

public class PaymentActivity extends RxAppCompatActivity {

	private static final String PRODUCT_EXTRA = "product";

	private PaymentManager paymentManager;
	private PaymentRepository paymentRepository;
	private Product product;

	public static Intent getIntent(Context context, Product product) {
		final Intent intent = new Intent(context, PaymentActivity.class);
		intent.putExtra(PRODUCT_EXTRA, product);
		return intent;
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		product = getIntent().getParcelableExtra(PRODUCT_EXTRA);

		// TODO Repository Factory
		final NetworkOperatorManager operatorManager = new NetworkOperatorManager((TelephonyManager) getSystemService(TELEPHONY_SERVICE));
		final ProductFactory productFactory = new ProductFactory();
		final PaymentFactory paymentFactory = new PaymentFactory();
		paymentRepository = new PaymentRepository(new AppRepository(operatorManager, productFactory, paymentFactory),
				new InAppBillingRepository(operatorManager, productFactory, paymentFactory),
				new NetworkOperatorManager((TelephonyManager) getSystemService(TELEPHONY_SERVICE)), productFactory);
		paymentManager = new PaymentManager(new PaymentConfirmationHandler(this, paymentRepository));
		paymentRepository.getPayments(this, product)
				.compose(bindUntilEvent(ActivityEvent.DESTROY))
				.observeOn(AndroidSchedulers.mainThread())
				.flatMap(payments -> paymentManager.pay(payments.get(0), product))
				.subscribe(success -> finish(RESULT_OK, null), throwable -> finish(RESULT_CANCELED, throwable));
	}

	private void finish(int code, Throwable throwable) {
		setResult(code);
		finish();
	}
}