/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.rx.RxAlertDialog;
import rx.Observable;

public class PayPalActivity extends ProductActivity implements PayPalView {

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

    attachPresenter(new PayPalPresenter(this, ((V8Engine) getApplicationContext()).getBilling(),
        ProductProvider.fromBundle(((V8Engine) getApplicationContext()).getBilling(),
            getIntent().getExtras()), ((V8Engine) getApplicationContext()).getPaymentAnalytics(),
        new PaymentNavigator(new PurchaseBundleMapper(new PaymentThrowableCodeMapper()),
            getActivityNavigator())), savedInstanceState);
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

  @Override public Observable<Void> errorDismisses() {
    return Observable.merge(networkErrorDialog.dismisses(), unknownErrorDialog.dismisses())
        .map(dialogInterface -> null);
  }
}
