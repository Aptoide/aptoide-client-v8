package cm.aptoide.pt.v8engine.billing.view;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.billing.Billing;
import cm.aptoide.pt.v8engine.billing.BillingAnalytics;
import cm.aptoide.pt.v8engine.billing.methods.mol.MolTransaction;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import rx.android.schedulers.AndroidSchedulers;

public class MolPresenter implements Presenter {

  private final WebView view;
  private final Billing billing;
  private final BillingAnalytics analytics;
  private final ProductProvider productProvider;
  private final BillingNavigator navigator;
  private final int paymentMethodId;

  public MolPresenter(WebView view, Billing billing, BillingAnalytics analytics,
      ProductProvider productProvider, BillingNavigator navigator, int paymentMethodId) {
    this.view = view;
    this.billing = billing;
    this.analytics = analytics;
    this.productProvider = productProvider;
    this.navigator = navigator;
    this.paymentMethodId = paymentMethodId;
  }

  @Override public void present() {

    onViewCreatedAuthorizeMolPayment();

    onViewCreatedShowMolPaymentError();

    handleWebsiteLoadedEvent();

    handleBackButtonEvent();

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
        .flatMapSingle(__ -> productProvider.getProduct())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(product -> view.showLoading())
        .flatMap(product -> billing.getTransaction(product)
            .first(transaction -> transaction.isPendingAuthorization())
            .cast(MolTransaction.class)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(transaction -> view.loadWebsite(transaction.getConfirmationUrl(),
                transaction.getSuccessUrl())))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          view.showError();
          view.hideLoading();
        });
  }

  private void onViewCreatedShowMolPaymentError() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMapSingle(__ -> productProvider.getProduct())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(product -> view.showLoading())
        .flatMap(product -> billing.getTransaction(product)
            .first(transaction -> !transaction.isPendingAuthorization())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(transaction -> {
              view.showError();
              view.hideLoading();
            }))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          view.showError();
          view.hideLoading();
        });
  }

  private void handleRedirectUrlEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.redirectUrlEvent()
            .doOnNext(backToStorePressed -> view.showLoading())
            .flatMapSingle(loading -> productProvider.getProduct())
            .doOnNext(product -> analytics.sendBackToStoreButtonPressedEvent(product))
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(sent -> navigator.popTransactionAuthorizationView()))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          view.hideLoading();
          view.showError();
        });
  }

  private void handleWebsiteLoadedEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.urlLoadedEvent())
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
        .flatMap(created -> view.backButtonEvent())
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
        .doOnNext(dismiss -> navigator.popTransactionAuthorizationView())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }
}
