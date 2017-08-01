/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 26/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.view;

import cm.aptoide.pt.v8engine.billing.exception.BillingException;
import cm.aptoide.pt.v8engine.billing.exception.ProductNotFoundException;
import cm.aptoide.pt.v8engine.billing.exception.PurchaseNotFoundException;
import cm.aptoide.pt.v8engine.billing.external.ExternalBillingBinder;
import java.io.IOException;

public class PaymentThrowableCodeMapper {

  public int map(Throwable throwable) {
    int errorCode = ExternalBillingBinder.RESULT_ERROR;

    if (throwable instanceof IOException) {
      errorCode = ExternalBillingBinder.RESULT_SERVICE_UNAVAILABLE;
    }

    if (throwable instanceof ProductNotFoundException) {
      errorCode = ExternalBillingBinder.RESULT_ITEM_UNAVAILABLE;
    }

    if (throwable instanceof IllegalArgumentException) {
      errorCode = ExternalBillingBinder.RESULT_DEVELOPER_ERROR;
    }

    if (throwable instanceof PurchaseNotFoundException) {
      errorCode = ExternalBillingBinder.RESULT_ITEM_NOT_OWNED;
    }

    return errorCode;
  }

  public Throwable map(int errorCode) {

    Throwable throwable = new BillingException("Unknown error code " + errorCode);

    if (errorCode == ExternalBillingBinder.RESULT_SERVICE_UNAVAILABLE) {
      throwable = new IOException();
    }

    if (errorCode == ExternalBillingBinder.RESULT_ITEM_UNAVAILABLE) {
      throwable = new ProductNotFoundException();
    }

    if (errorCode == ExternalBillingBinder.RESULT_DEVELOPER_ERROR) {
      throwable = new IllegalArgumentException();
    }

    if (errorCode == ExternalBillingBinder.RESULT_ITEM_NOT_OWNED) {
      throwable = new PurchaseNotFoundException();
    }

    return throwable;
  }
}
