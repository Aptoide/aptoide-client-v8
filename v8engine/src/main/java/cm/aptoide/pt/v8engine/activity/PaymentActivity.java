/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 19/08/2016.
 */

package cm.aptoide.pt.v8engine.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.realm.PaymentConfirmation;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.iab.ErrorCodeFactory;
import cm.aptoide.pt.iab.InAppBillingSerializer;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.PaymentFactory;
import cm.aptoide.pt.v8engine.payment.PaymentManager;
import cm.aptoide.pt.v8engine.payment.ProductFactory;
import cm.aptoide.pt.v8engine.payment.Purchase;
import cm.aptoide.pt.v8engine.payment.PurchaseFactory;
import cm.aptoide.pt.v8engine.payment.PurchaseIntentFactory;
import cm.aptoide.pt.v8engine.payment.product.AptoideProduct;
import cm.aptoide.pt.v8engine.presenter.PaymentPresenter;
import cm.aptoide.pt.v8engine.repository.AppRepository;
import cm.aptoide.pt.v8engine.repository.InAppBillingRepository;
import cm.aptoide.pt.v8engine.repository.PaymentRepository;
import cm.aptoide.pt.v8engine.view.PaymentView;
import rx.Observable;

public class PaymentActivity extends ActivityView implements PaymentView {

	private static final String PRODUCT_EXTRA = "product";

	private View overlay;
	private View header;
	private View body;
	private ProgressBar progressBar;
	private ViewGroup paymentContainer;
	private ImageView productIcon;
	private TextView productName;
	private TextView productPriceDescription;
	private TextView noPaymentsText;
	private TextView payWithText;
	private Button cancelButton;

	private List<Observable<Payment>> paymentSelections;
	private PurchaseIntentFactory intentFactory;

	public static Intent getIntent(Context context, AptoideProduct product) {
		final Intent intent = new Intent(context, PaymentActivity.class);
		intent.putExtra(PRODUCT_EXTRA, product);
		return intent;
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment);

		paymentContainer = (ViewGroup) findViewById(R.id.activity_payment_list);
		progressBar = (ProgressBar) findViewById(R.id.activity_payment_progress_bar);
		productIcon = (ImageView) findViewById(R.id.activity_payment_product_icon);
		productName= (TextView) findViewById(R.id.activity_payment_product_name);
		productPriceDescription= (TextView) findViewById(R.id.activity_payment_product_price_description);
		header = findViewById(R.id.activity_payment_header);
		body = findViewById(R.id.activity_payment_body);
		cancelButton = (Button) findViewById(R.id.activity_payment_cancel_button);
		overlay = findViewById(R.id.payment_activity_overlay);
		noPaymentsText = (TextView) findViewById(R.id.activity_payment_no_payments_text);
		payWithText = (TextView) findViewById(R.id.activity_payment_pay_with_text);
		paymentSelections = new ArrayList<>();
		intentFactory = new PurchaseIntentFactory(new ErrorCodeFactory());

		final AptoideProduct product = getIntent().getParcelableExtra(PRODUCT_EXTRA);

		// TODO Repository Factory, Presenter Factory
		final NetworkOperatorManager operatorManager = new NetworkOperatorManager((TelephonyManager) getSystemService(TELEPHONY_SERVICE));
		final ProductFactory productFactory = new ProductFactory();
		final PaymentManager paymentManager = new PaymentManager(new PaymentRepository(new AppRepository(operatorManager, productFactory),
				new InAppBillingRepository(operatorManager, productFactory),
				new NetworkOperatorManager((TelephonyManager) getSystemService(TELEPHONY_SERVICE)), productFactory,
				new PurchaseFactory(new InAppBillingSerializer()), new PaymentFactory(), AccessorFactory.getAccessorFor(PaymentConfirmation.class)));

		attachPresenter(new PaymentPresenter(this, paymentManager, product), savedInstanceState);
	}

	@Override
	public void dismiss(Purchase purchase) throws IOException {
		finish(RESULT_OK, intentFactory.create(purchase));
	}

	@Override
	public void dismiss(Throwable throwable) {
		finish(RESULT_CANCELED, intentFactory.create(throwable));
	}

	@Override
	public void dismiss() {
		finish(RESULT_CANCELED, intentFactory.createFromCancellation());
	}

	@Override
	public void showProduct(AptoideProduct product) {
		ImageLoader.load(product.getIcon(), productIcon);
		productName.setText(product.getTitle());
		productPriceDescription.setText(product.getPriceDescription());
	}

	@Override
	public Observable<Void> cancellationSelection() {
		return Observable.merge(RxView.clicks(cancelButton), RxView.clicks(overlay));
	}

	@Override
	public void showPayments(List<Payment> paymentList) {
		paymentContainer.removeAllViews();
		payWithText.setVisibility(View.VISIBLE);
		noPaymentsText.setVisibility(View.GONE);
		Button paymentButton;
		for (Payment payment: paymentList) {
			paymentButton = (Button) getLayoutInflater().inflate(getButtonLayoutResource(payment), paymentContainer, false);
			paymentButton.setText(payment.getDescription());
			paymentContainer.addView(paymentButton);
			paymentSelections.add(RxView.clicks(paymentButton).map(click -> payment));
		}
	}

	@Override
	public void showPaymentsNotFoundMessage() {
		paymentContainer.removeAllViews();
		payWithText.setVisibility(View.INVISIBLE);
		noPaymentsText.setVisibility(View.VISIBLE);
	}

	@Override
	public void showLoading() {
		header.setVisibility(View.INVISIBLE);
		body.setVisibility(View.INVISIBLE);
		cancelButton.setVisibility(View.INVISIBLE);
		progressBar.setVisibility(View.VISIBLE);
	}

	@Override
	public void removeLoading() {
		header.setVisibility(View.VISIBLE);
		body.setVisibility(View.VISIBLE);
		cancelButton.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
	}

	@Override
	public Observable<Payment> paymentSelection() {
		return Observable.merge(paymentSelections);
	}

	@LayoutRes
	private int getButtonLayoutResource(Payment payment) {
		return R.layout.button_visa;
	}

	private void finish(int code, Intent intent) {
		setResult(code, intent);
		finish();
	}

	private void finish(int code) {
		setResult(code);
		finish();
	}
}