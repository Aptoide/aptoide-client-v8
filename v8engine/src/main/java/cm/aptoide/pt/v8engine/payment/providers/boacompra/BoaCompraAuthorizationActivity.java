/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/11/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers.boacompra;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import cm.aptoide.pt.v8engine.R;

/**
 * Created by marcelobenites on 11/11/16.
 */
public class BoaCompraAuthorizationActivity extends AppCompatActivity {

  public static final String ACTION_AUTHORIZATION_OK =
      "cm.aptoide.pt.v8engine.payment.providers.boacompra.intent.action.AUTHORIZATION_RESULT";
  public static final String ACTION_AUTHORIZATION_CANCELLED =
      "cm.aptoide.pt.v8engine.payment.providers.boacompra.intent.action.AUTHORIZATION_CANCELLED";
  private static final String EXTRA_AUTHORIZATION_URL =
      "cm.aptoide.pt.v8engine.payment.providers.boacompra.intent.extra.AUTHORIZATION_URL";
  private static final String EXTRA_AUTHORIZATION_RESULT_URL =
      "cm.aptoide.pt.v8engine.payment.providers.boacompra.intent.extra.AUTHORIZATION_RESULT_URL";

  public static Intent getIntent(Context context, String url, String resultUrl) {
    final Intent intent = new Intent(context, BoaCompraAuthorizationActivity.class);
    intent.putExtra(EXTRA_AUTHORIZATION_URL, url);
    intent.putExtra(EXTRA_AUTHORIZATION_RESULT_URL, url);
    return intent;
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_boa_compra_authorization);

    if (getIntent().hasExtra(EXTRA_AUTHORIZATION_URL) && getIntent().hasExtra(
        EXTRA_AUTHORIZATION_RESULT_URL)) {
      final String url = getIntent().getStringExtra(EXTRA_AUTHORIZATION_URL);
      final String resultUrl = getIntent().getStringExtra(EXTRA_AUTHORIZATION_RESULT_URL);

      WebView webView = (WebView) findViewById(R.id.activity_boa_compra_authorization_web_view);
      webView.loadUrl(url);
      webView.getSettings().setJavaScriptEnabled(true);
      webView.setWebChromeClient(new WebChromeClient() {
        @Override public void onProgressChanged(WebView view, int progress) {
          super.onProgressChanged(view, progress);
          if (progress == 100 && view.getUrl().equals(resultUrl)) {
            sendBroadcast(new Intent(ACTION_AUTHORIZATION_OK));
            finish();
          }
        }
      });
    }
  }

  @Override public void onBackPressed() {
    super.onBackPressed();
    sendBroadcast(new Intent(ACTION_AUTHORIZATION_CANCELLED));
  }
}
