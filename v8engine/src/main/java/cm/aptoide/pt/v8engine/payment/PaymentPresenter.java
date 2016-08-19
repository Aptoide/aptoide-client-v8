/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 19/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import cm.aptoide.pt.v8engine.payment.exception.PaymentCancellationException;
import cm.aptoide.pt.v8engine.payment.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.repository.PaymentRepository;
import cm.aptoide.pt.v8engine.view.PaymentView;
import cm.aptoide.pt.v8engine.view.View;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by marcelobenites on 8/19/16.
 */
public class PaymentPresenter {

	private final PaymentView view;
	private final PaymentManager paymentManager;
	private final PaymentRepository paymentRepository;
	private final Product product;

	public PaymentPresenter(PaymentView view, PaymentManager paymentManager, PaymentRepository paymentRepository, Product product) {
		this.view = view;
		this.paymentManager = paymentManager;
		this.paymentRepository = paymentRepository;
		this.product = product;
	}

	public void present() {

		view.getLifecycle()
				.compose(view.bindUntilEvent(View.Event.DESTROY))
				.filter(event -> View.Event.RESUME.equals(event))
				.flatMap(resume -> view.cancellationSelection().doOnNext(cancellation -> view.dismissWithCancellation()))
				.subscribe();

		view.getLifecycle()
				.compose(view.bindUntilEvent(View.Event.DESTROY))
				.filter(event -> View.Event.CREATE.equals(event))
				.doOnNext(create -> view.showProduct(product))
				.doOnNext(create -> view.showLoading())
				.flatMap(event -> paymentRepository.getPayments(view.getContext(), product)
									.observeOn(AndroidSchedulers.mainThread())
									.doOnError(throwable -> view.dismissWithFailure())
									.onErrorReturn(throwable -> Collections.emptyList()))
				.observeOn(AndroidSchedulers.mainThread())
				.doOnNext(payments -> view.removeLoading())
				.doOnNext(payments -> view.showPayments(payments))
				.flatMap(payments -> view.paymentSelection().flatMap(payment -> paymentManager.pay(payment, product))
										.doOnNext(success -> view.dismissWithSuccess())
										.retryWhen(errors -> errors.doOnNext(throwable -> {
											if (throwable instanceof PaymentCancellationException) {
												view.showPaymentCancellationError();
											} else if (throwable instanceof PaymentFailureException) {
												view.showPaymentFailureError();
											}
										})))
				.subscribe();
	}
}
