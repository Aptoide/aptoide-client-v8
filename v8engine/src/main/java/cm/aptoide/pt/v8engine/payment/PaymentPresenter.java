/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 19/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.List;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.payment.exception.PaymentCancellationException;
import cm.aptoide.pt.v8engine.view.PaymentView;
import cm.aptoide.pt.v8engine.view.View;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by marcelobenites on 8/19/16.
 */
public class PaymentPresenter implements Presenter {

	private static final String EXTRA_CURRENT_PAYMENT_TYPE = "cm.aptoide.pt.v8engine.payment.extra.CURRENT_PAYMENT_TYPE";
	private static final String EXTRA_IS_PROCESSING_PAYMENT = "cm.aptoide.pt.v8engine.payment.extra.IS_PROCESSING_PAYMENT";
	private static final String EXTRA_IS_PROCESSING_LOGIN = "cm.aptoide.pt.v8engine.payment.extra.IS_PROCESSING_LOGIN";

	private final PaymentView view;
	private final PaymentManager paymentManager;
	private final Product product;

	private String currentPaymentType;
	private boolean isProcessingPayment;
	private boolean isProcessingLogin;

	public PaymentPresenter(PaymentView view, PaymentManager paymentManager, Product product) {
		this.view = view;
		this.paymentManager = paymentManager;
		this.product = product;
	}

	@Override
	public void present() {

		view.getLifecycle()
				.filter(event -> View.Event.RESUME.equals(event))
				.flatMap(resumed -> Observable.merge(login(), cancellationSelection()))
				.compose(view.bindUntilEvent(View.Event.PAUSE))
				.subscribe();

		view.getLifecycle()
				.filter(event -> View.Event.CREATE.equals(event))
				.flatMap(created -> pay())
				.compose(view.bindUntilEvent(View.Event.DESTROY))
				.subscribe();
	}

	@Override
	public void saveState(Bundle state) {
		state.putBoolean(EXTRA_IS_PROCESSING_PAYMENT, isProcessingPayment);
		state.putBoolean(EXTRA_IS_PROCESSING_LOGIN, isProcessingLogin);
		state.putString(EXTRA_CURRENT_PAYMENT_TYPE, currentPaymentType);
	}

	@Override
	public void restoreState(Bundle state) {
		this.currentPaymentType = state.getString(EXTRA_CURRENT_PAYMENT_TYPE);
		this.isProcessingPayment = state.getBoolean(EXTRA_IS_PROCESSING_PAYMENT);
		this.isProcessingLogin = state.getBoolean(EXTRA_IS_PROCESSING_LOGIN);
	}

	private Observable<Void> login() {
		return currentLoginState()
				.onErrorResumeNext(AptoideAccountManager.login(view.getContext())
						.doOnSubscribe(() -> saveLoginState())
						.map(success -> true))
				.<Void>flatMap(loggedIn -> (loggedIn)? Observable.just(null): Observable.error(new IllegalStateException("Not logged In. Payment can not be " +
						"processed!")))
				.doOnNext(loggedIn -> clearLoginState())
				.doOnError(throwable -> dismissWithFailureAndClearLoginState())
				.onErrorReturn(throwable -> null);
	}

	private Observable<Void> pay() {
		return currentPaymentState()
				.doOnSubscribe(() -> showProductAndShowLoading(product))
				.onErrorResumeNext(throwable -> paymentManager.getProductPayments(view.getContext(), product)
						.observeOn(AndroidSchedulers.mainThread())
						.doOnNext(payments -> removeLoadingAndShowPayments(payments))
						.filter(payments -> !payments.isEmpty())
						.flatMap(payments -> paymentSelection()))
				.doOnError(throwable -> view.dismissWithFailure())
				.doOnNext(paid -> view.dismissWithSuccess())
				.onErrorReturn(throwable -> null);
	}

	@NonNull
	private Observable<Void> paymentSelection() {
		return view.paymentSelection()
				.doOnNext(payment -> showLoadingAndSavePaymentState(payment))
				.flatMap(payment -> paymentManager.pay(payment))
				.doOnNext(success -> removeLoadingAndClearPaymentState())
				.retryWhen(errors -> errors
						.observeOn(AndroidSchedulers.mainThread())
						.doOnNext(throwable -> removeLoadingAndClearPaymentState())
						.flatMap(throwable -> {
							if (throwable instanceof PaymentCancellationException) {
								return Observable.just(throwable);
							}
							return Observable.error(throwable);
						}));
	}

	private void showProductAndShowLoading(Product product) {
		view.showLoading();
		view.showProduct(product);
	}

	private void removeLoadingAndShowPayments(List<Payment> payments) {
		view.removeLoading();
		if (payments.isEmpty()) {
			view.showPaymentsNotFoundMessage();
		} else {
			view.showPayments(payments);
		}
	}

	private void showLoadingAndSavePaymentState(Payment payment) {
		view.showLoading();
		this.currentPaymentType = payment.getType();
		this.isProcessingPayment = true;
	}

	private void removeLoadingAndClearPaymentState() {
		view.removeLoading();
		this.currentPaymentType = null;
		this.isProcessingPayment = false;
	}

	private void dismissWithFailureAndClearLoginState() {
		clearLoginState();
		view.dismissWithFailure();
	}

	private boolean clearLoginState() {
		return isProcessingLogin = false;
	}

	private boolean saveLoginState() {
		return isProcessingLogin = true;
	}

	private Observable<Boolean> currentLoginState() {
		return Observable.just(isProcessingLogin).flatMap(isProcessingLogin -> {
			if (isProcessingLogin) {
				return Observable.just(AptoideAccountManager.isLoggedIn());
			}
			return Observable.error(new IllegalStateException("No login currently being processed."));
		});
	}

	private Observable<Void> currentPaymentState() {
		return Observable.just(isProcessingPayment)
				.flatMap(isProcessingPayment -> {
					if (isProcessingPayment) {
						return paymentManager.isProductPaymentProcessed(view.getContext(), currentPaymentType, product)
								.flatMap(paid -> paid? Observable.just(null): Observable.error(new IllegalStateException("Processed payment failed.")));
					}
					return Observable.error(new IllegalStateException("Not payment currently being processed."));
				});
	}

	@NonNull
	private Observable<Void> cancellationSelection() {
		return view.cancellationSelection().doOnNext(cancellation -> view.dismissWithCancellation());
	}
}