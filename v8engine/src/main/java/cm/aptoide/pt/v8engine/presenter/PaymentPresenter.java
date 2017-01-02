/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/08/2016.
 */

package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.AptoidePay;
import cm.aptoide.pt.v8engine.payment.Purchase;
import cm.aptoide.pt.v8engine.payment.products.AptoideProduct;
import cm.aptoide.pt.v8engine.view.PaymentView;
import cm.aptoide.pt.v8engine.view.View;
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

  private boolean isProcessingLogin;
  private final List<Payment> currentPayments;
  private Payment selectedPayment;

  public PaymentPresenter(PaymentView view, AptoidePay aptoidePay, AptoideProduct product) {
    this.view = view;
    this.aptoidePay = aptoidePay;
    this.product = product;
    this.currentPayments = new ArrayList<>();
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.RESUME.equals(event))
        .flatMap(resumed -> cancellationSelection())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();

    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .flatMap(created -> login())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(loggedIn -> showProductAndShowLoading(product))
        .flatMap(loggedIn -> treatOngoingPurchase().switchIfEmpty(
            loadPayments().andThen(Completable.merge(buySelection(), paymentSelection()))
                .toObservable()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  @Override public void saveState(Bundle state) {
    state.putBoolean(EXTRA_IS_PROCESSING_LOGIN, isProcessingLogin);
  }

  @Override public void restoreState(Bundle state) {
    this.isProcessingLogin = state.getBoolean(EXTRA_IS_PROCESSING_LOGIN);
  }

  private Observable<Void> login() {
    return Observable.defer(() -> {
      if (isProcessingLogin) {
        return Observable.just(AptoideAccountManager.isLoggedIn())
            .flatMap(loggedIn -> (loggedIn) ? Observable.just(null) : Observable.error(
                new LoginException("Not logged In. Payment can not be processed!")));
      }
      return AptoideAccountManager.login(view.getContext()).doOnSubscribe(() -> saveLoginState());
    })
        .doOnNext(loggedIn -> clearLoginState())
        .doOnError(throwable -> clearLoginState())
        .subscribeOn(Schedulers.computation());
  }

  private Completable loadPayments() {
    return aptoidePay.availablePayments(view.getContext(), product)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess(payments -> showPaymentsAndRemoveLoading(payments))
        .doOnError(error -> removeLoadingAndDismiss(error))
        .flatMap(payments -> selectDefaultPayment(payments))
        .toCompletable();
}

  private Single<List<Payment>> selectDefaultPayment(List<Payment> payments) {
    return Observable.from(payments).doOnNext(payment -> {
      if (selectedPayment != null && selectedPayment.getId() == payment.getId()) {
        selectPayment(selectedPayment);
      }
      if (selectedPayment == null && payment.getId() == 1) { // PayPal
        selectPayment(payment);
      }
    }).toList().toSingle();
  }

  private Observable<Purchase> treatOngoingPurchase() {
    return aptoidePay.getPurchase(product)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(purchase -> removeLoadingAndDismiss(purchase))
        .doOnError(error -> removeLoadingAndDismiss(error))
        .onErrorResumeNext(throwable -> Observable.empty());
  }

  private Completable buySelection() {
    return view.buySelection()
        .doOnNext(payment -> view.showLoading())
        .flatMap(payment -> aptoidePay.process(selectedPayment).toObservable())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(purchase -> removeLoadingAndDismiss(purchase))
        .doOnError(error -> removeLoadingAndDismiss(error))
        .retry()
        .toCompletable();
  }

  private Completable paymentSelection() {
    return view.paymentSelection()
        .flatMap(paymentId -> Observable.from(currentPayments)
            .filter(payment -> payment.getId() == paymentId))
        .doOnNext(payment -> selectPayment(payment))
        .toCompletable();
  }

  private void showProductAndShowLoading(AptoideProduct product) {
    view.showLoading();
    view.showProduct(product);
  }

  private void showPaymentsAndRemoveLoading(List<Payment> payments) {
    currentPayments.clear();
    currentPayments.addAll(payments);
    if (payments.isEmpty()) {
      view.showPaymentsNotFoundMessage();
    } else {
      view.showPayments(convertToPaymentViewModel(payments));
    }
    view.removeLoading();
  }

  private List<PaymentView.PaymentViewModel> convertToPaymentViewModel(List<Payment> payments) {
    final List<PaymentView.PaymentViewModel> viewModels = new ArrayList<>();
    for (Payment payment : payments) {
      viewModels.add(new PaymentView.PaymentViewModel(payment.getId(), payment.getName(),
          payment.getDescription(), payment.getPrice().getAmount(),
          payment.getPrice().getCurrency()));
    }
    return viewModels;
  }

  private void removeLoadingAndDismiss(Throwable throwable) {
    view.removeLoading();
    view.dismiss(throwable);
  }

  private void removeLoadingAndDismiss(Purchase purchase) {
    view.removeLoading();
    view.dismiss(purchase);
  }

  private void selectPayment(Payment payment) {
    selectedPayment = payment;
    view.markPaymentAsSelected(selectedPayment.getId());
  }

  private boolean clearLoginState() {
    return isProcessingLogin = false;
  }

  private boolean saveLoginState() {
    return isProcessingLogin = true;
  }

  @NonNull private Observable<Void> cancellationSelection() {
    return view.cancellationSelection()
        .doOnNext(cancellation -> view.dismiss())
        .compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE));
  }
}
