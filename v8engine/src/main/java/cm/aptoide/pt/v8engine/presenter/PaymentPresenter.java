/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/08/2016.
 */

package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.List;

import javax.security.auth.login.LoginException;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.PaymentManager;
import cm.aptoide.pt.v8engine.payment.Purchase;
import cm.aptoide.pt.v8engine.payment.exception.PaymentCancellationException;
import cm.aptoide.pt.v8engine.payment.product.AptoideProduct;
import cm.aptoide.pt.v8engine.view.PaymentView;
import cm.aptoide.pt.v8engine.view.View;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by marcelobenites on 8/19/16.
 */
public class PaymentPresenter implements Presenter {

	private static final String EXTRA_CURRENT_PAYMENT_TYPE = "cm.aptoide.pt.v8engine.payment.extra.CURRENT_PAYMENT_TYPE";
	private static final String EXTRA_IS_PROCESSING_PAYMENT = "cm.aptoide.pt.v8engine.payment.extra.IS_PROCESSING_PAYMENT";
	private static final String EXTRA_IS_PROCESSING_LOGIN = "cm.aptoide.pt.v8engine.payment.extra.IS_PROCESSING_LOGIN";

	private final PaymentView view;
	private final PaymentManager paymentManager;
	private final AptoideProduct product;

	private String currentPaymentType;
	private boolean isProcessingPayment;
	private boolean isProcessingLogin;

	public PaymentPresenter(PaymentView view, PaymentManager paymentManager, AptoideProduct product) {
		this.view = view;
		this.paymentManager = paymentManager;
		this.product = product;
	}

	@Override
	public void present() {

		view.getLifecycle()
				.filter(event -> View.Event.RESUME.equals(event))
				.flatMap(resumed -> Observable.merge(login(), cancellationSelection()))
				.compose(view.bindUntilEvent(View.Event.DESTROY))
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
		return getProcessingLoginResult()
				.onErrorResumeNext(AptoideAccountManager.login(view.getContext())
						.doOnSubscribe(() -> saveLoginState())
						.map(success -> true))
				.<Void>flatMap(loggedIn -> (loggedIn)? Observable.just(null): Observable.error(new LoginException("Not logged In. Payment can not be " +
						"processed!")))
				.doOnNext(loggedIn -> clearLoginState())
				.doOnError(throwable -> clearLoginStateAndDismiss(throwable))
				.onErrorReturn(throwable -> null)
				.subscribeOn(Schedulers.computation());
	}

	private Observable<Purchase> pay() {
		return getProcessingPaymentPurchase()
				.doOnSubscribe(() -> showProductAndShowLoading(product))
				.flatMap(purchase -> {
					if (purchase == null) {
						return paymentManager.getProductPayments(view.getContext(), product)
								.observeOn(AndroidSchedulers.mainThread())
								.doOnNext(payments -> removeLoadingAndShowPayments(payments))
								.filter(payments -> !payments.isEmpty())
								.flatMap(payments -> paymentSelection());
					}
					return Observable.just(purchase);
				})
				.doOnError(throwable -> removeLoadingAndDismiss(throwable))
				.doOnNext(purchase -> removeLoadingAndDismiss(purchase))
				.onErrorReturn(throwable -> null)
				.subscribeOn(AndroidSchedulers.mainThread());
	}

	@NonNull
	private Observable<Purchase> paymentSelection() {
		return view.paymentSelection()
				.doOnNext(payment -> showLoadingAndSavePaymentState(payment))
				.flatMap(payment -> paymentManager.pay(payment))
				.observeOn(AndroidSchedulers.mainThread())
				.doOnNext(purchase -> removeLoadingAndClearPaymentState())
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

	private void showProductAndShowLoading(AptoideProduct product) {
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
		savePaymentState(payment);
	}

	private void removeLoadingAndClearPaymentState() {
		view.removeLoading();
		clearPaymentState();
	}

	private void savePaymentState(Payment payment) {
		this.currentPaymentType = payment.getType();
		this.isProcessingPayment = true;
	}

	private void clearPaymentState() {
		this.currentPaymentType = null;
		this.isProcessingPayment = false;
	}

	private void clearLoginStateAndDismiss(Throwable throwable) {
		clearLoginState();
		dismiss(throwable);
	}

	private void removeLoadingAndDismiss(Throwable throwable) {
		view.removeLoading();
		dismiss(throwable);
	}

	private void dismiss(Throwable throwable) {
		view.dismiss(throwable);
	}

	private void removeLoadingAndDismiss(Purchase purchase) {
		view.removeLoading();
		try {
			view.dismiss(purchase);
		} catch (IOException e) {
			dismiss(e);
		}
	}

	private boolean clearLoginState() {
		return isProcessingLogin = false;
	}

	private boolean saveLoginState() {
		return isProcessingLogin = true;
	}

	private Observable<Boolean> getProcessingLoginResult() {
		return Observable.just(isProcessingLogin).flatMap(isProcessingLogin -> {
			if (isProcessingLogin) {
				return Observable.just(AptoideAccountManager.isLoggedIn());
			}
			return Observable.error(new IllegalStateException("No login currently being processed."));
		});
	}

	private Observable<Purchase> getProcessingPaymentPurchase() {
		return Observable.just(isProcessingPayment)
				.flatMap(isProcessingPayment -> {
					if (isProcessingPayment) {
						return paymentManager.getPurchase(product)
								.doOnSubscribe(() -> clearPaymentState());
					}
					return Observable.just(null);
				});
	}

	@NonNull
	private Observable<Void> cancellationSelection() {
		return view.cancellationSelection().doOnNext(cancellation -> view.dismiss()).compose(view.bindUntilEvent(View.Event.PAUSE));
	}
}