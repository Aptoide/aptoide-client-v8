/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.view;

import cm.aptoide.pt.v8engine.billing.inapp.BillingBinder;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;

public class PurchaseErrorCodeFactory extends ErrorCodeFactory {

  @Override public int create(Throwable throwable) {
    if (throwable instanceof RepositoryItemNotFoundException) {
      return BillingBinder.RESULT_ITEM_NOT_OWNED;
    }
    return super.create(throwable);
  }
}
