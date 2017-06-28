/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 26/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.view;

import cm.aptoide.pt.v8engine.billing.exception.PaymentException;
import cm.aptoide.pt.v8engine.billing.inapp.InAppBillingBinder;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.io.IOException;
import javax.security.auth.login.LoginException;

public class PaymentThrowableCodeMapper {

  public int map(Throwable throwable) {
    int errorCode = InAppBillingBinder.RESULT_ERROR;

    if (throwable instanceof IOException) {
      errorCode = InAppBillingBinder.RESULT_SERVICE_UNAVAILABLE;
    }

    if (throwable instanceof RepositoryItemNotFoundException) {
      errorCode = InAppBillingBinder.RESULT_ITEM_UNAVAILABLE;
    }

    if (throwable instanceof RepositoryIllegalArgumentException) {
      errorCode = InAppBillingBinder.RESULT_DEVELOPER_ERROR;
    }

    return errorCode;
  }

  public Throwable map(int errorCode) {

    Throwable throwable = new PaymentException("Unknown error code " + errorCode);

    if (errorCode == InAppBillingBinder.RESULT_SERVICE_UNAVAILABLE) {
      throwable = new IOException();
    }

    if (errorCode == InAppBillingBinder.RESULT_ITEM_UNAVAILABLE) {
      throwable = new RepositoryItemNotFoundException();
    }

    if (errorCode == InAppBillingBinder.RESULT_DEVELOPER_ERROR) {
      throwable = new RepositoryIllegalArgumentException();
    }

    return throwable;
  }
}
