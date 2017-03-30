/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 26/08/2016.
 */

package cm.aptoide.pt.iab;

import cm.aptoide.pt.v8engine.payment.exception.PaymentCancellationException;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.io.IOException;
import javax.security.auth.login.LoginException;

/**
 * Created by marcelobenites on 8/26/16.
 */
public class ErrorCodeFactory {

  public int create(Throwable throwable) {
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
}
