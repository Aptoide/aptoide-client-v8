/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 15/02/2017.
 */

package cm.aptoide.pt.v8engine.billing.view;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.billing.Billing;
import cm.aptoide.pt.v8engine.billing.BillingAnalytics;
import cm.aptoide.pt.v8engine.billing.BillingSyncScheduler;
import cm.aptoide.pt.v8engine.billing.methods.boacompra.BoaCompraAuthorization;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import rx.android.schedulers.AndroidSchedulers;

public class BoaCompraPresenter implements Presenter {

  private final WebView view;
  private final Billing billing;
  private final BillingAnalytics analytics;
  private final BillingSyncScheduler syncScheduler;
  private final ProductProvider productProvider;
  private final BillingNavigator navigator;
  private final int paymentMethodId;

  public BoaCompraPresenter(WebView view, Billing billing, BillingAnalytics analytics,
      BillingSyncScheduler syncScheduler, ProductProvider productProvider,
      BillingNavigator navigator, int paymentMethodId) {
    this.view = view;
    this.billing = billing;
    this.analytics = analytics;
    this.syncScheduler = syncScheduler;
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

    handleWebsiteLoadedEvent();

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
        .doOnNext(authorization -> view.loadWebsite(authorization.getUrl(),
            authorization.getRedirectUrl()))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          view.hideLoading();
          view.showError();
        });
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
                .doOnCompleted(() -> {
                  view.hideLoading();
                  navigator.popTransactionAuthorizationView();
                })))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          view.hideLoading();
          view.showError();
        });
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
        }, throwable -> {
          view.hideLoading();
          view.showError();
        });
  }

  private void handleRedirectUrlEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.redirectUrlEvent()
            .doOnNext(backToStorePressed -> view.showLoading())
            .flatMapSingle(loading -> productProvider.getProduct())
            .doOnNext(product -> analytics.sendBackToStoreButtonPressedEvent(product)))
        // Optimization to accelerate authorization sync once user interacts with the UI, should
        // be removed once we have a better sync implementation
        .flatMapCompletable(
            analyticsSent -> syncScheduler.scheduleAuthorizationSync(paymentMethodId))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          view.hideLoading();
          view.showError();
        });
  }

  private void onViewCreatedShowBoaCompraError() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(product -> billing.getAuthorization(paymentMethodId))
        .first(authorization -> authorization.isFailed())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> {
          view.hideLoading();
          view.showError();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          view.hideLoading();
          view.showError();
        });
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
        }, throwable -> {
          view.hideLoading();
          view.showError();
        });
  }

  private void handleWebsiteLoadedEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.urlLoadedEvent())
        .doOnNext(loaded -> view.hideLoading())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          view.hideLoading();
          view.showError();
        });
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
        }, throwable -> {
          view.hideLoading();
          navigator.popTransactionAuthorizationView();
        });
  }

  private void handleDismissEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.errorDismissedEvent())
        .doOnNext(dismiss -> navigator.popTransactionAuthorizationView())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          view.hideLoading();
          navigator.popTransactionAuthorizationView();
        });
  }
}
