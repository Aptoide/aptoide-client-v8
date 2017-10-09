/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/08/2016.
 */

package cm.aptoide.pt.billing.view;

import android.os.Bundle;
import cm.aptoide.pt.billing.Billing;
import cm.aptoide.pt.billing.BillingAnalytics;
import cm.aptoide.pt.billing.PaymentService;
import cm.aptoide.pt.billing.Product;
import cm.aptoide.pt.billing.exception.ServiceNotAuthorizedException;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import java.io.IOException;
import java.util.List;
import rx.Completable;
import rx.android.schedulers.AndroidSchedulers;

public class PaymentPresenter implements Presenter {

  private static final int CUSTOMER_AUTHORIZATION_REQUEST_CODE = 2001;

  private final PaymentView view;
  private final Billing billing;
  private final BillingNavigator navigator;
  private final BillingAnalytics analytics;
  private final String merchantName;
  private final String sku;
  private final String payload;

  public PaymentPresenter(PaymentView view, Billing billing, BillingNavigator navigator,
      BillingAnalytics analytics, String merchantName, String sku, String payload) {
    this.view = view;
    this.billing = billing;
    this.navigator = navigator;
    this.analytics = analytics;
    this.merchantName = merchantName;
    this.sku = sku;
    this.payload = payload;
  }

  @Override public void present() {

    onViewCreatedNavigateToCustomerAuthentication();

    onViewCreatedHandleCustomerAuthenticationResult();

    onViewCreatedShowPaymentInformation();

    onViewCreatedCheckPurchase();

    handleSelectServiceEvent();

    handleCancelEvent();

    handleBuyEvent();
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }

  private void onViewCreatedNavigateToCustomerAuthentication() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> view.showPaymentLoading())
        .flatMap(__ -> billing.getCustomer()
            .isAuthenticated()
            .first())
        .doOnNext(authenticated -> analytics.sendCustomerAuthenticatedEvent(authenticated))
        .filter(authenticated -> !authenticated)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> navigator.navigateToCustomerAuthenticationForResult(
            CUSTOMER_AUTHORIZATION_REQUEST_CODE))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> navigator.popViewWithResult(throwable));
  }

  private void onViewCreatedHandleCustomerAuthenticationResult() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> navigator.customerAuthenticationResults(CUSTOMER_AUTHORIZATION_REQUEST_CODE))
        .doOnNext(authenticated -> analytics.sendCustomerAuthenticationResultEvent(authenticated))
        .filter(authenticated -> !authenticated)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> navigator.popViewWithResult())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> navigator.popViewWithResult(throwable));
  }

  private void onViewCreatedShowPaymentInformation() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> billing.getCustomer()
            .isAuthenticated())
        .filter(authenticated -> authenticated)
        .flatMapSingle(loading -> billing.getProduct(merchantName, sku))
        .flatMapCompletable(product -> billing.getPaymentServices()
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapCompletable(payments -> showPaymentInformation(product, payments))
            .doOnCompleted(() -> view.hidePaymentLoading()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(__ -> {
        }, throwable -> {
          view.hidePaymentLoading();
          navigator.popViewWithResult(throwable);
        });
  }

  private void onViewCreatedCheckPurchase() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> billing.getCustomer()
            .isAuthenticated())
        .filter(authenticated -> authenticated)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> view.showPurchaseLoading())
        .flatMap(__ -> billing.getPurchase(merchantName, sku)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(purchase -> {
              if (purchase.isPending()) {
                view.showPurchaseLoading();
              } else {
                view.hidePurchaseLoading();
              }

              if (purchase.isCompleted()) {
                analytics.sendPaymentSuccessEvent();
                navigator.popViewWithResult(purchase);
              }

              if (purchase.isFailed()) {
                analytics.sendPaymentErrorEvent();
                view.showUnknownError();
              }
            }))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(__ -> {
        }, throwable -> {
          view.hidePurchaseLoading();
          showError(throwable);
        });
  }

  private void handleCancelEvent() {
    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(product -> view.cancelEvent())
        .flatMapCompletable(
            product -> sendPaymentCancelAnalytics().observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> navigator.popViewWithResult()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> navigator.popViewWithResult());
  }

  private void handleSelectServiceEvent() {
    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(created -> view.selectServiceEvent())
        .flatMapCompletable(
            serviceViewModel -> billing.selectService(serviceViewModel.getId()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> navigator.popViewWithResult(throwable));
  }

  private void handleBuyEvent() {
    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .flatMap(__ -> view.buyEvent()
            .doOnNext(buySelection -> view.showBuyLoading())
            .flatMapSingle(selection -> billing.getProduct(merchantName, sku))
            .flatMapCompletable(product -> billing.getSelectedService()
                .doOnSuccess(
                    payment -> analytics.sendPaymentViewBuyEvent(product, payment.getType()))
                .flatMapCompletable(payment -> billing.processPayment(merchantName, sku, payload)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnCompleted(() -> {
                      analytics.sendAuthorizationSuccessEvent(payment.getType());
                      view.hideBuyLoading();
                    })
                    .onErrorResumeNext(
                        throwable -> navigateToAuthorizationView(payment, throwable))))
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(throwable -> {
              view.hideBuyLoading();
              showError(throwable);
            })
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> navigator.popViewWithResult(throwable));
  }

  private Completable navigateToAuthorizationView(PaymentService service, Throwable throwable) {
    if (throwable instanceof ServiceNotAuthorizedException) {
      navigator.navigateToTransactionAuthorizationView(merchantName, service, sku);
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

  private Completable showPaymentInformation(Product product,
      List<PaymentService> paymentServices) {
    view.showProduct(product);
    if (paymentServices.isEmpty()) {
      view.showPaymentsNotFoundMessage();
    } else {
      view.showPayments(paymentServices);
    }
    if (paymentServices.isEmpty()) {
      return Completable.complete();
    }
    return showSelectedService();
  }

  private Completable sendPaymentCancelAnalytics() {
    return billing.getProduct(merchantName, sku)
        .flatMapCompletable(product -> billing.getSelectedService()
            .doOnSuccess(payment -> analytics.sendPaymentViewCancelEvent(product))
            .toCompletable());
  }

  private Completable showSelectedService() {
    return billing.getSelectedService()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess(payment -> view.selectService(payment))
        .toCompletable();
  }
}
