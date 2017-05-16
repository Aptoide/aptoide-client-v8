/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 15/02/2017.
 */

package cm.aptoide.pt.v8engine.billing.view;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.billing.AptoideBilling;
import cm.aptoide.pt.v8engine.billing.Payment;
import cm.aptoide.pt.v8engine.billing.PaymentAnalytics;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.billing.repository.sync.PaymentSyncScheduler;
import cm.aptoide.pt.v8engine.billing.services.AuthorizedPayment;
import cm.aptoide.pt.v8engine.billing.services.WebAuthorization;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import rx.Completable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;

public class WebAuthorizationPresenter implements Presenter {

  private final WebAuthorizationView view;
  private final AptoideBilling aptoideBilling;
  private final int paymentId;
  private final PaymentAnalytics analytics;
  private final PaymentSyncScheduler syncScheduler;
  private final ProductProvider productProvider;

  public WebAuthorizationPresenter(WebAuthorizationView view, AptoideBilling aptoideBilling,
      int paymentId, PaymentAnalytics analytics, PaymentSyncScheduler syncScheduler,
      ProductProvider productProvider) {
    this.view = view;
    this.aptoideBilling = aptoideBilling;
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
        .flatMapSingle(lading -> productProvider.getProduct())
        .flatMap(product -> aptoideBilling.getPayment(paymentId, product)
            .toObservable()
            .cast(AuthorizedPayment.class)
            .flatMap(payment -> payment.getAuthorization()
                .cast(WebAuthorization.class)
                .takeUntil(authorization -> authorization.isAuthorized())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapCompletable(authorization -> {

                  if (authorization.isPendingUserConsent()) {
                    return aptoideBilling.authorizeWeb(payment.getId())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnCompleted(() -> view.showUrl(authorization.getUrl(),
                            authorization.getRedirectUrl()));
                  }

                  if (authorization.isAuthorized()) {
                    return processPaymentAndDismiss(payment, product);
                  }

                  if (authorization.isFailed()) {
                    return Completable.error(
                        new PaymentFailureException("Web authorization failed."));
                  }

                  return Completable.complete();
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

  public Completable processPaymentAndDismiss(Payment payment, Product product) {
    return payment.process(product)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnCompleted(() -> view.dismiss());
  }
}