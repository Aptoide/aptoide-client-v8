/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/08/2016.
 */

package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.payment.AptoidePay;
import cm.aptoide.pt.v8engine.payment.Payer;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.Purchase;
import cm.aptoide.pt.v8engine.payment.products.AptoideProduct;
import cm.aptoide.pt.v8engine.view.PaymentView;
import cm.aptoide.pt.v8engine.view.View;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.security.auth.login.LoginException;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by marcelobenites on 8/19/16.
 */
public class PaymentPresenter implements Presenter {

  private static final String EXTRA_IS_PROCESSING_LOGIN =
      "cm.aptoide.pt.v8engine.payment.extra.IS_PROCESSING_LOGIN";

  private final PaymentView view;
  private final AptoidePay aptoidePay;
  private final AptoideProduct product;
  private final Payer payer;
  private final PaymentSelector paymentSelector;

  private boolean processingLogin;
  private List<Payment> payments;

  public PaymentPresenter(PaymentView view, AptoidePay aptoidePay, AptoideProduct product,
      Payer payer, PaymentSelector paymentSelector) {
    this.view = view;
    this.aptoidePay = aptoidePay;
    this.product = product;
    this.payer = payer;
    this.paymentSelector = paymentSelector;
    this.payments = new ArrayList<>();
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.RESUME.equals(event))
        .flatMap(resumed -> Observable.merge(paymentSelection(), cancellationSelection())
            .retry()
            .compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();

    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .flatMap(created -> buySelection().observeOn(AndroidSchedulers.mainThread())
            .doOnError(throwable -> hideLoadingAndShowError(throwable))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();

    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.DESTROY.equals(event))
        .doOnNext(destroyed -> view.hideAllErrors())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();

    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .flatMap(created -> login())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(loggedIn -> view.showLoading())
        .flatMap(loading -> aptoidePay.payments(product)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap(payments -> showProductAndPayments(payments).andThen(
                aptoidePay.confirmation(payments)))
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap(confirmation -> {
              if (confirmation.isFailed() || confirmation.isNew()) {
                view.hideLoading();
              } else if (confirmation.isPending()) {
                view.showLoading();
              } else if (confirmation.isCompleted()) {
                view.showLoading();
                return aptoidePay.purchase(product).toObservable();
              }
              return Observable.empty();
            }))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(purchase -> hideLoadingAndDismiss(purchase),
            throwable -> hideLoadingAndDismiss(throwable));
  }

  @Override public void saveState(Bundle state) {
    state.putBoolean(EXTRA_IS_PROCESSING_LOGIN, processingLogin);
  }

  @Override public void restoreState(Bundle state) {
    this.processingLogin = state.getBoolean(EXTRA_IS_PROCESSING_LOGIN);
  }

  private Observable<Void> login() {
    return Observable.defer(() -> {
      if (processingLogin) {
        return Observable.just(payer.isLoggedIn())
            .flatMap(loggedIn -> (loggedIn) ? Observable.just(null) : Observable.error(
                new LoginException("Not logged In. Payment can not be processed!")));
      }
      return payer.login().doOnSubscribe(() -> saveLoginState());
    })
        .doOnNext(loggedIn -> clearLoginState())
        .doOnError(throwable -> clearLoginState())
        .subscribeOn(Schedulers.computation());
  }

  private Completable showProductAndPayments(List<Payment> payments) {
    return Completable.defer(() -> {
      saveCurrentPayments(payments);
      showProduct(product);
      if (payments.isEmpty()) {
        view.hideLoading();
        view.showPaymentsNotFoundMessage();
        return Completable.complete();
      } else {
        return paymentSelector.selectedPayment(payments)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapCompletable(selectedPayment -> showPayments(payments, selectedPayment));
      }
    });
  }

  private Completable showPayments(List<Payment> payments, Payment selectedPayment) {
    return convertToViewModel(payments, selectedPayment)
        .doOnSuccess(paymentViewModels -> view.showPayments(paymentViewModels))
        .toCompletable();
  }

  private Single<List<PaymentView.PaymentViewModel>> convertToViewModel(List<Payment> payments,
      Payment selectedPayment) {
    return Observable.from(payments)
        .map(payment -> convertToPaymentViewModel(payment, payment.getId() == selectedPayment.getId()))
        .toList()
        .toSingle();
  }

  private List<Payment> getCurrentPayments() {
    return payments;
  }

  private void saveCurrentPayments(List<Payment> payments) {
    this.payments.clear();
    this.payments.addAll(payments);
  }

  private Observable<Void> paymentSelection() {
    return view.paymentSelection()
        .flatMap(paymentViewModel -> getPayment(paymentViewModel).flatMapCompletable(
            payment -> paymentSelector.selectPayment(payment)).toObservable());
  }

  private void hideLoadingAndNavigateToAuthorizationView(Payment payment) {
    view.hideLoading();
    view.navigateToAuthorizationView(payment.getId(), product);
  }

  private Observable<Void> buySelection() {
    return view.buySelection()
        .doOnNext(selected -> view.showLoading())
        .flatMap(selected -> paymentSelector.selectedPayment(getCurrentPayments())
            .flatMapCompletable(
                selectedPayment -> processOrNavigateToAuthorization(selectedPayment))
            .toObservable());
  }

  private Completable processOrNavigateToAuthorization(Payment payment) {
    if (payment.isAuthorized()) {
      return aptoidePay.process(payment);
    }
    return aptoidePay.initiate(payment)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnCompleted(() -> hideLoadingAndNavigateToAuthorizationView(payment));
  }

  private Observable<Void> cancellationSelection() {
    return view.cancellationSelection().doOnNext(cancellation -> view.dismiss());
  }

  private void showProduct(AptoideProduct product) {
    view.showProduct(product);
  }

  private PaymentView.PaymentViewModel convertToPaymentViewModel(Payment payment, boolean selected) {
    return new PaymentView.PaymentViewModel(payment.getId(), payment.getName(),
        payment.getDescription(), selected);
  }

  private Single<Payment> getPayment(PaymentView.PaymentViewModel selectedPaymentViewModel) {
    return Observable.from(getCurrentPayments())
        .first(payment -> payment.getId() == selectedPaymentViewModel.getId())
        .toSingle();
  }

  private void hideLoadingAndDismiss(Throwable throwable) {
    view.hideLoading();
    view.dismiss(throwable);
  }

  private void hideLoadingAndShowError(Throwable throwable) {
    view.hideLoading();

    if (throwable instanceof IOException) {
      view.showNetworkError();
    } else {
      view.showUnknownError();
    }
  }

  private void hideLoadingAndDismiss(Purchase purchase) {
    view.hideLoading();
    view.dismiss(purchase);
  }

  private boolean clearLoginState() {
    return processingLogin = false;
  }

  private boolean saveLoginState() {
    return processingLogin = true;
  }
}
