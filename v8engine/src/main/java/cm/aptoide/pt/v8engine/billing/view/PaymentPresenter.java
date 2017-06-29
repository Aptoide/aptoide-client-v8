/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.view;

import android.os.Bundle;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.billing.Billing;
import cm.aptoide.pt.v8engine.billing.PaymentAnalytics;
import cm.aptoide.pt.v8engine.billing.PaymentMethod;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.exception.PaymentLocalProcessingRequiredException;
import cm.aptoide.pt.v8engine.billing.exception.PaymentMethodNotAuthorizedException;
import cm.aptoide.pt.v8engine.presenter.PaymentMethodSelector;
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
  private final PaymentMethodSelector paymentMethodSelector;
  private final AccountNavigator accountNavigator;
  private final PaymentNavigator paymentNavigator;
  private final ProductProvider productProvider;
  private final PaymentAnalytics paymentAnalytics;

  public PaymentPresenter(PaymentView view, Billing billing, AptoideAccountManager accountManager,
      PaymentMethodSelector paymentMethodSelector, AccountNavigator accountNavigator,
      PaymentNavigator paymentNavigator, PaymentAnalytics paymentAnalytics,
      ProductProvider productProvider) {
    this.view = view;
    this.billing = billing;
    this.accountManager = accountManager;
    this.paymentMethodSelector = paymentMethodSelector;
    this.accountNavigator = accountNavigator;
    this.paymentNavigator = paymentNavigator;
    this.paymentAnalytics = paymentAnalytics;
    this.productProvider = productProvider;
  }

  @Override public void present() {

    onViewCreatedShowLogin();

    onViewCreatedShowPaymentInformation();

    onViewCreatedCheckPurchase();

    handlePaymentSelection();

    handleCancellationSelection();

    handleTapOutsideSelection();

    handleBuySelection();

    onViewDestroyedHideAllErrors();
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
        .doOnNext(__ -> paymentNavigator.popBackStackWithResult())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> paymentNavigator.popBackStackWithResult(throwable));
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
          paymentNavigator.popBackStackWithResult(throwable);
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
            .doOnNext(confirmation -> {
              if (confirmation.isFailed()) {
                view.hideTransactionLoading();
                view.showUnknownError();
              } else if (confirmation.isNew()) {
                view.hideTransactionLoading();
              } else if (confirmation.isPending() || confirmation.isCompleted()) {
                view.showTransactionLoading();
              }
            })
            .first(confirmation -> confirmation.isCompleted())
            .flatMapSingle(confirmation -> billing.getPurchase(product)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(purchase -> view.hideTransactionLoading())
        .subscribe(purchase -> paymentNavigator.popBackStackWithResult(purchase), throwable -> {
          view.hideTransactionLoading();
          showError(throwable);
        });
  }

  private void handleTapOutsideSelection() {
    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(product -> view.tapOutsideSelection())
        .flatMapSingle(cancellation -> productProvider.getProduct())
        .flatMapCompletable(
            product -> sendTapOutsideAnalytics().observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> paymentNavigator.popBackStackWithResult()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> paymentNavigator.popBackStackWithResult(throwable));
  }

  private void handleCancellationSelection() {
    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(product -> view.cancellationSelection())
        .flatMapCompletable(
            product -> sendCancellationAnalytics().observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> paymentNavigator.popBackStackWithResult()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> paymentNavigator.popBackStackWithResult());
  }

  private void handlePaymentSelection() {
    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(created -> view.paymentSelection())
        .flatMapSingle(paymentMethodViewModel -> productProvider.getProduct()
            .flatMap(product -> billing.getPaymentMethods(paymentMethodViewModel.getId(), product)))
        .flatMapCompletable(payment -> paymentMethodSelector.selectPaymentMethod(payment))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> paymentNavigator.popBackStackWithResult(throwable));
  }

  private void handleBuySelection() {
    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .flatMap(__ -> view.buySelection()
            .doOnNext(buySelection -> view.showPaymentLoading())
            .flatMapSingle(selection -> productProvider.getProduct())
            .flatMapCompletable(product -> getSelectedPaymentMethod(product).doOnSuccess(
                payment -> paymentAnalytics.sendPaymentBuyButtonPressedEvent(product,
                    payment.getName()))
                .flatMapCompletable(payment -> billing.processPayment(payment.getId(), product)
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorResumeNext(throwable -> {

                      if (throwable instanceof PaymentMethodNotAuthorizedException) {
                        paymentNavigator.navigateToAuthorizationView(payment, product);
                        view.hidePaymentLoading();
                        return Completable.complete();
                      }

                      if (throwable instanceof PaymentLocalProcessingRequiredException) {
                        paymentNavigator.navigateToLocalPaymentView(payment, product);
                        view.hidePaymentLoading();
                        return Completable.complete();
                      }

                      return Completable.error(throwable);
                    })))
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(throwable -> {
              view.hidePaymentLoading();
              showError(throwable);
            })
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> paymentNavigator.popBackStackWithResult(throwable));
  }

  private void onViewDestroyedHideAllErrors() {
    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.DESTROY.equals(event))
        .doOnNext(destroyed -> view.hideAllErrors())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> paymentNavigator.popBackStackWithResult(throwable));
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
          return showSelectedPaymentMethod(paymentMethods);
        });
  }

  private Completable sendCancellationAnalytics() {
    return productProvider.getProduct()
        .flatMapCompletable(product -> getSelectedPaymentMethod(product).doOnSuccess(
            payment -> paymentAnalytics.sendPaymentCancelButtonPressedEvent(product,
                payment.getName()))
            .toCompletable());
  }

  private Completable sendTapOutsideAnalytics() {
    return productProvider.getProduct()
        .flatMapCompletable(product -> getSelectedPaymentMethod(product).doOnSuccess(
            payment -> paymentAnalytics.sendPaymentTapOutsideEvent(product, payment.getName()))
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

  private Single<PaymentMethod> getSelectedPaymentMethod(Product product) {
    return billing.getPaymentMethods(product)
        .flatMap(paymentMethods -> paymentMethodSelector.selectedPaymentMethod(paymentMethods));
  }

  private Completable showSelectedPaymentMethod(List<PaymentMethod> paymentMethods) {
    return paymentMethodSelector.selectedPaymentMethod(paymentMethods)
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
