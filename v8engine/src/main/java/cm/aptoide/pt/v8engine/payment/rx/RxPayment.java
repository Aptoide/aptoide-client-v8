/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.payment.rx;

import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.Payment;
import rx.Observable;

/**
 * Created by marcelobenites on 8/12/16.
 */
public class RxPayment {

	public static Observable<PaymentConfirmation> process(Payment payment) {
		return Observable.create(new ProcessPaymentOnSubscribe(payment));
	}

	private RxPayment() {
		throw new AssertionError("No instances");
	}
}
