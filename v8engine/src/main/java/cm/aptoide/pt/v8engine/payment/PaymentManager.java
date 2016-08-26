/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.content.Context;

import java.util.List;

import cm.aptoide.pt.v8engine.payment.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.payment.rx.RxPayment;
import cm.aptoide.pt.v8engine.repository.PaymentRepository;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import lombok.AllArgsConstructor;
import rx.Observable;

/**
 * Created by marcelobenites on 8/12/16.
 */
@AllArgsConstructor
public class PaymentManager {

	private final PaymentRepository paymentRepository;

	public Observable<List<Payment>> getProductPayments(Context context, Product product) {
		return paymentRepository.getPayments(context, product);
	}

	public Observable<Purchase> getPurchase(Context context, String paymentType, Product product) {
		return paymentRepository.getPayment(context, paymentType, product).flatMap(payment -> getPurchase(payment));
	}

	public Observable<Purchase> pay(Payment payment) {
		return getPurchase(payment)
				.onErrorResumeNext(throwable -> {
					if (throwable instanceof RepositoryItemNotFoundException) {
						return RxPayment.process(payment)
								.flatMap(paymentConfirmation -> savePaymentConfirmation(paymentConfirmation)
								.flatMap(saved -> paymentRepository.getPurchase(paymentConfirmation)));
					}
					return Observable.error(throwable);
				});
	}

	private Observable<Purchase> getPurchase(Payment payment) {
		// TODO payment confirmation is stored locally. The user may clean local data and a purchased product may be paid again. We must first check if the
		// purchase exists and fallback to payment confirmation afterwards.
		return paymentRepository.getPaymentConfirmation(payment).flatMap(paymentConfirmation -> paymentRepository.getPurchase(paymentConfirmation));
	}

	private Observable<Void> savePaymentConfirmation(PaymentConfirmation paymentConfirmation) {
		return paymentRepository.savePaymentConfirmation(paymentConfirmation);
	}
}