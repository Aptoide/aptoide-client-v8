package cm.aptoide.pt.billing.view;

import cm.aptoide.pt.billing.Billing;
import cm.aptoide.pt.billing.BillingAnalytics;
import cm.aptoide.pt.billing.authorization.AdyenAuthorization;
import cm.aptoide.pt.billing.payment.Adyen;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import java.io.IOException;
import rx.Completable;
import rx.Scheduler;
import rx.exceptions.OnErrorNotImplementedException;

public class AdyenAuthorizationPresenter implements Presenter {

  private final AdyenAuthorizationView view;
  private final String sku;
  private final Billing billing;
  private final BillingNavigator navigator;
  private final BillingAnalytics analytics;
  private final String serviceName;
  private final Adyen adyen;
  private final Scheduler viewScheduler;

  public AdyenAuthorizationPresenter(AdyenAuthorizationView view, String sku, Billing billing,
      BillingNavigator navigator, BillingAnalytics analytics, String serviceName, Adyen adyen,
      Scheduler viewScheduler) {
    this.view = view;
    this.sku = sku;
    this.billing = billing;
    this.navigator = navigator;
    this.analytics = analytics;
    this.serviceName = serviceName;
    this.adyen = adyen;
    this.viewScheduler = viewScheduler;
  }

  @Override public void present() {

    onViewCreatedShowAuthorization();

    onViewCreatedCheckAuthorizationActive();

    onViewCreatedCheckAuthorizationFailed();

    onViewCreatedCheckAuthorizationProcessing();

    handleAdyenCreditCardResults();

    handleErrorDismissEvent();

    handleAdyenPaymentResult();
  }

  private void onViewCreatedCheckAuthorizationActive() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> billing.getAuthorization(sku))
        .first(authorization -> authorization.isActive())
        .cast(AdyenAuthorization.class)
        .doOnNext(authorization -> analytics.sendAuthorizationSuccessEvent(serviceName))
        .observeOn(viewScheduler)
        .doOnNext(__ -> {
          navigator.popView();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError(throwable));
  }

  private void onViewCreatedCheckAuthorizationFailed() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> billing.getAuthorization(sku))
        .first(authorization -> authorization.isFailed())
        .cast(AdyenAuthorization.class)
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.showUnknownError())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError(throwable));
  }

  private void onViewCreatedCheckAuthorizationProcessing() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> billing.getAuthorization(sku))
        .first(authorization -> authorization.isProcessing())
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.showLoading())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError(throwable));
  }

  private void handleAdyenCreditCardResults() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(authorization -> navigator.adyenResults())
        .flatMapCompletable(details -> adyen.finishPayment(details))
        .observeOn(viewScheduler)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError(throwable));
  }

  private void handleAdyenPaymentResult() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMapSingle(__ -> adyen.getPaymentResult())
        .flatMapCompletable(result -> {
          if (result.isProcessed()) {
            return billing.authorize(sku, result.getPayment()
                .getPayload());
          }
          return Completable.error(result.getError());
        })
        .observeOn(viewScheduler)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError(throwable));
  }

  private void onViewCreatedShowAuthorization() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> view.showLoading())
        .flatMap(__ -> billing.getAuthorization(sku))
        .filter(authorization -> authorization instanceof AdyenAuthorization)
        .first(authorization -> authorization.isPending())
        .cast(AdyenAuthorization.class)
        .flatMapCompletable(
            authorization -> navigator.navigateToAdyenForResult(authorization.getSession())
                .observeOn(viewScheduler)
                .doOnCompleted(() -> view.hideLoading()))
        .observeOn(viewScheduler)
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
          navigator.popView();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
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
