/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 26/08/2016.
 */

package cm.aptoide.pt.v8engine.payment.view;

import android.content.Intent;
import cm.aptoide.pt.iab.BillingBinder;
import cm.aptoide.pt.iab.ErrorCodeFactory;
import cm.aptoide.pt.v8engine.payment.Purchase;
import java.io.IOException;

/**
 * Created by marcelobenites on 8/26/16.
 */
public class PurchaseIntentFactory {

  private final ErrorCodeFactory codeFactory;

  public PurchaseIntentFactory(ErrorCodeFactory codeFactory) {
    this.codeFactory = codeFactory;
  }

  public Intent create(Purchase purchase) {
    Intent intent;

    try {
      intent = new Intent();
      intent.putExtra(BillingBinder.INAPP_PURCHASE_DATA, purchase.getData());
      intent.putExtra(BillingBinder.RESPONSE_CODE, BillingBinder.RESULT_OK);

      if (purchase.getSignature() != null) {
        intent.putExtra(BillingBinder.INAPP_DATA_SIGNATURE, purchase.getSignature());
      }
    } catch (IOException e) {
      intent = create(e);
    }
    return intent;
  }

  public Intent create(Throwable throwable) {
    return new Intent().putExtra(BillingBinder.RESPONSE_CODE, codeFactory.create(throwable));
  }

  public Intent createFromCancellation() {
    return new Intent().putExtra(BillingBinder.RESPONSE_CODE, BillingBinder.RESULT_USER_CANCELLED);
  }
}
