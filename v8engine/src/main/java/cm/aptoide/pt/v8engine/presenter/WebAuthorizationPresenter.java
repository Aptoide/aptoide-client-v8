/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 15/02/2017.
 */

package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.payment.AptoidePay;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.PaymentAnalytics;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.payment.providers.web.WebAuthorizationPayment;
import rx.Completable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by marcelobenites on 15/02/17.
 */

public class WebAuthorizationPresenter implements Presenter {

  private final PaymentAuthorizationView view;
  private final AptoidePay aptoidePay;
  private final Product product;
  private final int paymentId;
  private final PaymentAnalytics analytics;

  private boolean processing;
  private boolean loading;

  public WebAuthorizationPresenter(PaymentAuthorizationView view, AptoidePay aptoidePay,
      Product product, int paymentId, PaymentAnalytics analytics) {
    this.view = view;
    this.aptoidePay = aptoidePay;
    this.product = product;
    this.paymentId = paymentId;
    this.analytics = analytics;
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.backButtonSelection())
        .doOnNext(backPressed -> analytics.sendPaymentAuthorizationBackButtonPressedEvent(product))
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
            .doOnNext(selection -> analytics.sendBackToStoreButtonPressedEvent(product)))
        .doOnNext(loaded -> view.showLoading())
        .flatMap(created -> aptoidePay.getPayment(paymentId)
            .toObservable()
            .cast(WebAuthorizationPayment.class))
        .flatMapCompletable(payment -> payment.authorize().onErrorComplete())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnError(throwable -> view.showErrorAndDismiss())
        .onErrorReturn(null)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(created -> view.showLoading())
        .flatMap(created -> aptoidePay.getPayment(paymentId)
            .toObservable()
            .cast(WebAuthorizationPayment.class))
        .flatMap(payment -> payment.getAuthorization()
            .takeUntil(authorization -> authorization.isAuthorized())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapCompletable(authorization -> {

              if (authorization.isPendingUserConsent()) {
                view.showUrl(authorization.getUrl(), authorization.getRedirectUrl());
                return Completable.complete();
              }

              if (authorization.isAuthorized()) {
                return processPaymentAndDismiss(payment);
              }

              if (authorization.isFailed()) {
                return Completable.error(new PaymentFailureException("Web authorization failed."));
              }

              return Completable.complete();
            }))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnError(throwable -> view.showErrorAndDismiss())
        .onErrorReturn(null)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  public Completable processPaymentAndDismiss(Payment payment) {
    return payment.process(product)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnCompleted(() -> view.dismiss());
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}
