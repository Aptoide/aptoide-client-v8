/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;
import cm.aptoide.pt.v8engine.BuildConfig;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.rx.RxAlertDialog;
import com.jakewharton.rxrelay.PublishRelay;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import java.math.BigDecimal;
import rx.Observable;

public class PayPalActivity extends ProductActivity implements PayPalView {

  private static final int PAY_APP_REQUEST_CODE = 12;

  private PublishRelay<PayPalResult> authorizationSubject;
  private ProgressBar progressBar;
  private RxAlertDialog unknownErrorDialog;
  private RxAlertDialog networkErrorDialog;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_paypal_payment);

    progressBar = (ProgressBar) findViewById(R.id.activity_paypal_authorization_preogress_bar);

    networkErrorDialog = new RxAlertDialog.Builder(this).setMessage(R.string.connection_error)
        .setPositiveButton(R.string.ok)
        .build();
    unknownErrorDialog =
        new RxAlertDialog.Builder(this).setMessage(R.string.all_message_general_error)
            .setPositiveButton(R.string.ok)
            .build();

    authorizationSubject = PublishRelay.create();

    attachPresenter(new PayPalPresenter(this, ((V8Engine) getApplicationContext()).getBilling(),
        ProductProvider.fromIntent(((V8Engine) getApplicationContext()).getBilling(), getIntent()),
        ((V8Engine) getApplicationContext()).getPaymentAnalytics()), savedInstanceState);
  }

  @Override
  public void showPayPal(String paymentCurrency, String paymentDescription, double paymentAmount) {

    final PayPalPayment payment =
        new PayPalPayment(new BigDecimal(paymentAmount), paymentCurrency, paymentDescription,
            PayPalPayment.PAYMENT_INTENT_SALE);

    startActivityForResult(
        new Intent(this, com.paypal.android.sdk.payments.PaymentActivity.class).putExtra(
            PayPalService.EXTRA_PAYPAL_CONFIGURATION,
            new PayPalConfiguration().environment(BuildConfig.PAYPAL_ENVIRONMENT)
                .clientId(BuildConfig.PAYPAL_KEY)
                .merchantName(V8Engine.getConfiguration()
                    .getMarketName()))
            .putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT, payment),
        PAY_APP_REQUEST_CODE);
  }

  @Override public void showLoading() {
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    progressBar.setVisibility(View.GONE);
  }

  @Override public Observable<PayPalResult> results() {
    return authorizationSubject;
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

  @Override public Observable<Void> errorDismisses() {
    return Observable.merge(networkErrorDialog.dismisses(), unknownErrorDialog.dismisses())
        .map(dialogInterface -> null);
  }

  @Override public void dismiss() {
    finish();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == PAY_APP_REQUEST_CODE) {
      if (resultCode == Activity.RESULT_OK) {
        final PaymentConfirmation confirmation = data.getParcelableExtra(
            com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION);
        if (confirmation != null && confirmation.getProofOfPayment() != null) {
          authorizationSubject.call(new PayPalResult(PayPalResult.SUCCESS,
              confirmation.getProofOfPayment()
                  .getPaymentId()));
        } else {
          authorizationSubject.call(new PayPalResult(PayPalResult.ERROR, null));
        }
      } else if (resultCode == Activity.RESULT_CANCELED) {
        authorizationSubject.call(new PayPalResult(PayPalResult.CANCELLED, null));
      } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
        authorizationSubject.call(new PayPalResult(PayPalResult.ERROR, null));
      }
    }
  }
}
