/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.payment.rx;

import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.exception.PaymentException;
import lombok.AllArgsConstructor;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

/**
 * Created by marcelobenites on 8/12/16.
 */
@AllArgsConstructor
public class ProcessPaymentOnSubscribe implements Observable.OnSubscribe<PaymentConfirmation> {

	private final Payment payment;

	@Override
	public void call(Subscriber<? super PaymentConfirmation> subscriber) {

		subscriber.add(Subscriptions.create(() -> payment.removeListener()));

		payment.process(new Payment.PaymentConfirmationListener() {
			@Override
			public void onSuccess(PaymentConfirmation paymentConfirmation) {
				if (!subscriber.isUnsubscribed()) {
					subscriber.onNext(paymentConfirmation);
					subscriber.onCompleted();
				}
			}

			@Override
			public void onError(PaymentException exception) {
				if (!subscriber.isUnsubscribed()) {
					subscriber.onError(exception);
				}
			}
		});
	}
}
