/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 19/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.payment.exception.PaymentCancellationException;
import cm.aptoide.pt.v8engine.payment.exception.PaymentFailureException;
import cm.aptoide.pt.utils.BroadcastRegisterOnSubscribe;
import cm.aptoide.pt.v8engine.view.PaymentView;
import cm.aptoide.pt.v8engine.view.View;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by marcelobenites on 8/19/16.
 */
public class PaymentPresenter implements Presenter {

	public static final String EXTRA_CURRENT_PAYMENT_TYPE = "cm.aptoide.pt.v8engine.payment.extra.CURRENT_PAYMENT_TYPE";
	public static final String EXTRA_IS_PROCESSING_PAYMENT = "cm.aptoide.pt.v8engine.payment.extra.IS_PROCESSING_PAYMENT";
	private final PaymentView view;
	private final PaymentManager paymentManager;
	private final Product product;

	private String currentPaymentType;
	private boolean isProcessingPayment;

	public PaymentPresenter(PaymentView view, PaymentManager paymentManager, Product product) {
		this.view = view;
		this.paymentManager = paymentManager;
		this.product = product;
	}

	public void present() {

		view.getLifecycle()
				.compose(view.bindUntilEvent(View.Event.PAUSE))
				.filter(event -> View.Event.RESUME.equals(event))
				.flatMap(resume -> cancellationSelection())
				.subscribe();

		view.getLifecycle()
				.compose(view.bindUntilEvent(View.Event.PAUSE))
				.filter(event -> View.Event.RESUME.equals(event))
				.flatMap(resumed -> login())
				.subscribe();

		view.getLifecycle()
				.compose(view.bindUntilEvent(View.Event.DESTROY))
				.filter(event -> View.Event.CREATE.equals(event))
				.doOnNext(create -> view.showProduct(product))
				.doOnNext(create -> view.showLoading())
				.flatMap(event -> getPayments())
				.observeOn(AndroidSchedulers.mainThread())
				.doOnNext(payments -> view.removeLoading())
				.doOnNext(payments -> showPayments(payments))
				.filter(payments -> !payments.isEmpty())
				.flatMap(payments -> paymentSelection())
				.subscribe();
	}

	private void showPayments(List<Payment> payments) {
		if (payments.isEmpty()) {
			view.showPaymentsNotFoundMessage();
		} else {
			view.showPayments(payments);
		}
	}

	private Observable<Void> login() {
		return Observable.<Boolean>fromCallable(() -> AptoideAccountManager.isLoggedIn()).<Void>flatMap(loggedIn -> {
			if (!loggedIn) {
				// TODO this logic should be abstracted by Account Manager. It should expose a callback (Observable, Listener ..etc)
				IntentFilter loginFilter = new IntentFilter(AptoideAccountManager.LOGIN);
				loginFilter.addAction(AptoideAccountManager.LOGIN_CANCELLED);
				return Observable.create(new BroadcastRegisterOnSubscribe(view.getContext(), loginFilter, null, null))
						.doOnSubscribe(() -> AptoideAccountManager.openAccountManager(view.getContext(), false))
						.flatMap(intent -> {
							if (AptoideAccountManager.LOGIN.equals(intent.getAction())) {
								return Observable.just(null);
							} else if (AptoideAccountManager.LOGIN_CANCELLED.equals(intent.getAction())) {
								return Observable.error(new IllegalStateException("User cancelled login. Can not perform payment."));
							} else if (AptoideAccountManager.LOGOUT.equals(intent.getAction())) {
								return Observable.error(new IllegalStateException("User logged out. Can not perform payment."));
							}
							return Observable.empty();
						});
			}
			return Observable.just(null);
		}).doOnError(throwable -> view.dismissWithFailure()).onErrorReturn(throwable -> null);
	}

	@Override
	public void saveState(Bundle state) {
		state.putBoolean(EXTRA_IS_PROCESSING_PAYMENT, isProcessingPayment);
		state.putString(EXTRA_CURRENT_PAYMENT_TYPE, currentPaymentType);
	}

	@Override
	public void restoreState(Bundle state) {
		this.currentPaymentType = state.getString(EXTRA_CURRENT_PAYMENT_TYPE);
		this.isProcessingPayment = state.getBoolean(EXTRA_IS_PROCESSING_PAYMENT);
	}

	private void setPaymentProcessingState(Payment payment) {
		this.currentPaymentType = payment.getType();
		this.isProcessingPayment = true;
	}

	@NonNull
	private Observable<List<Payment>> getPayments() {
		return paymentManager.getProductPayments(view.getContext(), product)
				.observeOn(AndroidSchedulers.mainThread())
				.doOnError(throwable -> view.dismissWithFailure())
				.onErrorReturn(throwable -> Collections.emptyList());
	}

	@NonNull
	private Observable<Void> paymentSelection() {
		return currentPaymentSelection()
				.doOnError(throwable -> treatPaymentError(throwable))
				.onErrorResumeNext(view.paymentSelection())
				.doOnNext(payment -> view.showLoading())
				.doOnNext(payment -> setPaymentProcessingState(payment))
				.flatMap(payment -> paymentManager.pay(payment))
				.doOnNext(success -> view.dismissWithSuccess())
				.retryWhen(errors -> errors
						.observeOn(AndroidSchedulers.mainThread())
						.doOnNext(throwable -> treatPaymentError(throwable)));
	}

	private void treatPaymentError(Throwable throwable) {
		if (throwable instanceof PaymentCancellationException) {
			view.showPaymentCancellationError();
		} else if (throwable instanceof PaymentFailureException) {
			view.showPaymentFailureError();
		}
		view.removeLoading();
	}

	private Observable<Payment> currentPaymentSelection() {
		return Observable.just(isProcessingPayment).flatMap(isProcessingPayment -> {
			if (isProcessingPayment) {
				return paymentManager.getProductPayment(view.getContext(), currentPaymentType, product);
			}
			return Observable.error(new IllegalStateException("Not payment currently being processed."));
		});
	}

	@NonNull
	private Observable<Void> cancellationSelection() {
		return view.cancellationSelection().doOnNext(cancellation -> view.dismissWithCancellation());
	}
}