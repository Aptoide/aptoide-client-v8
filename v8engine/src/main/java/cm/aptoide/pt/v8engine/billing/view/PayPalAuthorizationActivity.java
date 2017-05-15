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
import cm.aptoide.pt.v8engine.BuildConfig;
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

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    authorizationSubject = PublishSubject.create();

    attachPresenter(new PayPalAuthorizationPresenter(this,
        ((V8Engine) getApplicationContext()).getAptoideBilling(),
        getIntent().getIntExtra(EXTRA_PAYMENT_ID, 0), getIntent().getLongExtra(EXTRA_APP_ID, -1),
        getIntent().getStringExtra(EXTRA_STORE_NAME),
        getIntent().getBooleanExtra(EXTRA_SPONSORED, false),
        getIntent().getIntExtra(EXTRA_API_VERSION, -1), getIntent().getStringExtra(EXTRA_TYPE),
        getIntent().getStringExtra(EXTRA_SKU), getIntent().getStringExtra(EXTRA_PACKAGE_NAME),
        getIntent().getStringExtra(EXTRA_DEVELOPER_PAYLOAD)), savedInstanceState);
  }

  @Override public void showPayPalAuthorization() {
    startActivityForResult(new Intent(this, PayPalFuturePaymentActivity.class).putExtra(
        PayPalService.EXTRA_PAYPAL_CONFIGURATION,
        new PayPalConfiguration().environment(BuildConfig.PAYPAL_ENVIRONMENT)
            .clientId(BuildConfig.PAYPAL_KEY)
            .merchantName(V8Engine.getConfiguration()
                .getMarketName())
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"))),
        PAY_APP_REQUEST_CODE);
  }

  @Override public void showLoading() {

  }

  @Override public void hideLoading() {

  }

  @Override public void showNetworkError() {
    finish();
  }

  @Override public void showUnknownError() {
    finish();
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
