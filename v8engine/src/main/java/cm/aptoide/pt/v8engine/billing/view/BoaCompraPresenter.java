/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 15/02/2017.
 */

package cm.aptoide.pt.v8engine.billing.view;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.billing.Billing;
import cm.aptoide.pt.v8engine.billing.PaymentAnalytics;
import cm.aptoide.pt.v8engine.billing.exception.PaymentAuthorizationAlreadyInitializedException;
import cm.aptoide.pt.v8engine.billing.repository.sync.PaymentSyncScheduler;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import rx.Completable;
import rx.android.schedulers.AndroidSchedulers;

public class BoaCompraPresenter implements Presenter {

  private final BoaCompraView view;
  private final Billing billing;
  private final int paymentId;
  private final PaymentAnalytics analytics;
  private final PaymentSyncScheduler syncScheduler;
  private final ProductProvider productProvider;

  public BoaCompraPresenter(BoaCompraView view, Billing billing, int paymentId,
      PaymentAnalytics analytics, PaymentSyncScheduler syncScheduler,
      ProductProvider productProvider) {
    this.view = view;
    this.billing = billing;
    this.paymentId = paymentId;
    this.analytics = analytics;
    this.syncScheduler = syncScheduler;
    this.productProvider = productProvider;
  }

  @Override public void present() {

    onViewCreatedProcessBoaCompraPayment();

    handleBoaCompraConsentWebsiteLoadedEvent();

    handleBackButtonEvent();

    handleBackToStoreEvent();

    handleDismissEvent();
  }

  private void onViewCreatedProcessBoaCompraPayment() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMapSingle(__ -> productProvider.getProduct())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(product -> view.showLoading())
        .flatMapCompletable(
            product -> billing.getInitializedBoaCompraAuthorization(paymentId, product)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(
                    authorization -> view.loadBoaCompraConsentWebsite(authorization.getUrl(),
                        authorization.getRedirectUrl()))
                .flatMapCompletable(__ -> billing.processBoaCompraPayment(paymentId, product))
                .onErrorResumeNext(throwable -> {
                  if (throwable instanceof PaymentAuthorizationAlreadyInitializedException) {
                    return billing.processBoaCompraPayment(paymentId, product);
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
        .doOnNext(dismiss -> view.dismiss())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}
