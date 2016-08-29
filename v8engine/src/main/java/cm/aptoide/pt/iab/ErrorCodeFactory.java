/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 26/08/2016.
 */

package cm.aptoide.pt.iab;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import cm.aptoide.pt.v8engine.payment.exception.PaymentAlreadyProcessedException;
import cm.aptoide.pt.v8engine.payment.exception.PaymentCancellationException;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;

/**
 * Created by marcelobenites on 8/26/16.
 */
public class ErrorCodeFactory {

	public int create(Throwable throwable) {
		int errorCode = BillingBinder.RESULT_ERROR;

		if (throwable instanceof PaymentCancellationException) {
			errorCode = BillingBinder.RESULT_USER_CANCELED;
		}

		if (throwable instanceof PaymentAlreadyProcessedException) {
			errorCode = BillingBinder.RESULT_ITEM_ALREADY_OWNED;
		}

		if (throwable instanceof IOException) {
			errorCode = BillingBinder.RESULT_SERVICE_UNAVAILABLE;
		}

		if (throwable instanceof LoginException) {
			errorCode = BillingBinder.RESULT_BILLING_UNAVAILABLE;
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
