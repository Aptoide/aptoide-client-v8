/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/11/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import cm.aptoide.pt.v8engine.R;

/**
 * Created by marcelobenites on 11/11/16.
 */
public class WebPaymentActivity extends AppCompatActivity {

  private static final String EXTRA_AUTHORIZATION_URL =
      "cm.aptoide.pt.v8engine.payment.providers.boacompra.intent.extra.AUTHORIZATION_URL";
  private static final String EXTRA_AUTHORIZATION_RESULT_URL =
      "cm.aptoide.pt.v8engine.payment.providers.boacompra.intent.extra.AUTHORIZATION_RESULT_URL";
  private WebView webView;

  public static Intent getIntent(Context context, String url, String resultUrl) {
    final Intent intent = new Intent(context, WebPaymentActivity.class);
    intent.putExtra(EXTRA_AUTHORIZATION_URL, url);
    intent.putExtra(EXTRA_AUTHORIZATION_RESULT_URL, resultUrl);
    return intent;
  }

  @SuppressLint("SetJavaScriptEnabled") @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_boa_compra_authorization);

    if (getIntent().hasExtra(EXTRA_AUTHORIZATION_URL) && getIntent().hasExtra(
        EXTRA_AUTHORIZATION_RESULT_URL)) {
      final String url = getIntent().getStringExtra(EXTRA_AUTHORIZATION_URL);
      final String resultUrl = getIntent().getStringExtra(EXTRA_AUTHORIZATION_RESULT_URL);

      webView = (WebView) findViewById(R.id.activity_boa_compra_authorization_web_view);
      webView.getSettings().setJavaScriptEnabled(true);
      webView.setWebChromeClient(new WebChromeClient());
      webView.setWebViewClient(new WebViewClient() {

        @Override public void onPageFinished(WebView view, String url) {
          super.onPageFinished(view, url);
          if (url.equals(resultUrl)) {
            finish();
          }
        }
      });
      webView.loadUrl(url);
    } else {
      throw new IllegalStateException("Web payment urls must be provided");
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    ((ViewGroup) webView.getParent()).removeView(webView);
    webView.setWebViewClient(null);
    webView.destroy();
  }
}