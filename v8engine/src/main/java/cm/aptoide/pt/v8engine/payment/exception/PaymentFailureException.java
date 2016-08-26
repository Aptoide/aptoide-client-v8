/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.v8engine.payment.exception;

/**
 * Created by marcelobenites on 8/10/16.
 */
public class PaymentFailureException extends PaymentException {

	public PaymentFailureException(Throwable throwable) {
		super(throwable);
	}

	public PaymentFailureException(String message) {
		super(message);
	}
}