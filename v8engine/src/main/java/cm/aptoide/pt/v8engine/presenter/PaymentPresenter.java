/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/08/2016.
 */

package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.payment.AptoidePay;
import cm.aptoide.pt.v8engine.payment.Authorization;
import cm.aptoide.pt.v8engine.payment.Payer;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.Purchase;
import cm.aptoide.pt.v8engine.payment.products.AptoideProduct;
import cm.aptoide.pt.v8engine.repository.ProductRepository;
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
  private final Payer payer;
  private final List<Payment> otherPayments;
  private final ProductRepository productRepository;

  private boolean processingLogin;
  private Payment selectedPayment;
  private boolean otherPaymentsVisible;

  public PaymentPresenter(PaymentView view, AptoidePay aptoidePay, AptoideProduct product,
      Payer payer, ProductRepository productRepository) {
    this.view = view;
    this.aptoidePay = aptoidePay;
    this.product = product;
    this.payer = payer;
    this.productRepository = productRepository;
    this.otherPayments = new ArrayList<>();
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.RESUME.equals(event))
        .flatMap(resumed -> Observable.merge(paymentUseSelection(), otherPaymentsSelection(),
            cancellationSelection()))
        .retry()
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();

    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .flatMap(created -> Observable.merge(buySelection(), paymentRegisterSelection()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(selected -> {
        }, throwable -> hideGlobalAndPaymentsLoadingAndDismiss(throwable));

    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .flatMap(created -> login())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(loggedIn -> showGlobalAndPaymentsLoading())
        .flatMap(loggedIn -> Observable.merge(aptoidePay.getConfirmation(product),
            loadPayments().cast(PaymentConfirmation.class)))
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(paymentConfirmation -> {

          if (paymentConfirmation.isPending()) {
            view.showGlobalLoading();
          } else {
            showProduct(product);
            view.hideGlobalLoading();
          }

          if (paymentConfirmation.isCompleted()) {
            return productRepository.getPurchase(product).toObservable();
          }
          return Observable.empty();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(purchase -> hideGlobalAndPaymentsLoadingAndDismiss(purchase),
            throwable -> hideGlobalAndPaymentsLoadingAndDismiss(throwable));
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

  private Observable<Void> loadPayments() {
    return aptoidePay.availablePayments(product)
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(payments -> {
          if (payments.isEmpty()) {
            view.hidePaymentsLoading();
            view.showPaymentsNotFoundMessage();
            return Observable.empty();
          } else {
            view.hidePaymentsLoading();
            return getDefaultPayment(payments).flatMapCompletable(
                defaultPayment -> showPayments(payments, defaultPayment)).<Void>toObservable();
          }
        });
  }

  private Completable showPayments(List<Payment> allPayments, Payment selectedPayment) {
    return Completable.fromAction(() -> showSelectedPayment(selectedPayment))
        .andThen(Observable.from(allPayments)
            .filter(payment -> payment.getId() != selectedPayment.getId())
            .toList()
            .toSingle()
            .flatMapCompletable(otherPayments -> showOtherPayments(otherPayments)));
  }

  private Observable<Void> paymentUseSelection() {
    return view.usePaymentSelection()
        .flatMap(
            paymentViewModel -> getSelectedPayment(otherPayments, paymentViewModel)).<Void>flatMap(
            selectedPayment -> showPayments(getAllPayments(), selectedPayment).doOnCompleted(
                () -> hideOtherPayments()).toObservable()).compose(
            view.bindUntilEvent(View.LifecycleEvent.PAUSE));
  }

  private List<Payment> getAllPayments() {
    final List<Payment> allPayments = new ArrayList<>(otherPayments.size());
    allPayments.addAll(otherPayments);
    if (selectedPayment != null) {
      allPayments.add(selectedPayment);
    }
    return allPayments;
  }

  private Observable<Void> paymentRegisterSelection() {
    return view.registerPaymentSelection()
        .doOnNext(selection -> view.showGlobalLoading())
        .flatMap(paymentViewModel -> getSelectedPayment(getAllPayments(),
            paymentViewModel))
        .map(payment -> payment.getAuthorization())
        .<Void>flatMap(authorization -> {
            return aptoidePay.initiate(authorization)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> hideGlobalLoadingAndNavigateToAuthorizationView(authorization))
                .toObservable();
        });
  }

  private void  hideGlobalLoadingAndNavigateToAuthorizationView(Authorization authorization) {
    view.hideGlobalLoading();
    view.navigateToAuthorizationView(authorization.getPaymentId(), product);
  }

  private Observable<Void> buySelection() {
    return view.buySelection()
        .doOnNext(payment -> view.showGlobalLoading())
        .flatMap(payment -> aptoidePay.process(selectedPayment).toObservable());
  }

  private Observable<Void> otherPaymentsSelection() {
    return view.otherPaymentsSelection().<Void>flatMap(event -> {
      if (!otherPaymentsVisible) {
        return showPayments(getAllPayments(), selectedPayment).toObservable();
      }
      hideOtherPayments();
      return Observable.empty();
    }).compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE));
  }

  private void hideOtherPayments() {
    view.hideOtherPayments();
    otherPaymentsVisible = false;
  }

  private Completable showOtherPayments(List<Payment> otherPayments) {
    return Observable.from(otherPayments)
        .map(payment -> convertToPaymentViewModel(payment))
        .toList()
        .doOnNext(paymentViewModels -> view.showOtherPayments(paymentViewModels))
        .doOnNext(paymentViewModels -> otherPaymentsVisible = true)
        .doOnCompleted(() -> {
          this.otherPayments.clear();
          this.otherPayments.addAll(otherPayments);
        })
        .toCompletable();
  }

  private Observable<Void> cancellationSelection() {
    return view.cancellationSelection()
        .doOnNext(cancellation -> view.dismiss())
        .compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE));
  }

  private void showProduct(AptoideProduct product) {
    view.showProduct(product);
  }

  private PaymentView.PaymentViewModel convertToPaymentViewModel(Payment payment) {
    return new PaymentView.PaymentViewModel(payment.getId(), payment.getName(),
        payment.getDescription(), payment.getPrice().getAmount(), payment.getPrice().getCurrencySymbol(),
        getPaymentViewStatus(payment));
  }

  private PaymentView.PaymentViewModel.Status getPaymentViewStatus(Payment payment) {

    if (!payment.isAuthorizationRequired()) {
      return PaymentView.PaymentViewModel.Status.USE;
    }

    if (payment.getAuthorization() != null) {
      if (payment.getAuthorization().isAuthorized()) {
        return PaymentView.PaymentViewModel.Status.USE;
      } else if (payment.getAuthorization().isPending()) {
        return PaymentView.PaymentViewModel.Status.APPROVING;
      }
    }

    return PaymentView.PaymentViewModel.Status.REGISTER;
  }

  private void showSelectedPayment(Payment selectedPayment) {
    this.selectedPayment = selectedPayment;
    view.showSelectedPayment(convertToPaymentViewModel(selectedPayment));
  }

  private Observable<Payment> getSelectedPayment(List<Payment> payments,
      PaymentView.PaymentViewModel selectedPaymentViewModel) {
    return Observable.from(payments)
        .first(payment -> payment.getId() == selectedPaymentViewModel.getId());
  }

  private Single<Payment> getDefaultPayment(List<Payment> payments) {
    return Observable.from(payments).first(payment -> isDefaultPayment(payment)).toSingle();
  }

  private boolean isDefaultPayment(Payment payment) {
    if (selectedPayment != null && selectedPayment.getId() == payment.getId()) {
      return true;
    }
    if (selectedPayment == null && payment.getId() == 1) { // PayPal
      return true;
    }
    return false;
  }

  private void showGlobalAndPaymentsLoading() {
    view.showGlobalLoading();
    view.showPaymentsLoading();
  }

  private void hideGlobalAndPaymentsLoadingAndDismiss(Throwable throwable) {
    view.hideGlobalLoading();
    view.hidePaymentsLoading();
    view.dismiss(throwable);
  }

  private void hideGlobalAndPaymentsLoadingAndDismiss(Purchase purchase) {
    view.hideGlobalLoading();
    view.hidePaymentsLoading();
    view.dismiss(purchase);
  }

  private boolean clearLoginState() {
    return processingLogin = false;
  }

  private boolean saveLoginState() {
    return processingLogin = true;
  }
}
