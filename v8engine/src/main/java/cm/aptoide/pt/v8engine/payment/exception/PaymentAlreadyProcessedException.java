/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/08/2016.
 */

package cm.aptoide.pt.v8engine.payment.exception;

/**
 * Created by marcelobenites on 8/29/16.
 */
public class PaymentAlreadyProcessedException extends PaymentException {

	public PaymentAlreadyProcessedException(String message) {
		super(message);
	}
}
