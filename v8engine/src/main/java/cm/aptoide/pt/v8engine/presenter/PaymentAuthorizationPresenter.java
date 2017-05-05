/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 15/02/2017.
 */

package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.pt.v8engine.payment.AptoidePay;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.PaymentAnalytics;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.authorizations.WebAuthorization;
import cm.aptoide.pt.v8engine.payment.exception.PaymentFailureException;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by marcelobenites on 15/02/17.
 */

public class PaymentAuthorizationPresenter implements Presenter {

  private final PaymentAuthorizationView view;
  private final AptoidePay aptoidePay;
  private final Product product;
  private final int paymentId;
  private final PaymentAnalytics analytics;

  private boolean processing;
  private boolean loading;

  public PaymentAuthorizationPresenter(PaymentAuthorizationView view, AptoidePay aptoidePay,
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
        .flatMap(created -> aptoidePay.getPayment(paymentId).toObservable())
        .flatMap(payment -> processPaymentAndDismiss(payment))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnError(throwable -> view.showErrorAndDismiss())
        .onErrorReturn(null)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(created -> view.showLoading())
        .flatMap(created -> aptoidePay.getPayment(paymentId).toObservable())
        .flatMap(payment -> payment.getAuthorization()
            .takeUntil(authorization -> authorization.isInitiated() || authorization.isAuthorized())
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap(authorization -> {

              if (authorization.isAuthorized()) {
                view.showLoading();
                return processPaymentAndDismiss(payment);
              }

              if (authorization.isInitiated()) {
                view.showUrl(((WebAuthorization) authorization).getUrl(),
                    ((WebAuthorization) authorization).getRedirectUrl());
                return Observable.empty();
              }

              return Observable.error(new PaymentFailureException(
                  "Authorization is not initiated can not request user consent."));
            }))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnError(throwable -> view.showErrorAndDismiss())
        .onErrorReturn(null)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  @NonNull public Observable<Object> processPaymentAndDismiss(Payment payment) {
    return payment.process(product)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnCompleted(() -> view.dismiss())
        .toObservable();
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}
