/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.view;

import android.os.Bundle;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.billing.AptoideBilling;
import cm.aptoide.pt.v8engine.billing.Payment;
import cm.aptoide.pt.v8engine.billing.PaymentAnalytics;
import cm.aptoide.pt.v8engine.billing.PaymentConfirmation;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.Purchase;
import cm.aptoide.pt.v8engine.billing.exception.PaymentLocalProcessingRequiredException;
import cm.aptoide.pt.v8engine.billing.exception.PaymentNotAuthorizedException;
import cm.aptoide.pt.v8engine.presenter.PaymentSelector;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
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
  private final AptoideBilling aptoideBilling;
  private final AptoideAccountManager accountManager;
  private final PaymentSelector paymentSelector;
  private final AccountNavigator accountNavigator;
  private final PaymentNavigator paymentNavigator;
  private final ProductProvider productProvider;

  private boolean processingLogin;
  private List<Payment> payments;
  private PaymentAnalytics paymentAnalytics;

  public PaymentPresenter(PaymentView view, AptoideBilling aptoideBilling,
      AptoideAccountManager accountManager, PaymentSelector paymentSelector,
      AccountNavigator accountNavigator, PaymentNavigator paymentNavigator,
      PaymentAnalytics paymentAnalytics, ProductProvider productProvider) {
    this.view = view;
    this.aptoideBilling = aptoideBilling;
    this.accountManager = accountManager;
    this.paymentSelector = paymentSelector;
    this.accountNavigator = accountNavigator;
    this.paymentNavigator = paymentNavigator;
    this.payments = new ArrayList<>();
    this.paymentAnalytics = paymentAnalytics;
    this.productProvider = productProvider;
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.RESUME.equals(event))
        .flatMapSingle(resumed -> productProvider.getProduct())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(product -> Observable.merge(paymentSelection(), cancellationSelection(product))
            .retry()
            .compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> dismiss(throwable));

    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .flatMapSingle(created -> productProvider.getProduct())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(product -> buySelection(product).observeOn(AndroidSchedulers.mainThread())
            .doOnError(throwable -> hideLoadingAndShowError(throwable))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> dismiss(throwable));

    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.DESTROY.equals(event))
        .doOnNext(destroyed -> view.hideAllErrors())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> dismiss(throwable));

    view.getLifecycle()
        .flatMap(event -> loginLifecycle(event))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(loggedIn -> view.showLoading())
        .flatMapSingle(loading -> productProvider.getProduct())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(product -> showProductAndPayments(product).andThen(
            aptoideBilling.getConfirmation(product))
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap(confirmation -> treatLoadingAndGetPurchase(confirmation, product)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
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

  private Observable<Void> cancellationSelection(Product product) {
    return Observable.merge(view.cancellationSelection()
            .flatMap(
                cancelled -> sendCancellationAnalytics(product).andThen(Observable.just(cancelled))),
        view.tapOutsideSelection()
            .flatMap(tappedOutside -> sendTapOutsideAnalytics(product).andThen(
                Observable.just(tappedOutside))))
        .doOnNext(cancelled -> view.dismiss());
  }

  private Completable sendTapOutsideAnalytics(Product product) {
    return paymentSelector.selectedPayment(payments)
        .flatMapCompletable(payment -> Completable.fromAction(
            () -> paymentAnalytics.sendPaymentTapOutsideEvent(product, payment)));
  }

  private Completable sendCancellationAnalytics(Product product) {
    return paymentSelector.selectedPayment(payments)
        .flatMapCompletable(payment -> Completable.fromAction(
            () -> paymentAnalytics.sendPaymentCancelButtonPressedEvent(product, payment)));
  }

  private Observable<Void> buySelection(Product product) {
    return view.buySelection()
        .doOnNext(selected -> view.showLoading())
        .flatMap(selected -> paymentSelector.selectedPayment(getCurrentPayments())
            .flatMapCompletable(selectedPayment -> {
              paymentAnalytics.sendPaymentBuyButtonPressedEvent(product, selectedPayment);
              return processOrNavigate(selectedPayment, product);
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

  private Completable showProductAndPayments(Product product) {
    return aptoideBilling.getPayments(product)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess(payments -> {
          saveCurrentPayments(payments);
          showProduct(product);
        })
        .flatMapCompletable(payments -> {
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

  private Observable<Purchase> treatLoadingAndGetPurchase(PaymentConfirmation confirmation,
      Product product) {
    if (confirmation.isFailed() || confirmation.isNew()) {
      view.hideLoading();
    } else if (confirmation.isPending()) {
      view.showLoading();
    } else if (confirmation.isCompleted()) {
      view.showLoading();
      return aptoideBilling.getPurchase(product)
          .observeOn(AndroidSchedulers.mainThread())
          .toObservable()
          .onErrorResumeNext(throwable -> {
            hideLoadingAndShowError(throwable);
            return Observable.empty();
          });
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

  private Completable processOrNavigate(Payment payment, Product product) {
    return payment.process(product)
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof PaymentNotAuthorizedException) {
            paymentNavigator.navigateToAuthorizationView(payment, product);
            view.hideLoading();
            return Completable.complete();
          }

          if (throwable instanceof PaymentLocalProcessingRequiredException) {
            paymentNavigator.navigateToLocalPaymentView(payment, product);
            view.hideLoading();
            return Completable.complete();
          }

          return Completable.error(throwable);
        });
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
