/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 26/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.view;

import android.content.Intent;
import cm.aptoide.pt.v8engine.billing.inapp.BillingBinder;
import cm.aptoide.pt.v8engine.billing.Purchase;
import java.io.IOException;

public class PurchaseIntentConverter {

  private final ErrorCodeFactory codeFactory;

  public PurchaseIntentConverter(ErrorCodeFactory codeFactory) {
    this.codeFactory = codeFactory;
  }

  public Intent convert(Purchase purchase) {
    Intent intent;

    try {
      intent = new Intent();
      intent.putExtra(BillingBinder.INAPP_PURCHASE_DATA, purchase.getData());
      intent.putExtra(BillingBinder.RESPONSE_CODE, BillingBinder.RESULT_OK);

      if (purchase.getSignature() != null) {
        intent.putExtra(BillingBinder.INAPP_DATA_SIGNATURE, purchase.getSignature());
      }
    } catch (IOException e) {
      intent = convert(e);
    }
    return intent;
  }

  public Intent convert(Throwable throwable) {
    return new Intent().putExtra(BillingBinder.RESPONSE_CODE, codeFactory.create(throwable));
  }

  public Intent convertCancellation() {
    return new Intent().putExtra(BillingBinder.RESPONSE_CODE, BillingBinder.RESULT_USER_CANCELLED);
  }
}
