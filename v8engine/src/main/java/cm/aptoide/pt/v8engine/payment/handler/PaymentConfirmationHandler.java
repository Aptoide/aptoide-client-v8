/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 17/08/2016.
 */

package cm.aptoide.pt.v8engine.payment.handler;

import android.content.Context;

import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.repository.PaymentRepository;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import lombok.AllArgsConstructor;
import rx.Observable;

/**
 * Created by marcelobenites on 8/12/16.
 */
@AllArgsConstructor
public class PaymentConfirmationHandler {

	private final Context context;
	private final PaymentRepository paymentRepository;

	public Observable<Boolean> isHandled(Payment payment) {
		return paymentRepository.getPaymentConfirmation(payment)
				.first()
				.flatMap(paymentConfirmation -> syncPaymentConfirmation(paymentConfirmation))
				.map(synced -> true)
				.onErrorResumeNext(throwable -> (throwable instanceof RepositoryItemNotFoundException)? Observable.just(false) : Observable.error(throwable));
	}

	public Observable<Void> handle(PaymentConfirmation paymentConfirmation) {
		return paymentRepository.savePaymentConfirmation(paymentConfirmation)
				.flatMap(saved -> syncPaymentConfirmation(paymentConfirmation));
	}

	private Observable<Void> syncPaymentConfirmation(PaymentConfirmation paymentConfirmation) {
		return paymentRepository.getPaymentConfirmations()
				.doOnSubscribe(() -> context.startService(PaymentConfirmationSyncService.getIntent(context)))
				.filter(paymentConfirmations -> !paymentConfirmations.contains(paymentConfirmation))
				.map(paymentConfirmations -> null);
	}
}