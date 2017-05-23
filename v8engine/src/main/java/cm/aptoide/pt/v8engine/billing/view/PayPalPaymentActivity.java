/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ProgressBar;
import cm.aptoide.pt.v8engine.BuildConfig;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.billing.exception.PaymentCancellationException;
import cm.aptoide.pt.v8engine.billing.exception.PaymentException;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import java.math.BigDecimal;
import rx.Observable;
import rx.subjects.PublishSubject;

public class PayPalPaymentActivity extends ProductActivity implements PayPalPaymentView {

  private static final int PAY_APP_REQUEST_CODE = 12;

  private com.paypal.android.sdk.payments.PayPalPayment payment;
  private PayPalConfiguration configuration;
  private PublishSubject<String> authorizationSubject;
  private ProgressBar progressBar;
  private AlertDialog unknownErrorDialog;
  private AlertDialog networkErrorDialog;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_paypal_authorization);

    progressBar = (ProgressBar) findViewById(R.id.activity_paypal_authorization_preogress_bar);

    networkErrorDialog = new AlertDialog.Builder(this).setMessage(R.string.connection_error)
        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
          finish();
        })
        .create();
    unknownErrorDialog = new AlertDialog.Builder(this).setMessage(R.string.having_some_trouble)
        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
          finish();
        })
        .create();

    unknownErrorDialog = new AlertDialog.Builder(this).setMessage(R.string.having_some_trouble)
        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
          finish();
        })
        .create();

    authorizationSubject = PublishSubject.create();

    attachPresenter(
        new PayPalPaymentPresenter(this, ((V8Engine) getApplicationContext()).getAptoideBilling(),
            ProductProvider.fromIntent(((V8Engine) getApplicationContext()).getAptoideBilling(),
                getIntent())), savedInstanceState);
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
                .merchantName(V8Engine.getConfiguration().getMarketName())
                .merchantPrivacyPolicyUri(Uri.parse(BuildConfig.PAYPAL_PRIVACY_POLICY_URL))
                .merchantUserAgreementUri(Uri.parse(BuildConfig.PAYPAL_USER_AGREEMENT_URL)))
            .putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT, payment),
        PAY_APP_REQUEST_CODE);
  }

  @Override public void showLoading() {
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    progressBar.setVisibility(View.GONE);
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

  @Override public void dismiss() {
    finish();
  }

  @Override public Observable<String> paymentConfirmationId() {
    return authorizationSubject;
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == PAY_APP_REQUEST_CODE) {
      if (resultCode == Activity.RESULT_OK) {
        final PaymentConfirmation confirmation = data.getParcelableExtra(
            com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION);
        if (confirmation != null) {
          authorizationSubject.onNext(confirmation.getProofOfPayment().getPaymentId());
        }
      } else if (resultCode == Activity.RESULT_CANCELED) {
        authorizationSubject.onError(
            new PaymentCancellationException("User cancelled PayPal local processing."));
      } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
        authorizationSubject.onError(new PaymentException("Unknown PayPal local processing error"));
      }
    }
  }
}
