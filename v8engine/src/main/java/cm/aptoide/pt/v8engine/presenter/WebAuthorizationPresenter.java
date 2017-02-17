/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 15/02/2017.
 */

package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.payment.AptoidePay;
import cm.aptoide.pt.v8engine.payment.authorizations.WebAuthorization;
import cm.aptoide.pt.v8engine.payment.products.AptoideProduct;
import cm.aptoide.pt.v8engine.view.View;
import cm.aptoide.pt.v8engine.view.WebAuthorizationView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by marcelobenites on 15/02/17.
 */

public class WebAuthorizationPresenter implements Presenter {

  private final WebAuthorizationView view;
  private final AptoidePay aptoidePay;
  private final AptoideProduct product;
  private final int paymentId;

  public WebAuthorizationPresenter(WebAuthorizationView view, AptoidePay aptoidePay,
      AptoideProduct product, int paymentId) {
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
        .subscribe();

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.redirect())
        .doOnNext(loaded -> view.showLoading())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(created -> view.showLoading())
        .flatMap(loading -> aptoidePay.payment(paymentId, product))
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(payment -> {
          if (payment.isPending()) {
            view.showLoading();
          } else if (payment.isCompleted()) {
            view.hideLoading();
            view.dismiss();
          } else if (payment.isAuthorized()) {
            return aptoidePay.process(payment).toObservable();
          } else if (payment.isPendingAuthorization()) {
            view.showUrl(((WebAuthorization) payment.getAuthorization()).getUrl(),
                ((WebAuthorization) payment.getAuthorization()).getRedirectUrl());
          } else if (payment.isFailed()) {
            view.showErrorAndDismiss();
          }
          return Observable.empty();
        })
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
