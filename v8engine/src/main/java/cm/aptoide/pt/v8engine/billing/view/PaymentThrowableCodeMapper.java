/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 26/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.view;

import cm.aptoide.pt.v8engine.billing.exception.PaymentCancellationException;
import cm.aptoide.pt.v8engine.billing.exception.PaymentException;
import cm.aptoide.pt.v8engine.billing.inapp.BillingBinder;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.io.IOException;
import javax.security.auth.login.LoginException;

public class PaymentThrowableCodeMapper {

  public int map(Throwable throwable) {
    int errorCode = BillingBinder.RESULT_ERROR;

    if (throwable instanceof PaymentCancellationException || throwable instanceof LoginException) {
      errorCode = BillingBinder.RESULT_USER_CANCELLED;
    }

    if (throwable instanceof IOException) {
      errorCode = BillingBinder.RESULT_SERVICE_UNAVAILABLE;
    }

    if (throwable instanceof RepositoryItemNotFoundException) {
      errorCode = BillingBinder.RESULT_ITEM_UNAVAILABLE;
    }

    if (throwable instanceof RepositoryIllegalArgumentException) {
      errorCode = BillingBinder.RESULT_DEVELOPER_ERROR;
    }

    return errorCode;
  }

  public Throwable map(int errorCode) {

    Throwable throwable = new PaymentException("Unknown error code " + errorCode);

    if (errorCode == BillingBinder.RESULT_USER_CANCELLED) {
      throwable = new PaymentCancellationException();
    }

    if (errorCode == BillingBinder.RESULT_SERVICE_UNAVAILABLE) {
      throwable = new IOException();
    }

    if (errorCode == BillingBinder.RESULT_ITEM_UNAVAILABLE) {
      throwable = new RepositoryItemNotFoundException();
    }

    if (errorCode == BillingBinder.RESULT_DEVELOPER_ERROR) {
      throwable = new RepositoryIllegalArgumentException();
    }

    return throwable;
  }
}
