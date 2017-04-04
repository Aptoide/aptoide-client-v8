/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 15/02/2017.
 */

package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.payment.AptoidePay;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.authorizations.WebAuthorization;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by marcelobenites on 15/02/17.
 */

public class WebAuthorizationPresenter implements Presenter {

  private final WebAuthorizationView view;
  private final AptoidePay aptoidePay;
  private final Product product;
  private final int paymentId;
  private boolean processing;
  private boolean loading;

  public WebAuthorizationPresenter(WebAuthorizationView view, AptoidePay aptoidePay,
      Product product, int paymentId) {
    this.view = view;
    this.aptoidePay = aptoidePay;
    this.product = product;
    this.paymentId = paymentId;
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.urlLoad())
        .doOnNext(loaded -> view.hideLoading())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          CrashReport.getInstance().log(err);
        });

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.redirect())
        .doOnNext(loaded -> view.showLoading())
        .flatMap(loading -> aptoidePay.authorize(paymentId).toObservable())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          CrashReport.getInstance().log(err);
        });

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(created -> view.showLoading())
        .flatMap(created -> Observable.combineLatest(
            aptoidePay.payment(paymentId).observeOn(AndroidSchedulers.mainThread()),
            aptoidePay.confirmation(product).observeOn(AndroidSchedulers.mainThread()),
            (payment, confirmation) -> {
              if (payment.getAuthorization().isPending() || confirmation.isPending()) {
                view.showLoading();
              } else if (confirmation.isCompleted()) {
                view.hideLoading();
                view.dismiss();
              } else if (payment.getAuthorization().isAuthorized()) {
                if (!processing) {
                  processing = true;
                  return aptoidePay.process(payment, product).toObservable();
                }
              } else if (payment.getAuthorization().isInitiated()) {
                if (!loading) {
                  loading = true;
                  view.showUrl(((WebAuthorization) payment.getAuthorization()).getUrl(),
                      ((WebAuthorization) payment.getAuthorization()).getRedirectUrl());
                }
              } else if (payment.getAuthorization().isFailed() || confirmation.isFailed()) {
                view.showErrorAndDismiss();
              }
              return Observable.empty();
            }))
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(observable -> observable)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnError(throwable -> view.showErrorAndDismiss())
        .onErrorReturn(null)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          CrashReport.getInstance().log(err);
        });
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}
