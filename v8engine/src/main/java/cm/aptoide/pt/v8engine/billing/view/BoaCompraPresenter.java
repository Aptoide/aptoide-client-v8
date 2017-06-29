/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 15/02/2017.
 */

package cm.aptoide.pt.v8engine.billing.view;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.billing.Billing;
import cm.aptoide.pt.v8engine.billing.PaymentAnalytics;
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

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.backButtonSelection())
        .flatMapSingle(backButtonPressed -> productProvider.getProduct())
        .doOnNext(product -> analytics.sendPaymentAuthorizationBackButtonPressedEvent(product))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.urlLoad())
        .doOnNext(loaded -> view.hideLoading())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.backToStoreSelection()
            .doOnNext(backToStorePressed -> view.showLoading())
            .flatMapSingle(loading -> productProvider.getProduct())
            .doOnNext(product -> analytics.sendBackToStoreButtonPressedEvent(product)))
        // Optimization to accelerate authorization sync once user interacts with the UI, should
        // be removed once we have a better sync implementation
        .flatMapCompletable(analyticsSent -> syncScheduler.scheduleAuthorizationSync(paymentId))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnError(throwable -> view.showErrorAndDismiss())
        .onErrorReturn(null)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(created -> view.showLoading())
        .flatMap(__ -> productProvider.getProduct()
            .flatMapObservable(product -> billing.getWebPaymentAuthorization(paymentId, product)
                .takeUntil(webAuthorization -> !webAuthorization.isPendingUserConsent())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapCompletable(authorization -> {

                  if (authorization.isPendingUserConsent()) {
                    view.showUrl(authorization.getUrl(), authorization.getRedirectUrl());
                    return Completable.complete();
                  }

                  return billing.processPayment(paymentId, product)
                      .observeOn(AndroidSchedulers.mainThread())
                      .doOnCompleted(() -> view.dismiss());
                })))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnError(throwable -> view.showErrorAndDismiss())
        .onErrorReturn(null)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}
