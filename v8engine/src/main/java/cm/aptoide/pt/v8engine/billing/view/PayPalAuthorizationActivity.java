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
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalService;
import rx.Observable;
import rx.subjects.PublishSubject;

public class PayPalAuthorizationActivity extends AuthorizationActivity
    implements PayPalAuthorizationView {

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

    attachPresenter(new PayPalAuthorizationPresenter(this,
        ((V8Engine) getApplicationContext()).getAptoideBilling(),
        ProductProvider.fromIntent(((V8Engine) getApplicationContext()).getAptoideBilling(),
            getIntent())), savedInstanceState);
  }

  @Override public void showPayPalAuthorization() {
    startActivityForResult(new Intent(this, PayPalFuturePaymentActivity.class).putExtra(
        PayPalService.EXTRA_PAYPAL_CONFIGURATION,
        new PayPalConfiguration().environment(BuildConfig.PAYPAL_ENVIRONMENT)
            .clientId(BuildConfig.PAYPAL_KEY)
            .merchantName(V8Engine.getConfiguration()
                .getMarketName())
            .merchantPrivacyPolicyUri(Uri.parse(BuildConfig.PAYPAL_PRIVACY_POLICY_URL))
            .merchantUserAgreementUri(Uri.parse(BuildConfig.PAYPAL_USER_AGREEMENT_URL))),
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

  @Override public Observable<String> authorizationCode() {
    return authorizationSubject;
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == PAY_APP_REQUEST_CODE) {
      if (resultCode == Activity.RESULT_OK) {
        final PayPalAuthorization payPalAuthorization =
            data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
        if (payPalAuthorization != null) {
          authorizationSubject.onNext(payPalAuthorization.getAuthorizationCode());
        }
      } else if (resultCode == Activity.RESULT_CANCELED) {
        authorizationSubject.onError(
            new PaymentCancellationException("User cancelled PayPal authorization."));
      } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
        authorizationSubject.onError(new PaymentException("Unknown PayPal authorization error"));
      }
    }
  }
}
