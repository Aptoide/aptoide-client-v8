/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 17/08/2016.
 */

package cm.aptoide.pt.v8engine.payment.handler;

import android.content.Context;

import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import lombok.AllArgsConstructor;
import rx.Observable;

/**
 * Created by marcelobenites on 8/12/16.
 */
@AllArgsConstructor
public class PaymentConfirmationHandler {

	private final Context context;

	public Observable<Boolean> isHandled(Payment payment) {
		return Observable.just(false);
	}

	public Observable<Void> handle(PaymentConfirmation paymentConfirmation) {
//		context.startService(PaymentConfirmationService.getIntent(context, paymentConfirmation));
		return Observable.just(null);
	}
}
