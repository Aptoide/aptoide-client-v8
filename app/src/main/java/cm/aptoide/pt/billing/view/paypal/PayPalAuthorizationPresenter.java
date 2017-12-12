package cm.aptoide.pt.billing.view.paypal;

import cm.aptoide.pt.billing.Billing;
import cm.aptoide.pt.billing.BillingAnalytics;
import cm.aptoide.pt.billing.authorization.PayPalAuthorization;
import cm.aptoide.pt.billing.view.BillingNavigator;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Scheduler;

public class PayPalAuthorizationPresenter implements Presenter {

  private static final int PAY_APP_REQUEST_CODE = 12;
  private final PayPalView view;
  private final Billing billing;
  private final BillingAnalytics analytics;
  private final BillingNavigator billingNavigator;
  private final String serviceName;
  private final Scheduler viewScheduler;
  private final String sku;

  public PayPalAuthorizationPresenter(PayPalView view, Billing billing, BillingAnalytics analytics,
      BillingNavigator billingNavigator, Scheduler viewScheduler, String sku, String serviceName) {
    this.view = view;
    this.billing = billing;
    this.analytics = analytics;
    this.billingNavigator = billingNavigator;
    this.serviceName = serviceName;
    this.viewScheduler = viewScheduler;
    this.sku = sku;
  }

  @Override public void present() {

    onViewCreatedShowAuthorization();

    onViewCreatedCheckAuthorizationActive();

    onViewCreatedCheckAuthorizationFailed();

    onViewCreatedCheckAuthorizationProcessing();

    handlePayPalResultEvent();

    handleErrorDismissEvent();
  }

  private void onViewCreatedShowAuthorization() {
    view.getLifecycleEvents()
        .first(event -> event.equals(View.LifecycleEvent.RESUME))
        .doOnNext(__ -> view.showLoading())
        .flatMap(created -> billing.getPayment(sku))
        .first(payment -> payment.isPendingAuthorization())
        .map(payment -> payment.getAuthorization())
        .cast(PayPalAuthorization.class)
        .delay(100, TimeUnit.MILLISECONDS)
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.hideLoading())
        .doOnNext(authorization -> billingNavigator.navigateToPayPalForResult(PAY_APP_REQUEST_CODE,
            authorization.getPrice()
                .getCurrency(), authorization.getDescription(), authorization.getPrice()
                .getAmount()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError(throwable));
  }

  private void onViewCreatedCheckAuthorizationActive() {
    view.getLifecycleEvents()
        .first(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> billing.getPayment(sku))
        .first(payment -> payment.isCompleted())
        .doOnNext(payment -> analytics.sendAuthorizationSuccessEvent(payment))
        .observeOn(viewScheduler)
        .doOnNext(__ -> {
          popView();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError(throwable));
  }

  private void onViewCreatedCheckAuthorizationFailed() {
    view.getLifecycleEvents()
        .first(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> billing.getPayment(sku))
        .first(payment -> payment.isFailed())
        .observeOn(viewScheduler)
        .doOnNext(__ -> popViewWithError())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError(throwable));
  }

  private void onViewCreatedCheckAuthorizationProcessing() {
    view.getLifecycleEvents()
        .first(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> billing.getPayment(sku))
        .first(authorization -> authorization.isProcessing())
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.showLoading())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError(throwable));
  }

  private void handlePayPalResultEvent() {
    view.getLifecycleEvents()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> billingNavigator.payPalResults(PAY_APP_REQUEST_CODE))
        .doOnNext(result -> view.showLoading())
        .flatMapCompletable(result -> {
          switch (result.getStatus()) {
            case BillingNavigator.PayPalResult.SUCCESS:
              return billing.authorize(sku, result.getPaymentConfirmationId());
            case BillingNavigator.PayPalResult.CANCELLED:
              analytics.sendAuthorizationCancelEvent(serviceName);
              popView();
              return Completable.complete();
            case BillingNavigator.PayPalResult.ERROR:
              showUnknownError();
              return Completable.complete();
            default:
              return Completable.complete();
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError(throwable));
  }

  private void handleErrorDismissEvent() {
    view.getLifecycleEvents()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.errorDismisses())
        .doOnNext(product -> popViewWithError())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError(throwable));
  }

  private void popView() {
    view.hideLoading();
    billingNavigator.popView();
  }

  private void showUnknownError() {
    view.hideLoading();
  }

  private void showError(Throwable throwable) {
    if (throwable instanceof IOException) {
      view.hideLoading();
      view.showNetworkError();
    } else {
      popViewWithError();
    }
  }

  private void popViewWithError() {
    analytics.sendAuthorizationErrorEvent(serviceName);
    view.hideLoading();
    billingNavigator.popView();
  }
}
