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
		return paymentRepository.getPayments(context, product)
				.onErrorResumeNext(throwable -> Observable.error(new PaymentFailureException(throwable)));
	}

	public Observable<Boolean> isProductPaymentProcessed(Context context, String paymentType, Product product) {
		return paymentRepository.getPayment(context, paymentType, product)
				.onErrorResumeNext(throwable -> Observable.error(new PaymentFailureException(throwable)))
				.flatMap(payment -> isPaymentConfirmed(payment));
	}

	public Observable<Void> pay(Payment payment) {
		return isPaymentConfirmed(payment).flatMap(confirmed -> {

			if (confirmed) {
				return Observable.just(null);
			}

			return RxPayment.process(payment)
					.flatMap(paymentConfirmation -> savePaymentConfirmation(paymentConfirmation));
		});
	}

	private Observable<Void> savePaymentConfirmation(PaymentConfirmation paymentConfirmation) {
		return paymentRepository.savePaymentConfirmation(paymentConfirmation)
				.onErrorResumeNext(throwable -> Observable.error(new PaymentFailureException(throwable)));
	}

	private Observable<Boolean> isPaymentConfirmed(Payment payment) {
		return paymentRepository.getPaymentConfirmation(payment)
				.map(paymentConfirmation -> true)
				.onErrorResumeNext(throwable -> (throwable instanceof RepositoryItemNotFoundException)? Observable.just(false) : Observable.error
						(new PaymentFailureException(throwable)));
	}
}