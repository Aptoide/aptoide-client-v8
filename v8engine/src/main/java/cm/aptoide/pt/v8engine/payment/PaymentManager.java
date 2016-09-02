/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.content.Context;

import java.util.List;

import cm.aptoide.pt.v8engine.payment.exception.PaymentAlreadyProcessedException;
import cm.aptoide.pt.v8engine.payment.product.AptoideProduct;
import cm.aptoide.pt.v8engine.payment.rx.RxPayment;
import cm.aptoide.pt.v8engine.repository.PaymentRepository;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import lombok.AllArgsConstructor;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by marcelobenites on 8/12/16.
 */
@AllArgsConstructor
public class PaymentManager {

	private final PaymentRepository paymentRepository;

	public Observable<List<Payment>> getProductPayments(Context context, AptoideProduct product) {
		return paymentRepository.getPayments(context, product);
	}

	public Observable<Purchase> pay(Payment payment) {
		return getPurchase((AptoideProduct) payment.getProduct())
				.flatMap(purchase -> Observable.<Purchase>error(new PaymentAlreadyProcessedException("Product " + payment.getProduct().getId() + " already " +
						"purchased.")))
				.onErrorResumeNext(throwable -> {
					if (throwable instanceof RepositoryItemNotFoundException) {
						return RxPayment.process(payment)
								.flatMap(paymentConfirmation -> paymentRepository.savePaymentConfirmation(paymentConfirmation)
								.flatMap(saved -> getPurchase((AptoideProduct) payment.getProduct())));
					}
					return Observable.error(throwable);
				}).subscribeOn(Schedulers.computation());
	}

	public Observable<Purchase> getPurchase(AptoideProduct product) {
		// Payment confirmation is stored locally. The user may clean local data and attempt to purchase a product again. We should always check if
		// a purchase exists in server even if its payment confirmation is not stored locally.
		return paymentRepository.getPaymentConfirmation(product)
				.flatMap(paymentConfirmation -> paymentRepository.getPurchase(product))
				.doOnNext(purchase -> paymentRepository.deletePaymentConfirmation(product))
				.onErrorResumeNext(throwable -> {
					if (throwable instanceof RepositoryItemNotFoundException) {
						return paymentRepository.getPurchase(product);
					}
					return Observable.error(throwable);
				});
	}
}