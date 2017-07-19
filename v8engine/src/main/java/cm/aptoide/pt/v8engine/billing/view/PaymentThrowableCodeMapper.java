/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 26/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.view;

import cm.aptoide.pt.v8engine.billing.exception.PaymentException;
import cm.aptoide.pt.v8engine.billing.external.ExternalBillingBinder;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.io.IOException;

public class PaymentThrowableCodeMapper {

  public int map(Throwable throwable) {
    int errorCode = ExternalBillingBinder.RESULT_ERROR;

    if (throwable instanceof IOException) {
      errorCode = ExternalBillingBinder.RESULT_SERVICE_UNAVAILABLE;
    }

    if (throwable instanceof RepositoryItemNotFoundException) {
      errorCode = ExternalBillingBinder.RESULT_ITEM_UNAVAILABLE;
    }

    if (throwable instanceof RepositoryIllegalArgumentException) {
      errorCode = ExternalBillingBinder.RESULT_DEVELOPER_ERROR;
    }

    return errorCode;
  }

  public Throwable map(int errorCode) {

    Throwable throwable = new PaymentException("Unknown error code " + errorCode);

    if (errorCode == ExternalBillingBinder.RESULT_SERVICE_UNAVAILABLE) {
      throwable = new IOException();
    }

    if (errorCode == ExternalBillingBinder.RESULT_ITEM_UNAVAILABLE) {
      throwable = new RepositoryItemNotFoundException();
    }

    if (errorCode == ExternalBillingBinder.RESULT_DEVELOPER_ERROR) {
      throwable = new RepositoryIllegalArgumentException();
    }

    return throwable;
  }
}
