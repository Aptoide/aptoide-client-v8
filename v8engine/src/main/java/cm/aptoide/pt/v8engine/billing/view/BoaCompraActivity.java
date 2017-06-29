/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 26/01/2017.
 */

package cm.aptoide.pt.v8engine.billing.view;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.rx.RxAlertDialog;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;

public class BoaCompraActivity extends ProductActivity implements BoaCompraView {

  private WebView webView;
  private View progressBarContainer;
  private RxAlertDialog unknownErrorDialog;
  private PublishRelay<Void> mainUrlSubject;
  private PublishRelay<Void> redirectUrlSubject;
  private PublishRelay<Void> backButtonSelectionSubject;
  private ClickHandler clickHandler;

  @SuppressLint("SetJavaScriptEnabled") @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_web_authorization);

    webView = (WebView) findViewById(R.id.activity_boa_compra_authorization_web_view);
    webView.getSettings()
        .setJavaScriptEnabled(true);
    webView.setWebChromeClient(new WebChromeClient());
    progressBarContainer = findViewById(R.id.activity_web_authorization_preogress_bar);
    unknownErrorDialog =
        new RxAlertDialog.Builder(this).setMessage(R.string.all_message_general_error)
            .setPositiveButton(R.string.ok)
            .build();
    mainUrlSubject = PublishRelay.create();
    redirectUrlSubject = PublishRelay.create();
    backButtonSelectionSubject = PublishRelay.create();
    clickHandler = () -> {
      backButtonSelectionSubject.call(null);
      return false;
    };
    registerClickHandler(clickHandler);

    attachPresenter(new BoaCompraPresenter(this, ((V8Engine) getApplicationContext()).getBilling(),
        getIntent().getIntExtra(EXTRA_PAYMENT_ID, 0),
        ((V8Engine) getApplicationContext()).getPaymentAnalytics(),
        ((V8Engine) getApplicationContext()).getPaymentSyncScheduler(),
        ProductProvider.fromBundle(((V8Engine) getApplicationContext()).getBilling(),
            getIntent().getExtras())), savedInstanceState);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    ((ViewGroup) webView.getParent()).removeView(webView);
    webView.setWebViewClient(null);
    webView.destroy();
    unknownErrorDialog.dismiss();
    unregisterClickHandler(clickHandler);
  }

  @Override public void showLoading() {
    progressBarContainer.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    progressBarContainer.setVisibility(View.GONE);
  }

  @Override public void loadBoaCompraConsentWebsite(String mainUrl, String redirectUrl) {
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

  @Override public Observable<Void> backToStoreEvent() {
    return redirectUrlSubject;
  }

  @Override public Observable<Void> backButtonSelection() {
    return backButtonSelectionSubject;
  }

  @Override public Observable<Void> boaCompraConsentWebsiteLoaded() {
    return mainUrlSubject;
  }

  @Override public void dismiss() {
    finish();
  }

  @Override public void showError() {
    unknownErrorDialog.show();
  }

  @Override public Observable<Void> errorDismissedEvent() {
    return unknownErrorDialog.dismisses()
        .map(dialogInterface -> null);
  }
}
