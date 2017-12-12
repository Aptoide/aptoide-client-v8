/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/08/2016.
 */

package cm.aptoide.pt.billing.view.payment;

import cm.aptoide.pt.billing.Billing;
import cm.aptoide.pt.billing.BillingAnalytics;
import cm.aptoide.pt.billing.exception.ServiceNotAuthorizedException;
import cm.aptoide.pt.billing.payment.Payment;
import cm.aptoide.pt.billing.view.BillingNavigator;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import java.io.IOException;
import java.util.Set;
import rx.Completable;
import rx.android.schedulers.AndroidSchedulers;

public class PaymentPresenter implements Presenter {

  private static final int CUSTOMER_AUTHORIZATION_REQUEST_CODE = 2001;

  private final Set<String> purchaseErrorShown;
  private final PaymentView view;
  private final Billing billing;
  private final BillingNavigator navigator;
  private final BillingAnalytics analytics;
  private final String merchantName;
  private final String sku;
  private final String payload;

  public PaymentPresenter(PaymentView view, Billing billing, BillingNavigator navigator,
      BillingAnalytics analytics, String merchantName, String sku, String payload,
      Set<String> purchaseErrorShown) {
    this.view = view;
    this.billing = billing;
    this.navigator = navigator;
    this.analytics = analytics;
    this.merchantName = merchantName;
    this.sku = sku;
    this.payload = payload;
    this.purchaseErrorShown = purchaseErrorShown;
  }

  @Override public void present() {

    onViewCreatedNavigateToCustomerAuthentication();

    onViewCreatedHandleCustomerAuthenticationResult();

    onViewCreatedShowPayment();

    onViewCreatedCheckPaymentResult();

    handleSelectServiceEvent();

    handleCancelEvent();

    handleBuyEvent();
  }

  private void onViewCreatedNavigateToCustomerAuthentication() {
    view.getLifecycleEvents()
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
    view.getLifecycleEvents()
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

  private void onViewCreatedShowPayment() {
    view.getLifecycleEvents()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> billing.getCustomer()
            .isAuthenticated())
        .filter(authenticated -> authenticated)
        .flatMap(loading -> billing.getPayment(sku))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(payment -> {
          showPayment(payment);
          view.hidePaymentLoading();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> navigator.popViewWithResult(throwable));
  }

  private void onViewCreatedCheckPaymentResult() {
    view.getLifecycleEvents()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> view.showPurchaseLoading())
        .flatMap(__ -> billing.getCustomer()
            .isAuthenticated())
        .filter(authenticated -> authenticated)
        .flatMap(__ -> billing.getPayment(sku)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(payment -> {

              if (payment.isNew() || payment.isPendingAuthorization()) {
                view.hidePurchaseLoading();
              }

              if (payment.isProcessing()) {
                view.showPurchaseLoading();
              }

              if (payment.isCompleted()) {
                analytics.sendPaymentSuccessEvent();
                navigator.popViewWithResult(payment.getPurchase());
              }

              if (payment.isFailed() && !purchaseErrorShown.contains(payment.getTransaction()
                  .getId())) {
                purchaseErrorShown.add(payment.getTransaction()
                    .getId());
                view.hidePurchaseLoading();
                view.showUnknownError();
                analytics.sendPaymentErrorEvent();
              }
            }))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(__ -> {
        }, throwable -> navigator.popViewWithResult(throwable));
  }

  private void handleCancelEvent() {
    view.getLifecycleEvents()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(__ -> view.cancelEvent())
        .flatMap(__ -> billing.getPayment(sku)
            .first())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(payment -> {
          analytics.sendPaymentViewCancelEvent(payment);
          navigator.popViewWithResult();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> navigator.popViewWithResult());
  }

  private void handleSelectServiceEvent() {
    view.getLifecycleEvents()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(created -> view.selectServiceEvent())
        .flatMapCompletable(serviceId -> billing.selectService(serviceId))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> navigator.popViewWithResult(throwable));
  }

  private void handleBuyEvent() {
    view.getLifecycleEvents()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .flatMap(__ -> view.buyEvent()
            .doOnNext(___ -> view.showBuyLoading())
            .flatMap(___ -> billing.getPayment(sku)
                .first())
            .doOnNext(payment -> analytics.sendPaymentViewBuyEvent(payment))
            .flatMapCompletable(payment -> billing.processPayment(sku, payload)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> {
                  analytics.sendAuthorizationSuccessEvent(payment);
                  view.hideBuyLoading();
                })
                .onErrorResumeNext(throwable -> navigateToAuthorizationView(payment, throwable)))
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

  private Completable navigateToAuthorizationView(Payment payment, Throwable throwable) {
    if (throwable instanceof ServiceNotAuthorizedException) {
      navigator.navigateToTransactionAuthorizationView(merchantName,
          payment.getSelectedPaymentService(), sku);
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

  private void showPayment(Payment payment) {
    view.showProduct(payment.getProduct());
    if (payment.getPaymentServices()
        .isEmpty()) {
      view.showPaymentsNotFoundMessage();
    } else {
      view.showPayments(payment.getPaymentServices(), payment.getSelectedPaymentService());
    }
  }
}
