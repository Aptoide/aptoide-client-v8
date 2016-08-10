/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

/**
 * Created by marcelobenites on 8/10/16.
 */
public class PaymentConfirmation {
	
	private final Payment payment;
	private final String transactionId;

	public PaymentConfirmation(Payment payment, String transactionId) {
		this.payment = payment;
		this.transactionId = transactionId;
	}

	public Payment getPayment() {
		return payment;
	}

	public String getTransactionId() {
		return transactionId;
	}
}
