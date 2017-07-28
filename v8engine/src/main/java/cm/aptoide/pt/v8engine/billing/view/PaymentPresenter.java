/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.view;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.billing.Billing;
import cm.aptoide.pt.v8engine.billing.BillingAnalytics;
import cm.aptoide.pt.v8engine.billing.PaymentMethod;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.exception.PaymentMethodNotAuthorizedException;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import java.io.IOException;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;

public class PaymentPresenter implements Presenter {

  private static final int PAYER_AUTHORIZATION_REQUEST_CODE = 2001;

  private final PaymentView view;
  private final Billing billing;
  private final BillingNavigator navigator;
  private final ProductProvider productProvider;
  private final BillingAnalytics analytics;

  public PaymentPresenter(PaymentView view, Billing billing, BillingNavigator navigator,
      BillingAnalytics analytics, ProductProvider productProvider) {
    this.view = view;
    this.billing = billing;
    this.navigator = navigator;
    this.analytics = analytics;
    this.productProvider = productProvider;
  }

  @Override public void present() {

    onViewCreatedNavigateToPayerAuthentication();

    onViewCreatedHandlePayerAuthenticationResult();

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

  private void onViewCreatedNavigateToPayerAuthentication() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> billing.getPayer()
            .isAuthenticated()
            .first())
        .filter(authenticated -> !authenticated)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> navigator.navigateToPayerAuthenticationForResult(
            PAYER_AUTHORIZATION_REQUEST_CODE))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> navigator.popPaymentViewWithResult(throwable));
  }

  private void onViewCreatedHandlePayerAuthenticationResult() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> navigator.payerAuthenticationResults(PAYER_AUTHORIZATION_REQUEST_CODE))
        .filter(authenticated -> !authenticated)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> navigator.popPaymentViewWithResult())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> navigator.popPaymentViewWithResult(throwable));
  }

  private void onViewCreatedShowPaymentInformation() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> billing.getPayer()
            .isAuthenticated())
        .filter(authenticated -> authenticated)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> view.showPaymentLoading())
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
          navigator.popPaymentViewWithResult(throwable);
        });
  }

  private void onViewCreatedCheckPurchase() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> billing.getPayer()
            .isAuthenticated())
        .filter(authenticated -> authenticated)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> view.showTransactionLoading())
        .flatMapSingle(loading -> productProvider.getProduct())
        .flatMap(product -> billing.getTransaction(product)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(transaction -> {
              if (transaction.isPending() || transaction.isCompleted() || transaction.isUnknown()) {
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
        .subscribe(purchase -> navigator.popPaymentViewWithResult(purchase), throwable -> {
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
                .doOnCompleted(() -> navigator.popPaymentViewWithResult()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> navigator.popPaymentViewWithResult(throwable));
  }

  private void handleCancellationEvent() {
    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(product -> view.cancellationSelection())
        .flatMapCompletable(
            product -> sendCancellationAnalytics().observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> navigator.popPaymentViewWithResult()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> navigator.popPaymentViewWithResult());
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
        }, throwable -> navigator.popPaymentViewWithResult(throwable));
  }

  private void handleBuyEvent() {
    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .flatMap(__ -> view.buySelection()
            .doOnNext(buySelection -> view.showBuyLoading())
            .flatMapSingle(selection -> productProvider.getProduct())
            .flatMapCompletable(product -> billing.getSelectedPaymentMethod(product)
                .doOnSuccess(payment -> analytics.sendPaymentBuyButtonPressedEvent(product,
                    payment.getName()))
                .flatMapCompletable(payment -> billing.processPayment(payment.getId(), product)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnCompleted(() -> view.hideBuyLoading())
                    .onErrorResumeNext(
                        throwable -> navigateToAuthorizationView(product, payment, throwable))))
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(throwable -> {
              view.hideBuyLoading();
              showError(throwable);
            })
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> navigator.popPaymentViewWithResult(throwable));
  }

  private Completable navigateToAuthorizationView(Product product, PaymentMethod payment,
      Throwable throwable) {
    if (throwable instanceof PaymentMethodNotAuthorizedException) {
      navigator.navigateToTransactionAuthorizationView(payment, product);
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
            .doOnSuccess(payment -> analytics.sendPaymentCancelButtonPressedEvent(product,
                payment.getName()))
            .toCompletable());
  }

  private Completable sendTapOutsideAnalytics() {
    return productProvider.getProduct()
        .flatMapCompletable(product -> billing.getSelectedPaymentMethod(product)
            .doOnSuccess(
                payment -> analytics.sendPaymentTapOutsideEvent(product, payment.getName()))
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
}
