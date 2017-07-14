/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.view;

import android.os.Bundle;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.billing.Billing;
import cm.aptoide.pt.v8engine.billing.BillingAnalytics;
import cm.aptoide.pt.v8engine.billing.PaymentMethod;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.exception.PaymentMethodNotAuthorizedException;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import cm.aptoide.pt.v8engine.view.account.AccountNavigator;
import java.io.IOException;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;

public class PaymentPresenter implements Presenter {

  private static final int LOGIN_REQUEST_CODE = 2001;

  private final PaymentView view;
  private final Billing billing;
  private final AptoideAccountManager accountManager;
  private final AccountNavigator accountNavigator;
  private final BillingNavigator billingNavigator;
  private final ProductProvider productProvider;
  private final BillingAnalytics billingAnalytics;

  public PaymentPresenter(PaymentView view, Billing billing, AptoideAccountManager accountManager,
      AccountNavigator accountNavigator, BillingNavigator billingNavigator,
      BillingAnalytics billingAnalytics, ProductProvider productProvider) {
    this.view = view;
    this.billing = billing;
    this.accountManager = accountManager;
    this.accountNavigator = accountNavigator;
    this.billingNavigator = billingNavigator;
    this.billingAnalytics = billingAnalytics;
    this.productProvider = productProvider;
  }

  @Override public void present() {

    onViewCreatedShowLogin();

    onViewCreatedShowPaymentInformation();

    onViewCreatedCheckPurchase();

    handlePaymentMethodSelectionEvent();

    handleCancellationEvent();

    handleTapOutsideEvent();

    handleBuyEvent();
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }

  private void onViewCreatedShowLogin() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMapSingle(__ -> accountManager.accountStatus()
            .first()
            .toSingle())
        .filter(account -> !account.isLoggedIn())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(__ -> accountNavigator.navigateToLoginViewForResult(LOGIN_REQUEST_CODE))
        .filter(loggedIn -> !loggedIn)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> billingNavigator.popPaymentViewWithResult())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> billingNavigator.popPaymentViewWithResult(throwable));
  }

  private void onViewCreatedShowPaymentInformation() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMapSingle(created -> userLoggedIn())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(loggedIn -> view.showPaymentLoading())
        .flatMapSingle(loading -> productProvider.getProduct())
        .flatMapCompletable(product -> billing.getPaymentMethods(product)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapCompletable(payments -> showPaymentInformation(product, payments))
            .doOnCompleted(() -> view.hidePaymentLoading()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(__ -> {
        }, throwable -> {
          view.hidePaymentLoading();
          billingNavigator.popPaymentViewWithResult(throwable);
        });
  }

  private void onViewCreatedCheckPurchase() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMapSingle(created -> userLoggedIn())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(loggedIn -> view.showTransactionLoading())
        .flatMapSingle(loading -> productProvider.getProduct())
        .flatMap(product -> billing.getTransaction(product)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(transaction -> {
              if (transaction.isPending() || transaction.isCompleted()) {
                view.showTransactionLoading();
              } else {
                view.hideTransactionLoading();
              }
            })
            .first(transaction -> transaction.isCompleted())
            .flatMapSingle(__ -> billing.getPurchase(product)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(purchase -> view.hideTransactionLoading())
        .subscribe(purchase -> billingNavigator.popPaymentViewWithResult(purchase), throwable -> {
          view.hideTransactionLoading();
          showError(throwable);
        });
  }

  private void handleTapOutsideEvent() {
    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(product -> view.tapOutsideSelection())
        .flatMapSingle(cancellation -> productProvider.getProduct())
        .flatMapCompletable(
            product -> sendTapOutsideAnalytics().observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> billingNavigator.popPaymentViewWithResult()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> billingNavigator.popPaymentViewWithResult(throwable));
  }

  private void handleCancellationEvent() {
    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(product -> view.cancellationSelection())
        .flatMapCompletable(
            product -> sendCancellationAnalytics().observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> billingNavigator.popPaymentViewWithResult()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> billingNavigator.popPaymentViewWithResult());
  }

  private void handlePaymentMethodSelectionEvent() {
    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(created -> view.paymentSelection())
        .flatMapCompletable(paymentMethodViewModel -> productProvider.getProduct()
            .flatMapCompletable(
                product -> billing.selectPaymentMethod(paymentMethodViewModel.getId(), product)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> billingNavigator.popPaymentViewWithResult(throwable));
  }

  private void handleBuyEvent() {
    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .flatMap(__ -> view.buySelection()
            .doOnNext(buySelection -> view.showTransactionLoading())
            .flatMapSingle(selection -> productProvider.getProduct())
            .flatMapCompletable(product -> billing.getSelectedPaymentMethod(product)
                .doOnSuccess(payment -> billingAnalytics.sendPaymentBuyButtonPressedEvent(product,
                    payment.getName()))
                .flatMapCompletable(payment -> billing.processPayment(payment.getId(), product)
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorResumeNext(
                        throwable -> navigateToAuthorizationView(product, payment, throwable))))
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(throwable -> {
              view.hideTransactionLoading();
              showError(throwable);
            })
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> billingNavigator.popPaymentViewWithResult(throwable));
  }

  private Completable navigateToAuthorizationView(Product product, PaymentMethod payment,
      Throwable throwable) {
    if (throwable instanceof PaymentMethodNotAuthorizedException) {
      billingNavigator.navigateToTransactionAuthorizationView(payment, product);
      return Completable.complete();
    }
    return Completable.error(throwable);
  }

  private void showError(Throwable throwable) {
    if (throwable instanceof IOException) {
      view.showNetworkError();
    } else {
      view.showUnknownError();
    }
  }

  private Completable showPaymentInformation(Product product, List<PaymentMethod> paymentMethods) {
    return getPaymentMethodsViewModels(paymentMethods).doOnSuccess(paymentViewModels -> {
      view.showProduct(product);
      if (paymentViewModels.isEmpty()) {
        view.showPaymentsNotFoundMessage();
      } else {
        view.showPayments(paymentViewModels);
      }
    })
        .flatMapCompletable(paymentViewModels -> {
          if (paymentViewModels.isEmpty()) {
            return Completable.complete();
          }
          return showSelectedPaymentMethod(product);
        });
  }

  private Completable sendCancellationAnalytics() {
    return productProvider.getProduct()
        .flatMapCompletable(product -> billing.getSelectedPaymentMethod(product)
            .doOnSuccess(payment -> billingAnalytics.sendPaymentCancelButtonPressedEvent(product,
                payment.getName()))
            .toCompletable());
  }

  private Completable sendTapOutsideAnalytics() {
    return productProvider.getProduct()
        .flatMapCompletable(product -> billing.getSelectedPaymentMethod(product)
            .doOnSuccess(
                payment -> billingAnalytics.sendPaymentTapOutsideEvent(product, payment.getName()))
            .toCompletable());
  }

  private Single<List<PaymentView.PaymentMethodViewModel>> getPaymentMethodsViewModels(
      List<PaymentMethod> paymentMethods) {
    return Observable.from(paymentMethods)
        .map(payment -> mapToPaymentMethodViewModel(payment))
        .toList()
        .toSingle();
  }

  private PaymentView.PaymentMethodViewModel mapToPaymentMethodViewModel(
      PaymentMethod paymentMethod) {
    return new PaymentView.PaymentMethodViewModel(paymentMethod.getId(), paymentMethod.getName(),
        paymentMethod.getDescription());
  }

  private Completable showSelectedPaymentMethod(Product paymentMethods) {
    return billing.getSelectedPaymentMethod(paymentMethods)
        .observeOn(AndroidSchedulers.mainThread())
        .map(payment -> mapToPaymentMethodViewModel(payment))
        .doOnSuccess(payment -> view.selectPayment(payment))
        .toCompletable();
  }

  private Single<Account> userLoggedIn() {
    return accountManager.accountStatus()
        .first(account -> account.isLoggedIn())
        .toSingle();
  }
}
