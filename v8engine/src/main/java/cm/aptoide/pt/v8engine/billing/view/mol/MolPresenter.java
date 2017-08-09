package cm.aptoide.pt.v8engine.billing.view.mol;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.billing.Billing;
import cm.aptoide.pt.v8engine.billing.BillingAnalytics;
import cm.aptoide.pt.v8engine.billing.transaction.mol.MolTransaction;
import cm.aptoide.pt.v8engine.billing.view.BillingNavigator;
import cm.aptoide.pt.v8engine.billing.view.WebView;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import rx.android.schedulers.AndroidSchedulers;

public class MolPresenter implements Presenter {

  private final WebView view;
  private final Billing billing;
  private final BillingAnalytics analytics;
  private final BillingNavigator navigator;
  private final String sellerId;
  private final String paymentMethodName;
  private final String productId;

  public MolPresenter(WebView view, Billing billing, BillingAnalytics analytics,
      BillingNavigator navigator, String sellerId, String paymentMethodName, String productId) {
    this.view = view;
    this.billing = billing;
    this.analytics = analytics;
    this.navigator = navigator;
    this.sellerId = sellerId;
    this.paymentMethodName = paymentMethodName;
    this.productId = productId;
  }

  @Override public void present() {

    onViewCreatedAuthorizeMolPayment();

    onViewCreatedCheckTransactionError();

    onViewCreatedCheckTransactionCompleted();

    handleUrlLoadErrorEvent();

    handleRedirectUrlEvent();

    handleDismissEvent();
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }

  private void onViewCreatedAuthorizeMolPayment() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> view.showLoading())
        .flatMap(__ -> billing.getTransaction(sellerId, productId)
            .first(transaction -> transaction.isPendingAuthorization())
            .cast(MolTransaction.class)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(transaction -> {
              view.hideLoading();
              view.loadWebsite(transaction.getConfirmationUrl(), transaction.getSuccessUrl());
            }))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError());
  }

  private void onViewCreatedCheckTransactionError() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> view.showLoading())
        .flatMap(__ -> billing.getTransaction(sellerId, productId)
            .first(transaction -> transaction.isFailed())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(transaction -> showError()))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError());
  }

  private void onViewCreatedCheckTransactionCompleted() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> view.showLoading())
        .flatMap(__ -> billing.getTransaction(sellerId, productId)
            .first(transaction -> transaction.isCompleted())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(transaction -> {
              view.hideLoading();
              analytics.sendAuthorizationSuccessEvent(paymentMethodName);
              navigator.popTransactionAuthorizationView();
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
            .doOnNext(backToStorePressed -> view.showLoading())
            .flatMapSingle(loading -> billing.getProduct(sellerId, productId))
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(sent -> view.showLoading()))
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
          analytics.sendAuthorizationErrorEvent(paymentMethodName);
          navigator.popTransactionAuthorizationView();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void showError() {
    view.hideLoading();
    view.showError();
  }
}
