/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 19/08/2016.
 */

package cm.aptoide.pt.v8engine.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.iab.ErrorCodeFactory;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.payment.AptoidePay;
import cm.aptoide.pt.v8engine.payment.Payer;
import cm.aptoide.pt.v8engine.payment.Purchase;
import cm.aptoide.pt.v8engine.payment.PurchaseIntentFactory;
import cm.aptoide.pt.v8engine.payment.products.AptoideProduct;
import cm.aptoide.pt.v8engine.presenter.PaymentPresenter;
import cm.aptoide.pt.v8engine.repository.PaymentAuthorizationFactory;
import cm.aptoide.pt.v8engine.repository.ProductRepository;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.view.PaymentView;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.List;
import java.util.Locale;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class PaymentActivity extends BaseActivity implements PaymentView {

  private static final String PRODUCT_EXTRA = "product";

  private View overlay;
  private View header;
  private View body;
  private View actionButtons;
  private View globalProgressView;
  private View paymentsProgressView;
  private ViewGroup morePaymentsList;
  private View morePaymentsContainer;
  private ImageView productIcon;
  private TextView productName;
  private TextView productDescription;
  private TextView noPaymentsText;
  private Button morePaymentsButton;
  private Button cancelButton;
  private Button buyButton;
  private TextView selectedPaymentName;
  private TextView selectedPaymentPrice;

  private PublishRelay<PaymentViewModel> usePaymentClick;
  private PublishRelay<PaymentViewModel> registerPaymentClick;
  private CompositeSubscription paymentClicks;
  private PurchaseIntentFactory intentFactory;
  private AlertDialog networkErrorDialog;
  private AlertDialog unknownErrorDialog;

  public static Intent getIntent(Context context, AptoideProduct product) {
    final Intent intent = new Intent(context, PaymentActivity.class);
    intent.putExtra(PRODUCT_EXTRA, product);
    return intent;
  }

  @SuppressLint("UseSparseArrays") @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_payment);

    overlay = findViewById(R.id.payment_activity_overlay);
    globalProgressView = findViewById(R.id.activity_payment_global_progress_bar);
    paymentsProgressView = findViewById(R.id.activity_payment_list_progress_bar);
    noPaymentsText = (TextView) findViewById(R.id.activity_payment_no_payments_text);

    header = findViewById(R.id.activity_payment_header);
    productIcon = (ImageView) findViewById(R.id.activity_payment_product_icon);
    productName = (TextView) findViewById(R.id.activity_payment_product_name);
    productDescription = (TextView) findViewById(R.id.activity_payment_product_description);

    body = findViewById(R.id.activity_payment_body);
    selectedPaymentName = (TextView) findViewById(R.id.activity_selected_payment_name);
    selectedPaymentPrice = (TextView) findViewById(R.id.activity_selected_payment_price);
    morePaymentsButton = (Button) findViewById(R.id.activity_payment_more_payments_button);
    morePaymentsList = (ViewGroup) findViewById(R.id.activity_payment_list);
    morePaymentsContainer = findViewById(R.id.activity_payment_list_container);

    actionButtons = findViewById(R.id.activity_payment_buttons);
    cancelButton = (Button) findViewById(R.id.activity_payment_cancel_button);
    buyButton = (Button) findViewById(R.id.activity_payment_buy_button);

    usePaymentClick = PublishRelay.create();
    registerPaymentClick = PublishRelay.create();
    intentFactory = new PurchaseIntentFactory(new ErrorCodeFactory());
    paymentClicks = new CompositeSubscription();
    final ContextThemeWrapper dialogTheme =
        new ContextThemeWrapper(this, R.style.AptoideThemeDefault);
    networkErrorDialog = new AlertDialog.Builder(dialogTheme).setMessage(R.string.connection_error)
        .setPositiveButton(android.R.string.ok, null)
        .create();
    unknownErrorDialog =
        new AlertDialog.Builder(dialogTheme).setMessage(R.string.having_some_trouble)
            .setPositiveButton(android.R.string.ok, null)
            .create();

    final AptoideProduct product = getIntent().getParcelableExtra(PRODUCT_EXTRA);
    final ProductRepository productRepository =
        RepositoryFactory.getProductRepository(this, product);
    final Payer payer = new Payer(this, AptoideAccountManager.getInstance(this,
        Application.getConfiguration()));
    attachPresenter(new PaymentPresenter(this,
            new AptoidePay(RepositoryFactory.getPaymentConfirmationRepository(this, product),
                RepositoryFactory.getPaymentAuthorizationRepository(this), productRepository,
                new PaymentAuthorizationFactory(this), payer), product, payer, productRepository),
        savedInstanceState);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    paymentClicks.clear();
  }

  @Override public Observable<PaymentViewModel> usePaymentSelection() {
    return usePaymentClick;
  }

  @Override public Observable<Void> cancellationSelection() {
    return Observable.merge(RxView.clicks(cancelButton), RxView.clicks(overlay))
        .subscribeOn(AndroidSchedulers.mainThread())
        .unsubscribeOn(AndroidSchedulers.mainThread());
  }

  @Override public Observable<Void> buySelection() {
    return RxView.clicks(buyButton)
        .subscribeOn(AndroidSchedulers.mainThread())
        .unsubscribeOn(AndroidSchedulers.mainThread());
  }

  @Override public Observable<Void> otherPaymentsSelection() {
    return RxView.clicks(morePaymentsButton)
        .subscribeOn(AndroidSchedulers.mainThread())
        .unsubscribeOn(AndroidSchedulers.mainThread());
  }

  @Override public Observable<PaymentViewModel> registerPaymentSelection() {
    return registerPaymentClick;
  }

  @Override public void showGlobalLoading() {
    header.setVisibility(View.GONE);
    body.setVisibility(View.GONE);
    actionButtons.setVisibility(View.GONE);
    globalProgressView.setVisibility(View.VISIBLE);
  }

  @Override public void showPaymentsLoading() {
    paymentsProgressView.setVisibility(View.VISIBLE);
  }

  @Override public void showOtherPayments(List<PaymentViewModel> otherPayments) {
    morePaymentsList.removeAllViews();
    noPaymentsText.setVisibility(View.GONE);
    morePaymentsButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up, 0);
    paymentClicks.clear();

    if (otherPayments.isEmpty()) {
      morePaymentsContainer.setVisibility(View.GONE);
      morePaymentsButton.setVisibility(View.GONE);
    } else {
      morePaymentsContainer.setVisibility(View.VISIBLE);
      morePaymentsButton.setVisibility(View.VISIBLE);
      int height = otherPayments.size() * AptoideUtils.ScreenU.getPixels(72);
      int maxHeight = AptoideUtils.ScreenU.getPixels(144);
      morePaymentsContainer.getLayoutParams().height = Math.min(height, maxHeight);
    }

    View view;
    TextView name;
    TextView description;
    TextView approving;
    Button useButton;
    Button registerButton;
    for (PaymentViewModel otherPayment : otherPayments) {
      view = getLayoutInflater().inflate(R.layout.payment_item, morePaymentsList, false);
      name = (TextView) view.findViewById(R.id.item_payment_name);
      description = (TextView) view.findViewById(R.id.item_payment_description);
      useButton = (Button) view.findViewById(R.id.item_payment_button_use);
      registerButton = (Button) view.findViewById(R.id.item_payment_button_register);
      approving = (TextView) view.findViewById(R.id.item_payment_approving_text);

      name.setText(otherPayment.getName());
      description.setText(otherPayment.getDescription());
      switch (otherPayment.getStatus()) {
        case USE:
          paymentClicks.add(RxView.clicks(useButton)
              .subscribeOn(AndroidSchedulers.mainThread())
              .unsubscribeOn(AndroidSchedulers.mainThread())
              .doOnNext(click -> usePaymentClick.call(otherPayment))
              .subscribe(__ -> { /* does nothing */}, err -> {
                CrashReport.getInstance().log(err);
              }));
          useButton.setVisibility(View.VISIBLE);
          approving.setVisibility(View.GONE);
          registerButton.setVisibility(View.GONE);
          break;
        case REGISTER:
          paymentClicks.add(RxView.clicks(registerButton)
              .subscribeOn(AndroidSchedulers.mainThread())
              .unsubscribeOn(AndroidSchedulers.mainThread())
              .doOnNext(click -> registerPaymentClick.call(otherPayment))
              .subscribe(__ -> { /* does nothing */}, err -> {
                CrashReport.getInstance().log(err);
              }));
          registerButton.setVisibility(View.VISIBLE);
          approving.setVisibility(View.GONE);
          useButton.setVisibility(View.GONE);
          break;
        case APPROVING:
          approving.setVisibility(View.VISIBLE);
          registerButton.setVisibility(View.GONE);
          useButton.setVisibility(View.GONE);
          break;
        default:
          throw new IllegalStateException("Invalid payment view model state");
      }
      morePaymentsList.addView(view);
    }
  }

  @Override public void hideOtherPayments() {
    morePaymentsButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
    morePaymentsContainer.setVisibility(View.GONE);
  }

  @Override public void showProduct(AptoideProduct product) {
    ImageLoader.with(this).load(product.getIcon(), productIcon);
    productName.setText(product.getTitle());
    productDescription.setText(product.getDescription());
  }

  @Override public void showSelectedPayment(PaymentViewModel selectedPayment) {
    selectedPaymentName.setText(selectedPayment.getName());
    selectedPaymentPrice.setText(
        String.format(Locale.getDefault(), "%.2f %s", selectedPayment.getPrice(),
            selectedPayment.getCurrency()));
  }

  @Override public void hideGlobalLoading() {
    header.setVisibility(View.VISIBLE);
    body.setVisibility(View.VISIBLE);
    actionButtons.setVisibility(View.VISIBLE);
    globalProgressView.setVisibility(View.GONE);
  }

  @Override public void hidePaymentsLoading() {
    paymentsProgressView.setVisibility(View.GONE);
  }

  @Override public void dismiss(Purchase purchase) {
    finish(RESULT_OK, intentFactory.create(purchase));
  }

  @Override public void dismiss(Throwable throwable) {
    finish(RESULT_CANCELED, intentFactory.create(throwable));
  }

  @Override public void dismiss() {
    finish(RESULT_CANCELED, intentFactory.createFromCancellation());
  }

  @Override public void navigateToAuthorizationView(int paymentId, AptoideProduct product) {
    startActivity(WebAuthorizationActivity.getIntent(this, paymentId, product));
  }

  @Override public void showPaymentsNotFoundMessage() {
    noPaymentsText.setVisibility(View.VISIBLE);
  }

  @Override public void showNetworkError() {
    if (!networkErrorDialog.isShowing() && !unknownErrorDialog.isShowing()) {
      networkErrorDialog.show();
    }
  }

  @Override public void showUnknownError() {
    if (!networkErrorDialog.isShowing() && !unknownErrorDialog.isShowing()) {
      unknownErrorDialog.show();
    }
  }

  @Override public void hideAllErrors() {
    networkErrorDialog.dismiss();
    unknownErrorDialog.dismiss();
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
