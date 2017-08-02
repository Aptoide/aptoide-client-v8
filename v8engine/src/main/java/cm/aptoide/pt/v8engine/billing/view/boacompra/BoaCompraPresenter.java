/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 15/02/2017.
 */

package cm.aptoide.pt.v8engine.billing.view.boacompra;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.billing.Billing;
import cm.aptoide.pt.v8engine.billing.BillingAnalytics;
import cm.aptoide.pt.v8engine.billing.authorization.boacompra.BoaCompraAuthorization;
import cm.aptoide.pt.v8engine.billing.view.BillingNavigator;
import cm.aptoide.pt.v8engine.billing.view.ProductProvider;
import cm.aptoide.pt.v8engine.billing.view.WebView;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import rx.android.schedulers.AndroidSchedulers;

public class BoaCompraPresenter implements Presenter {

  private final WebView view;
  private final Billing billing;
  private final BillingAnalytics analytics;
  private final ProductProvider productProvider;
  private final BillingNavigator navigator;
  private final int paymentMethodId;

  public BoaCompraPresenter(WebView view, Billing billing, BillingAnalytics analytics,
      ProductProvider productProvider, BillingNavigator navigator, int paymentMethodId) {
    this.view = view;
    this.billing = billing;
    this.analytics = analytics;
    this.productProvider = productProvider;
    this.navigator = navigator;
    this.paymentMethodId = paymentMethodId;
  }

  @Override public void present() {

    onViewCreatedShowBoaCompraWebsite();

    onViewCreatedProcessAuthorizedBoaCompra();

    onViewCreatedAuthorizeBoaCompra();

    onViewCreatedShowBoaCompraError();

    onViewCreatedShowBoaCompraPendingLoading();

    handleUrlLoadErrorEvent();

    handleBackButtonEvent();

    handleRedirectUrlEvent();

    handleDismissEvent();
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }

  private void onViewCreatedShowBoaCompraWebsite() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> view.showLoading())
        .flatMap(product -> billing.getAuthorization(paymentMethodId))
        .first(authorization -> authorization.isInitialized())
        .cast(BoaCompraAuthorization.class)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(authorization -> {
          view.hideLoading();
          view.loadWebsite(authorization.getUrl(), authorization.getRedirectUrl());
        })
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError());
  }

  private void onViewCreatedProcessAuthorizedBoaCompra() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(product -> billing.getAuthorization(paymentMethodId))
        .first(authorization -> authorization.isAuthorized())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> view.showLoading())
        .flatMapCompletable(authorization -> productProvider.getProduct()
            .flatMapCompletable(product -> billing.processPayment(paymentMethodId, product)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> popAuthorizationView())))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError());
  }

  private void popAuthorizationView() {
    view.hideLoading();
    navigator.popTransactionAuthorizationView();
  }

  private void onViewCreatedAuthorizeBoaCompra() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(product -> billing.getAuthorization(paymentMethodId))
        .first(authorization -> authorization.isInactive())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMapCompletable(authorization -> billing.authorize(paymentMethodId))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError());
  }

  private void handleRedirectUrlEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.redirectUrlEvent()
            .doOnNext(backToStorePressed -> view.showLoading())
            .flatMapSingle(loading -> productProvider.getProduct())
            .doOnNext(product -> analytics.sendBackToStoreButtonPressedEvent(product)))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError());
  }

  private void onViewCreatedShowBoaCompraError() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(product -> billing.getAuthorization(paymentMethodId))
        .first(authorization -> authorization.isFailed())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> showError())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError());
  }

  private void onViewCreatedShowBoaCompraPendingLoading() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(product -> billing.getAuthorization(paymentMethodId))
        .first(authorization -> authorization.isFailed())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(loaded -> view.showLoading())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError());
  }

  private void handleUrlLoadErrorEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.loadUrlErrorEvent())
        .first()
        .doOnNext(loaded -> showError())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError());
  }

  private void handleBackButtonEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.backButtonEvent())
        .flatMapSingle(backButtonPressed -> productProvider.getProduct())
        .doOnNext(product -> analytics.sendPaymentAuthorizationBackButtonPressedEvent(product))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> popAuthorizationView());
  }

  private void handleDismissEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.errorDismissedEvent())
        .doOnNext(dismiss -> navigator.popTransactionAuthorizationView())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> popAuthorizationView());
  }

  private void showError() {
    view.hideLoading();
    view.showError();
  }
}
