/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 26/01/2017.
 */

package cm.aptoide.pt.v8engine.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.navigation.AccountNavigator;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.payment.AptoidePay;
import cm.aptoide.pt.v8engine.payment.Payer;
import cm.aptoide.pt.v8engine.payment.authorizations.WebAuthorization;
import cm.aptoide.pt.v8engine.payment.products.AptoideProduct;
import cm.aptoide.pt.v8engine.repository.PaymentAuthorizationFactory;
import cm.aptoide.pt.v8engine.repository.ProductRepository;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import com.trello.rxlifecycle.android.ActivityEvent;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by marcelobenites on 11/11/16.
 */
public class WebAuthorizationActivity extends ActivityView {

  private static final String EXTRA_PAYMENT_ID =
      "cm.aptoide.pt.v8engine.payment.providers.boacompra.intent.extra.PAYMENT_ID";
  private static final String EXTRA_PRODUCT =
      "cm.aptoide.pt.v8engine.payment.providers.boacompra.intent.extra.PRODUCT";
  private WebView webView;
  private AptoidePay aptoidePay;
  private int paymentId;
  private View progressBarContainer;
  private AlertDialog unknownErrorDialog;

  public static Intent getIntent(Context context, int paymentId, AptoideProduct product) {
    final Intent intent = new Intent(context, WebAuthorizationActivity.class);
    intent.putExtra(EXTRA_PAYMENT_ID, paymentId);
    intent.putExtra(EXTRA_PRODUCT, product);
    return intent;
  }

  @SuppressLint("SetJavaScriptEnabled") @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_web_authorization);

    if (getIntent().hasExtra(EXTRA_PAYMENT_ID) && getIntent().hasExtra(EXTRA_PRODUCT)) {
      paymentId = getIntent().getIntExtra(EXTRA_PAYMENT_ID, 0);
      final AptoideProduct product = getIntent().getParcelableExtra(EXTRA_PRODUCT);
      final AptoideAccountManager accountManager = ((V8Engine) getApplicationContext()).getAccountManager();
      aptoidePay = new AptoidePay(RepositoryFactory.getPaymentConfirmationRepository(this, product),
          RepositoryFactory.getPaymentAuthorizationRepository(this),
          new PaymentAuthorizationFactory(this),
          new Payer(this, accountManager, new AccountNavigator(this, accountManager)),
          RepositoryFactory.getPaymentRepository(this, product));

      webView = (WebView) findViewById(R.id.activity_boa_compra_authorization_web_view);
      webView.getSettings().setJavaScriptEnabled(true);
      webView.setWebChromeClient(new WebChromeClient());
      progressBarContainer = findViewById(R.id.activity_web_authorization_preogress_bar);
      unknownErrorDialog = new AlertDialog.Builder(this).setMessage(R.string.having_some_trouble)
          .setPositiveButton(android.R.string.ok, (dialog, which) -> {
            finish();
          })
          .create();
    } else {
      throw new IllegalStateException("Web payment urls must be provided");
    }

    progressBarContainer.setVisibility(View.VISIBLE);
    aptoidePay.getAuthorization(paymentId)
        .distinctUntilChanged(authorization -> authorization.getStatus())
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(ActivityEvent.DESTROY))
        .subscribe(authorization -> {
          if (authorization.isAuthorized()) {
            hideLoadingAndDismiss();
          } else if (authorization.isInvalid()) {
            progressBarContainer.setVisibility(View.GONE);
            unknownErrorDialog.show();
          } else if (authorization.isInitiated()) {
            showAuthorization((WebAuthorization) authorization);
          } else if (authorization.isPending()) {
            finish();
          }
        });
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    ((ViewGroup) webView.getParent()).removeView(webView);
    webView.setWebViewClient(null);
    webView.destroy();
    unknownErrorDialog.dismiss();
  }

  private void hideLoadingAndDismiss() {
    progressBarContainer.setVisibility(View.GONE);
    finish();
  }

  private void showAuthorization(WebAuthorization webAuthorization) {
    webView.setWebViewClient(new WebViewClient() {

      @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (url.equals(webAuthorization.getRedirectUrl())) {
          progressBarContainer.setVisibility(View.VISIBLE);
          aptoidePay.authorize(webAuthorization.getPaymentId())
              .observeOn(AndroidSchedulers.mainThread())
              .compose(bindUntilEvent(ActivityEvent.DESTROY).forCompletable())
              .subscribe(() -> hideLoadingAndDismiss(), throwable -> {
                hideLoadingAndDismiss();
                CrashReport.getInstance().log(throwable);
              });
        }
      }

      @Override public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        progressBarContainer.setVisibility(View.GONE);
      }
    });
    webView.loadUrl(webAuthorization.getUrl());
  }
}
