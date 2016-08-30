/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.v8engine.payment.exception;

/**
 * Created by marcelobenites on 8/10/16.
 */
public class PaymentException extends Exception {
	
	public PaymentException(String message) {
		super(message);
	}

	public PaymentException(Throwable throwable) {
		super(throwable);
	}
}
