/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.content.Context;

import cm.aptoide.pt.v8engine.payment.exception.PaymentPurchaseNotFoundException;
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
					if (throwable instanceof PaymentPurchaseNotFoundException) {
						return RxPayment.process(payment)
								.flatMap(paymentConfirmation -> paymentRepository.savePaymentConfirmation(paymentConfirmation)
								.flatMap(saved -> getPurchase((AptoideProduct) payment.getProduct())));
					}
					return Observable.error(throwable);
				}).subscribeOn(Schedulers.computation());
	}

	public Observable<Purchase> getPurchase(AptoideProduct product) {
		return paymentRepository.getPurchase(product)
				.doOnNext(purchase -> paymentRepository.deletePaymentConfirmation(product))
				.onErrorResumeNext(throwable -> {
					if (throwable instanceof RepositoryItemNotFoundException) {
						return Observable.error(new PaymentPurchaseNotFoundException("Purchase not found for product " + product.getId()));
					}
					return Observable.error(throwable);
				});
	}
}