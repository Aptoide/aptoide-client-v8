/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 15/02/2017.
 */

package cm.aptoide.pt.v8engine.billing.view;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.billing.Billing;
import cm.aptoide.pt.v8engine.billing.PaymentAnalytics;
import cm.aptoide.pt.v8engine.billing.exception.PaymentMethodAlreadyAuthorizedException;
import cm.aptoide.pt.v8engine.billing.repository.sync.PaymentSyncScheduler;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import rx.Completable;
import rx.android.schedulers.AndroidSchedulers;

public class BoaCompraPresenter implements Presenter {

  private final BoaCompraView view;
  private final Billing billing;
  private final PaymentAnalytics analytics;
  private final PaymentSyncScheduler syncScheduler;
  private final ProductProvider productProvider;
  private final PaymentNavigator navigator;
  private final int paymentId;

  public BoaCompraPresenter(BoaCompraView view, Billing billing, PaymentAnalytics analytics,
      PaymentSyncScheduler syncScheduler, ProductProvider productProvider,
      PaymentNavigator navigator, int paymentId) {
    this.view = view;
    this.billing = billing;
    this.analytics = analytics;
    this.syncScheduler = syncScheduler;
    this.productProvider = productProvider;
    this.navigator = navigator;
    this.paymentId = paymentId;
  }

  @Override public void present() {

    onViewCreatedProcessBoaCompraPayment();

    handleBoaCompraConsentWebsiteLoadedEvent();

    handleBackButtonEvent();

    handleBackToStoreEvent();

    handleDismissEvent();
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }

  private void onViewCreatedProcessBoaCompraPayment() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMapSingle(__ -> productProvider.getProduct())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(product -> view.showLoading())
        .flatMapCompletable(product -> billing.getBoaCompraAuthorization(product)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess(authorization -> view.loadBoaCompraConsentWebsite(authorization.getUrl(),
                authorization.getRedirectUrl()))
            .flatMapCompletable(__ -> billing.processBoaCompraPayment(product))
            .onErrorResumeNext(throwable -> {
              if (throwable instanceof PaymentMethodAlreadyAuthorizedException) {
                return billing.processBoaCompraPayment(product);
              }
              return Completable.error(throwable);
            })
            .observeOn(AndroidSchedulers.mainThread())
            .doOnCompleted(() -> view.hideLoading()))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          view.showError();
          view.hideLoading();
        });
  }

  private void handleBackToStoreEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.backToStoreEvent()
            .doOnNext(backToStorePressed -> view.showLoading())
            .flatMapSingle(loading -> productProvider.getProduct())
            .doOnNext(product -> analytics.sendBackToStoreButtonPressedEvent(product)))
        // Optimization to accelerate authorization sync once user interacts with the UI, should
        // be removed once we have a better sync implementation
        .flatMapCompletable(analyticsSent -> syncScheduler.scheduleAuthorizationSync(paymentId))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          view.hideLoading();
          view.showError();
        });
  }

  private void handleBoaCompraConsentWebsiteLoadedEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.boaCompraConsentWebsiteLoaded())
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
        .flatMap(created -> view.backButtonSelection())
        .flatMapSingle(backButtonPressed -> productProvider.getProduct())
        .doOnNext(product -> analytics.sendPaymentAuthorizationBackButtonPressedEvent(product))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          view.hideLoading();
          view.showError();
        });
  }

  private void handleDismissEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.errorDismissedEvent())
        .doOnNext(dismiss -> navigator.popAuthorizedPaymentView())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }
}
