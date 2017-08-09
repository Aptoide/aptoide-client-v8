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
import cm.aptoide.pt.v8engine.billing.view.WebView;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import rx.android.schedulers.AndroidSchedulers;

public class BoaCompraPresenter implements Presenter {

  private final WebView view;
  private final Billing billing;
  private final BillingAnalytics analytics;
  private final BillingNavigator navigator;
  private final String paymentMethodName;
  private final String sellerId;
  private final String productId;
  private final String payload;

  public BoaCompraPresenter(WebView view, Billing billing, BillingAnalytics analytics,
      BillingNavigator navigator, String paymentMethodName, String sellerId, String productId,
      String payload) {
    this.view = view;
    this.billing = billing;
    this.analytics = analytics;
    this.navigator = navigator;
    this.paymentMethodName = paymentMethodName;
    this.sellerId = sellerId;
    this.productId = productId;
    this.payload = payload;
  }

  @Override public void present() {

    onViewCreatedShowBoaCompraWebsite();

    onViewCreatedProcessAuthorizedBoaCompra();

    onViewCreatedAuthorizeBoaCompra();

    onViewCreatedShowBoaCompraError();

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
        .flatMap(product -> billing.getAuthorization(sellerId, productId))
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
        .flatMap(product -> billing.getAuthorization(sellerId, productId))
        .first(authorization -> authorization.isAuthorized())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> view.showLoading())
        .flatMapCompletable(authorization -> billing.processPayment(sellerId, productId, payload)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnCompleted(() -> {
              analytics.sendAuthorizationSuccessEvent(paymentMethodName);
              popAuthorizationView();
            }))
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
        .doOnNext(__ -> view.showLoading())
        .flatMap(product -> billing.getAuthorization(sellerId, productId))
        .first(authorization -> authorization.isInactive())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMapCompletable(authorization -> billing.authorize(sellerId, productId))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError());
  }

  private void handleRedirectUrlEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.redirectUrlEvent()
            .doOnNext(backToStorePressed -> view.showLoading()))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError());
  }

  private void onViewCreatedShowBoaCompraError() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(product -> billing.getAuthorization(sellerId, productId))
        .first(authorization -> authorization.isFailed())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> showError())
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
        .doOnNext(__ -> analytics.sendAuthorizationCancelEvent(paymentMethodName))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> popAuthorizationView());
  }

  private void handleDismissEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.errorDismissedEvent())
        .doOnNext(dismiss -> {
          analytics.sendAuthorizationErrorEvent(paymentMethodName);
          popAuthorizationView();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> popAuthorizationView());
  }

  private void showError() {
    view.hideLoading();
    view.showError();
  }
}
