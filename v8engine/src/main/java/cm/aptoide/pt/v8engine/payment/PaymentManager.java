/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import cm.aptoide.pt.v8engine.payment.handler.PaymentConfirmationHandler;
import cm.aptoide.pt.v8engine.payment.rx.RxPayment;
import lombok.AllArgsConstructor;
import rx.Observable;

/**
 * Created by marcelobenites on 8/12/16.
 */
@AllArgsConstructor
public class PaymentManager {

	private final PaymentConfirmationHandler paymentConfirmationHandler;

	public Observable<Void> pay(Payment payment, Product product) {
		return paymentConfirmationHandler.isHandled(payment).flatMap(handled -> {

			if (handled) {
				return Observable.just(null);
			}

			return RxPayment.process(payment)
					.flatMap(paymentConfirmation -> paymentConfirmationHandler.handle(paymentConfirmation));
		});
	}
}