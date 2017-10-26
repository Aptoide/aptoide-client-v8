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
    view.getLifecycle()
        .first(event -> event.equals(View.LifecycleEvent.RESUME))
        .doOnNext(__ -> view.showLoading())
        .flatMap(created -> billing.getAuthorization(sku))
        .first(authorization -> authorization instanceof PayPalAuthorization
            && authorization.isPending())
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
    view.getLifecycle()
        .first(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> billing.getAuthorization(sku))
        .first(authorization -> authorization instanceof PayPalAuthorization
            && authorization.isActive())
        .cast(PayPalAuthorization.class)
        .doOnNext(authorization -> analytics.sendAuthorizationSuccessEvent(serviceName))
        .observeOn(viewScheduler)
        .doOnNext(__ -> {
          popView();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError(throwable));
  }

  private void onViewCreatedCheckAuthorizationFailed() {
    view.getLifecycle()
        .first(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> billing.getAuthorization(sku))
        .first(authorization -> authorization instanceof PayPalAuthorization
            && authorization.isFailed())
        .cast(PayPalAuthorization.class)
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.showUnknownError())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError(throwable));
  }

  private void onViewCreatedCheckAuthorizationProcessing() {
    view.getLifecycle()
        .first(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> billing.getAuthorization(sku))
        .first(authorization -> authorization.isProcessing())
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.showLoading())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError(throwable));
  }

  private void handlePayPalResultEvent() {
    view.getLifecycle()
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
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.errorDismisses())
        .doOnNext(product -> {
          analytics.sendAuthorizationErrorEvent(serviceName);
          billingNavigator.popView();
        })
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
    view.showUnknownError();
  }

  private void showError(Throwable throwable) {
    view.hideLoading();

    if (throwable instanceof IOException) {
      view.showNetworkError();
    } else {
      view.showUnknownError();
    }
  }
}