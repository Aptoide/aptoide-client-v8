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
import cm.aptoide.pt.v8engine.repository.ProductRepository;
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
  private final ProductRepository productRepository;

  private boolean processingLogin;
  private Payment selectedPayment;
  private List<Payment> payments;

  public PaymentPresenter(PaymentView view, AptoidePay aptoidePay, AptoideProduct product,
      Payer payer, ProductRepository productRepository) {
    this.view = view;
    this.aptoidePay = aptoidePay;
    this.product = product;
    this.payer = payer;
    this.productRepository = productRepository;
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
        .flatMap(loading -> aptoidePay.availablePayments(product)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap(payments -> showProductAndPayments(payments).andThen(
                aptoidePay.getConfirmation(payments)))
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap(confirmation -> {
              if (confirmation.isFailed() || confirmation.isNew()) {
                view.hideLoading();
              } else if (confirmation.isPending()) {
                view.showLoading();
              } else if (confirmation.isCompleted()) {
                view.showLoading();
                return productRepository.getPurchase(product).toObservable();
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
      showProduct(product);
      if (payments.isEmpty()) {
        view.hideLoading();
        view.showPaymentsNotFoundMessage();
        return Completable.complete();
      } else {
        return showPaymentsAndSelectDefault(payments);
      }
    });
  }

  private Completable showPaymentsAndSelectDefault(List<Payment> payments) {
    return convertToViewModel(payments).doOnSuccess(
        paymentViewModels -> showPayments(payments, paymentViewModels))
        .flatMap(paymentViewModels -> selectDefaultPayment(paymentViewModels))
        .toCompletable();
  }

  private Single<List<PaymentView.PaymentViewModel>> convertToViewModel(List<Payment> payments) {
    return Observable.from(payments)
        .map(payment -> convertToPaymentViewModel(payment))
        .toList()
        .toSingle();
  }

  private void showPayments(List<Payment> payments,
      List<PaymentView.PaymentViewModel> paymentViewModels) {
    view.showPayments(paymentViewModels);
    this.payments.clear();
    this.payments.addAll(payments);
  }

  private Observable<Void> paymentSelection() {
    return view.paymentSelection()
        .flatMap(
            paymentViewModel -> saveSelectedPayment(paymentViewModel).toObservable()).<Void>map(
            selectedPayment -> null);
  }

  private Single<Payment> saveSelectedPayment(PaymentView.PaymentViewModel payment) {
    return getSelectedPayment(payments, payment).doOnSuccess(
        selectedPayment -> PaymentPresenter.this.selectedPayment = selectedPayment);
  }

  private void hideLoadingAndNavigateToAuthorizationView(Payment payment) {
    view.hideLoading();
    view.navigateToAuthorizationView(payment.getId(), product);
  }

  private Observable<Void> buySelection() {
    return view.buySelection().doOnNext(selected -> view.showLoading()).<Void>flatMap(selected -> {
      if (selectedPayment.isAuthorized()) {
        return aptoidePay.process(selectedPayment).toObservable();
      }
      return aptoidePay.initiate(selectedPayment)
          .observeOn(AndroidSchedulers.mainThread())
          .doOnCompleted(() -> hideLoadingAndNavigateToAuthorizationView(selectedPayment))
          .toObservable();
    });
  }

  private Observable<Void> cancellationSelection() {
    return view.cancellationSelection().doOnNext(cancellation -> view.dismiss());
  }

  private void showProduct(AptoideProduct product) {
    view.showProduct(product);
  }

  private PaymentView.PaymentViewModel convertToPaymentViewModel(Payment payment) {
    return new PaymentView.PaymentViewModel(payment.getId(), payment.getName(),
        payment.getDescription(), payment.getPrice().getAmount(),
        payment.getPrice().getCurrencySymbol());
  }

  private Single<Payment> getSelectedPayment(List<Payment> payments,
      PaymentView.PaymentViewModel selectedPaymentViewModel) {
    return Observable.from(payments)
        .first(payment -> payment.getId() == selectedPaymentViewModel.getId())
        .toSingle();
  }

  private Single<PaymentView.PaymentViewModel> selectDefaultPayment(
      List<PaymentView.PaymentViewModel> payments) {
    return Observable.from(payments)
        .first(payment -> isDefaultPayment(payment))
        .doOnNext(defaultPayment -> view.selectPayment(defaultPayment))
        .toSingle();
  }

  private boolean isDefaultPayment(PaymentView.PaymentViewModel payment) {
    if (selectedPayment != null && selectedPayment.getId() == payment.getId()) {
      return true;
    }
    return selectedPayment == null && payment.getId() == 1;
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
