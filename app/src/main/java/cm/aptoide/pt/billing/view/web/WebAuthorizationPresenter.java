package cm.aptoide.pt.billing.view.web;

import android.os.Bundle;
import cm.aptoide.pt.billing.Billing;
import cm.aptoide.pt.billing.BillingAnalytics;
import cm.aptoide.pt.billing.authorization.WebAuthorization;
import cm.aptoide.pt.billing.view.BillingNavigator;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.android.schedulers.AndroidSchedulers;

public class WebAuthorizationPresenter implements Presenter {

  private final WebAuthorizationView view;
  private final Billing billing;
  private final BillingAnalytics analytics;
  private final BillingNavigator navigator;
  private final String merchantName;
  private final String serviceName;
  private final String sku;

  public WebAuthorizationPresenter(WebAuthorizationView view, Billing billing, BillingAnalytics analytics,
      BillingNavigator navigator, String merchantName, String serviceName, String sku) {
    this.view = view;
    this.billing = billing;
    this.analytics = analytics;
    this.navigator = navigator;
    this.merchantName = merchantName;
    this.serviceName = serviceName;
    this.sku = sku;
  }

  @Override public void present() {

    onViewCreatedShowAuthorization();

    onViewCreatedCheckAuthorizationFailed();

    onViewCreatedCheckAuthorizationActive();

    handleUrlLoadErrorEvent();

    handleRedirectUrlEvent();

    handleDismissEvent();
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }

  private void onViewCreatedShowAuthorization() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> view.showLoading())
        .flatMap(__ -> billing.getAuthorization(merchantName, sku)
            .first(authorization -> authorization.isInactive())
            .cast(WebAuthorization.class)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(authorization -> {
              view.hideLoading();
              view.loadWebsite(authorization.getUrl(), authorization.getRedirectUrl());
            }))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError());
  }

  private void onViewCreatedCheckAuthorizationFailed() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> view.showLoading())
        .flatMap(__ -> billing.getAuthorization(merchantName, sku)
            .first(authorization -> authorization.isFailed())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(authorization -> showError()))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError());
  }

  private void onViewCreatedCheckAuthorizationActive() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> view.showLoading())
        .flatMap(__ -> billing.getAuthorization(merchantName, sku)
            .first(authorization -> authorization.isActive())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(authorization -> {
              view.hideLoading();
              analytics.sendAuthorizationSuccessEvent(serviceName);
              navigator.popView();
            }))
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

  private void handleDismissEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.errorDismissedEvent())
        .doOnNext(dismiss -> {
          analytics.sendAuthorizationErrorEvent(serviceName);
          navigator.popView();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void showError() {
    view.hideLoading();
    view.showError();
  }
}
