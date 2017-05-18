/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/08/2016.
 */

package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.payment.AptoidePay;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.PaymentAnalytics;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.Purchase;
import cm.aptoide.pt.v8engine.view.account.AccountNavigator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.security.auth.login.LoginException;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by marcelobenites on 8/19/16.
 */
public class PaymentPresenter implements Presenter {

  private static final String EXTRA_IS_PROCESSING_LOGIN =
      "cm.aptoide.pt.v8engine.payment.extra.IS_PROCESSING_LOGIN";

  private final PaymentView view;
  private final AptoidePay aptoidePay;
  private final Product product;
  private final AptoideAccountManager accountManager;
  private final PaymentSelector paymentSelector;
  private final AccountNavigator accountNavigator;

  private boolean processingLogin;
  private List<Payment> payments;
  private PaymentAnalytics paymentAnalytics;

  public PaymentPresenter(PaymentView view, AptoidePay aptoidePay, Product product,
      AptoideAccountManager accountManager, PaymentSelector paymentSelector,
      AccountNavigator accountNavigator, PaymentAnalytics paymentAnalytics) {
    this.view = view;
    this.aptoidePay = aptoidePay;
    this.product = product;
    this.accountManager = accountManager;
    this.paymentSelector = paymentSelector;
    this.accountNavigator = accountNavigator;
    this.payments = new ArrayList<>();
    this.paymentAnalytics = paymentAnalytics;
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.RESUME.equals(event))
        .flatMap(resumed -> Observable.merge(paymentSelection(), cancellationSelection())
            .retry()
            .compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          CrashReport.getInstance()
              .log(err);
        });

    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .flatMap(created -> buySelection().observeOn(AndroidSchedulers.mainThread())
            .doOnError(throwable -> hideLoadingAndShowError(throwable))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          CrashReport.getInstance()
              .log(err);
        });

    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.DESTROY.equals(event))
        .doOnNext(destroyed -> view.hideAllErrors())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          CrashReport.getInstance()
              .log(err);
        });

    view.getLifecycle()
        .flatMap(event -> loginLifecycle(event))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(loggedIn -> view.showLoading())
        .flatMap(loading -> Observable.combineLatest(aptoidePay.payments()
            .observeOn(AndroidSchedulers.mainThread()), aptoidePay.confirmation(product)
            .observeOn(AndroidSchedulers.mainThread()), (payments, confirmation) -> {
          return showProductAndPayments(payments).<Purchase>andThen(
              treatLoadingAndGetPurchase(confirmation));
        })).<Purchase>flatMap(observable -> observable).compose(
        view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(purchase -> dismiss(purchase), throwable -> dismiss(throwable));
  }

  @Override public void saveState(Bundle state) {
    state.putBoolean(EXTRA_IS_PROCESSING_LOGIN, processingLogin);
  }

  @Override public void restoreState(Bundle state) {
    this.processingLogin = state.getBoolean(EXTRA_IS_PROCESSING_LOGIN);
  }

  private Observable<Void> paymentSelection() {
    return view.paymentSelection()
        .flatMap(paymentViewModel -> getPayment(paymentViewModel).flatMapCompletable(
            payment -> paymentSelector.selectPayment(payment))
            .toObservable());
  }

  private Observable<Void> cancellationSelection() {
    return Observable.merge(view.cancellationSelection()
            .flatMap(cancelled -> sendCancellationAnalytics().andThen(Observable.just(cancelled))),
        view.tapOutsideSelection()
            .flatMap(
                tappedOutside -> sendTapOutsideAnalytics().andThen(Observable.just(tappedOutside))))
        .doOnNext(cancelled -> view.dismiss());
  }

  private Completable sendTapOutsideAnalytics() {
    return paymentSelector.selectedPayment(payments)
        .flatMapCompletable(payment -> Completable.fromAction(
            () -> paymentAnalytics.sendPaymentTapOutsideEvent(product, payment)));
  }

  private Completable sendCancellationAnalytics() {
    return paymentSelector.selectedPayment(payments)
        .flatMapCompletable(payment -> Completable.fromAction(
            () -> paymentAnalytics.sendPaymentCancelButtonPressedEvent(product, payment)));
  }

  private Observable<Void> buySelection() {
    return view.buySelection()
        .doOnNext(selected -> view.showLoading())
        .flatMap(selected -> paymentSelector.selectedPayment(getCurrentPayments())
            .flatMapCompletable(selectedPayment -> {
              paymentAnalytics.sendPaymentBuyButtonPressedEvent(product, selectedPayment);
              return processOrNavigateToAuthorization(selectedPayment);
            })
            .toObservable());
  }

  private void hideLoadingAndShowError(Throwable throwable) {
    view.hideLoading();

    if (throwable instanceof IOException) {
      view.showNetworkError();
    } else {
      view.showUnknownError();
    }
  }

  private Observable<Void> loginLifecycle(View.LifecycleEvent event) {

    if (event.equals(View.LifecycleEvent.CREATE) || event.equals(View.LifecycleEvent.RESUME)) {

      if (accountManager.isLoggedIn()) {
        if (processingLogin || event.equals(View.LifecycleEvent.CREATE)) {
          processingLogin = false;
          return Observable.just(null);
        }
        return Observable.empty();
      }

      if (processingLogin) {
        processingLogin = false;
        return Observable.error(new LoginException("Not logged In. Payment can not be processed!"));
      }

      if (event.equals(View.LifecycleEvent.RESUME)) {
        processingLogin = true;
        accountNavigator.navigateToLoginView();
      }
    }
    return Observable.empty();
  }

  private Completable showProductAndPayments(List<Payment> payments) {
    return Completable.defer(() -> {
      saveCurrentPayments(payments);
      showProduct(product);
      if (payments.isEmpty()) {
        view.showPaymentsNotFoundMessage();
        return Completable.complete();
      } else {
        return paymentSelector.selectedPayment(payments)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapCompletable(selectedPayment -> showPayments(payments, selectedPayment));
      }
    });
  }

  private Observable<Purchase> treatLoadingAndGetPurchase(PaymentConfirmation confirmation) {
    if (confirmation.isFailed() || confirmation.isNew()) {
      view.hideLoading();
    } else if (confirmation.isPending()) {
      view.showLoading();
    } else if (confirmation.isCompleted()) {
      view.showLoading();
      return aptoidePay.purchase(product)
          .toObservable();
    }
    return Observable.empty();
  }

  private void dismiss(Purchase purchase) {
    view.dismiss(purchase);
  }

  private void dismiss(Throwable throwable) {
    view.dismiss(throwable);
  }

  private Single<Payment> getPayment(PaymentView.PaymentViewModel selectedPaymentViewModel) {
    return Observable.from(getCurrentPayments())
        .first(payment -> payment.getId() == selectedPaymentViewModel.getId())
        .toSingle();
  }

  private List<Payment> getCurrentPayments() {
    return payments;
  }

  private Completable processOrNavigateToAuthorization(Payment payment) {
    if (payment.getAuthorization()
        .isAuthorized()) {
      return aptoidePay.process(payment, product);
    }
    return aptoidePay.initiate(payment)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnCompleted(() -> view.navigateToAuthorizationView(payment.getId(), product));
  }

  private void saveCurrentPayments(List<Payment> payments) {
    this.payments.clear();
    this.payments.addAll(payments);
  }

  private void showProduct(Product product) {
    view.showProduct(product);
  }

  private Completable showPayments(List<Payment> payments, Payment selectedPayment) {
    return convertToViewModel(payments, selectedPayment).doOnSuccess(
        paymentViewModels -> view.showPayments(paymentViewModels))
        .toCompletable();
  }

  private Single<List<PaymentView.PaymentViewModel>> convertToViewModel(List<Payment> payments,
      Payment selectedPayment) {
    return Observable.from(payments)
        .map(payment -> convertToPaymentViewModel(payment,
            payment.getId() == selectedPayment.getId()))
        .toList()
        .toSingle();
  }

  private PaymentView.PaymentViewModel convertToPaymentViewModel(Payment payment,
      boolean selected) {
    return new PaymentView.PaymentViewModel(payment.getId(), payment.getName(),
        payment.getDescription(), selected);
  }
}
