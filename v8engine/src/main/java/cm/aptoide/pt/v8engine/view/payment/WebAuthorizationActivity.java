/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 26/01/2017.
 */

package cm.aptoide.pt.v8engine.view.payment;

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
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.payment.AptoidePay;
import cm.aptoide.pt.v8engine.payment.Payer;
import cm.aptoide.pt.v8engine.payment.products.ParcelableProduct;
import cm.aptoide.pt.v8engine.presenter.WebAuthorizationPresenter;
import cm.aptoide.pt.v8engine.repository.PaymentAuthorizationFactory;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.presenter.WebAuthorizationView;
import cm.aptoide.pt.v8engine.view.ActivityView;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;

/**
 * Created by marcelobenites on 11/11/16.
 */
public class WebAuthorizationActivity extends ActivityView implements WebAuthorizationView {

  private static final String EXTRA_PAYMENT_ID =
      "cm.aptoide.pt.v8engine.payment.providers.boacompra.intent.extra.PAYMENT_ID";
  private static final String EXTRA_PRODUCT =
      "cm.aptoide.pt.v8engine.payment.providers.boacompra.intent.extra.PRODUCT";
  private WebView webView;
  private AptoidePay aptoidePay;
  private int paymentId;
  private View progressBarContainer;
  private AlertDialog unknownErrorDialog;
  private PublishRelay<Void> mainUrlSubject;
  private PublishRelay<Void> redirectUrlSubject;

  public static Intent getIntent(Context context, int paymentId, ParcelableProduct product) {
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
      final ParcelableProduct product = getIntent().getParcelableExtra(EXTRA_PRODUCT);
      final AptoideAccountManager accountManager =
          ((V8Engine) getApplicationContext()).getAccountManager();
      aptoidePay = new AptoidePay(RepositoryFactory.getPaymentConfirmationRepository(this, product),
          RepositoryFactory.getPaymentAuthorizationRepository(this),
          new PaymentAuthorizationFactory(this),
          RepositoryFactory.getPaymentRepository(this, product),
          RepositoryFactory.getProductRepository(this, product), new Payer(accountManager));

      webView = (WebView) findViewById(R.id.activity_boa_compra_authorization_web_view);
      webView.getSettings().setJavaScriptEnabled(true);
      webView.setWebChromeClient(new WebChromeClient());
      progressBarContainer = findViewById(R.id.activity_web_authorization_preogress_bar);
      unknownErrorDialog = new AlertDialog.Builder(this).setMessage(R.string.having_some_trouble)
          .setPositiveButton(android.R.string.ok, (dialog, which) -> {
            finish();
          })
          .create();
      mainUrlSubject = PublishRelay.create();
      redirectUrlSubject = PublishRelay.create();
      attachPresenter(new WebAuthorizationPresenter(this, aptoidePay, product, paymentId),
          savedInstanceState);
    } else {
      throw new IllegalStateException("Web payment urls must be provided");
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    ((ViewGroup) webView.getParent()).removeView(webView);
    webView.setWebViewClient(null);
    webView.destroy();
    unknownErrorDialog.dismiss();
  }

  @Override public void showLoading() {
    progressBarContainer.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    progressBarContainer.setVisibility(View.GONE);
  }

  @Override public void showUrl(String mainUrl, String redirectUrl) {
    webView.setWebViewClient(new WebViewClient() {

      @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (url.equals(redirectUrl)) {
          redirectUrlSubject.call(null);
        }
      }

      @Override public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (url.equals(mainUrl)) {
          mainUrlSubject.call(null);
        }
      }
    });
    webView.loadUrl(mainUrl);
  }

  @Override public Observable<Void> redirect() {
    return redirectUrlSubject;
  }

  @Override public Observable<Void> urlLoad() {
    return mainUrlSubject;
  }

  @Override public void dismiss() {
    finish();
  }

  @Override public void showErrorAndDismiss() {
    unknownErrorDialog.show();
  }
}
