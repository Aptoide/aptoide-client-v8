package cm.aptoide.pt.billing.view.card;

import cm.aptoide.pt.billing.Billing;
import cm.aptoide.pt.billing.BillingAnalytics;
import cm.aptoide.pt.billing.authorization.AdyenAuthorization;
import cm.aptoide.pt.billing.payment.Adyen;
import cm.aptoide.pt.billing.view.BillingNavigator;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import com.adyen.core.models.PaymentMethod;
import java.io.IOException;
import rx.Completable;
import rx.Scheduler;
import rx.exceptions.OnErrorNotImplementedException;

public class CreditCardAuthorizationPresenter implements Presenter {

  private final CreditCardAuthorizationView view;
  private final String sku;
  private final Billing billing;
  private final BillingNavigator navigator;
  private final BillingAnalytics analytics;
  private final String serviceName;
  private final Adyen adyen;
  private final Scheduler viewScheduler;

  public CreditCardAuthorizationPresenter(CreditCardAuthorizationView view, String sku,
      Billing billing,
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

    onViewCreatedCreatePayment();

    onViewCreatedSelectCreditCardPayment();

    onViewCreatedShowCreditCardInputView();

    onViewCreatedCheckAuthorizationActive();

    onViewCreatedCheckAuthorizationFailed();

    onViewCreatedCheckAuthorizationProcessing();

    handleAdyenCreditCardResults();

    handleAdyenUriRedirect();

    handleAdyenUriResult();

    handleErrorDismissEvent();

    handleAdyenPaymentResult();

    handleCancel();
  }

  private void handleCancel() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.cancelEvent())
        .observeOn(viewScheduler)
        .doOnNext(__ -> {
          analytics.sendAuthorizationCancelEvent(serviceName);
          navigator.popView();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError(throwable));
  }

  private void onViewCreatedCheckAuthorizationActive() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> billing.getPayment(sku))
        .first(payment -> payment.isCompleted())
        .doOnNext(payment -> analytics.sendAuthorizationSuccessEvent(payment))
        .observeOn(viewScheduler)
        .doOnNext(__ -> navigator.popView())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError(throwable));
  }

  private void onViewCreatedCheckAuthorizationFailed() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> billing.getPayment(sku))
        .first(payment -> payment.isFailed())
        .cast(AdyenAuthorization.class)
        .observeOn(viewScheduler)
        .doOnNext(__ -> popViewWithError())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError(throwable));
  }

  private void onViewCreatedCheckAuthorizationProcessing() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> billing.getPayment(sku))
        .filter(payment -> payment.isProcessing())
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.showLoading())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError(throwable));
  }

  private void handleAdyenCreditCardResults() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(authorization -> view.creditCardDetailsEvent())
        .doOnNext(__ -> view.showLoading())
        .flatMapCompletable(details -> adyen.finishPayment(details))
        .observeOn(viewScheduler)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError(throwable));
  }

  private void handleAdyenUriRedirect() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMapSingle(__ -> adyen.getRedirectUrl())
        .observeOn(viewScheduler)
        .doOnNext(redirectUrl -> {
          view.showLoading();
          navigator.navigateToUriForResult(redirectUrl);
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError(throwable));
  }

  private void handleAdyenUriResult() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> navigator.uriResults())
        .flatMapCompletable(uri -> adyen.finishUri(uri))
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

  private void onViewCreatedShowCreditCardInputView() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMapSingle(__ -> adyen.getPaymentData())
        .observeOn(viewScheduler)
        .doOnNext(data -> {
          view.hideLoading();
          if (data.getPaymentMethod()
              .getType()
              .equals(PaymentMethod.Type.CARD)) {
            view.showCreditCardView(data.getPaymentMethod(), data.getAmount(), true,
                data.getShopperReference() != null, data.getPublicKey(), data.getGenerationTime());
          } else {
            view.showCvcView(data.getAmount(), data.getPaymentMethod());
          }
        })
        .observeOn(viewScheduler)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError(throwable));
  }

  private void onViewCreatedSelectCreditCardPayment() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMapSingle(__ -> adyen.getCreditCardPaymentService())
        .flatMapCompletable(creditCard -> adyen.selectPaymentService(creditCard))
        .observeOn(viewScheduler)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError(throwable));
  }

  private void onViewCreatedCreatePayment() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> view.showLoading())
        .flatMap(__ -> billing.getPayment(sku))
        .observeOn(viewScheduler)
        .doOnNext(payment -> view.showProduct(payment.getProduct()))
        .first(payment -> payment.isPendingAuthorization())
        .map(payment -> payment.getAuthorization())
        .cast(AdyenAuthorization.class)
        .flatMapCompletable(authorization -> adyen.createPayment(authorization.getSession()))
        .observeOn(viewScheduler)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError(throwable));
  }

  private void handleErrorDismissEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.errorDismisses())
        .doOnNext(__ -> popViewWithError())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
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
    navigator.popView();
  }
}
