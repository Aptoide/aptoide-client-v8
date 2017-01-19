/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 23/12/2016.
 */

package cm.aptoide.pt.v8engine.payment.authorizations;

import android.content.Context;
import cm.aptoide.pt.v8engine.payment.Authorization;

/**
 * Created by marcelobenites on 15/11/16.
 */
public class WebAuthorization extends Authorization {

  private final Context context;
  private final String url;
  private final String redirectUrl;

  public WebAuthorization(Context context, int paymentId, String url, String redirectUrl,
      Status status, String payerId) {
    super(paymentId, payerId, status);
    this.context = context;
    this.url = url;
    this.redirectUrl = redirectUrl;
  }

  public String getUrl() {
    return url;
  }

  public String getRedirectUrl() {
    return redirectUrl;
  }

  @Override public void authorize() {
    context.startActivity(WebAuthorizationActivity.getIntent(context, url, redirectUrl));
  }
}
