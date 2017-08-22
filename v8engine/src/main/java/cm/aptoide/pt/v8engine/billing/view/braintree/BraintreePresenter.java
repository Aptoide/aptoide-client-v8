package cm.aptoide.pt.v8engine.billing.view.braintree;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.billing.Billing;
import cm.aptoide.pt.v8engine.billing.BillingAnalytics;
import cm.aptoide.pt.v8engine.billing.transaction.braintree.BraintreeTransaction;
import cm.aptoide.pt.v8engine.billing.view.BillingNavigator;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;

public class BraintreePresenter implements Presenter {

  private final BraintreeCreditCardView view;
  private final Braintree braintree;
  private final Billing billing;
  private final BillingAnalytics analytics;
  private final BillingNavigator navigator;
  private final Scheduler viewScheduler;
  private final String paymentMethodName;
  private final String sellerId;
  private final String productId;
  private final String developerPayload;

  public BraintreePresenter(BraintreeCreditCardView view, Braintree braintree, Billing billing,
      BillingAnalytics analytics, BillingNavigator navigator, Scheduler viewScheduler,
      String sellerId, String productId, String paymentMethodName, String developerPayload) {
    this.view = view;
    this.braintree = braintree;
    this.billing = billing;
    this.analytics = analytics;
    this.navigator = navigator;
    this.viewScheduler = viewScheduler;
    this.sellerId = sellerId;
    this.productId = productId;
    this.paymentMethodName = paymentMethodName;
    this.developerPayload = developerPayload;
  }

  @Override public void present() {

    onViewCreatedValidatePendingTransaction();

    onViewCreatedShowProduct();

    onViewCreatedProcessCreditCardPayment();

    handleCreditCardEvent();

    handleErrorDismissEvent();

    handleCancelEvent();
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }

  private void handleCancelEvent() {
    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .flatMap(product -> view.cancelEvent())
        .doOnNext(__ -> {
          analytics.sendAuthorizationCancelEvent(paymentMethodName);
          navigator.popTransactionAuthorizationView();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> navigator.popTransactionAuthorizationView());
  }

  private void handleErrorDismissEvent() {
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

  private void onViewCreatedProcessCreditCardPayment() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> braintree.getNonce())
        .observeOn(viewScheduler)
        .flatMapCompletable(nonce -> {
          switch (nonce.getStatus()) {
            case Braintree.NonceResult.SUCCESS:
              return billing.processLocalPayment(sellerId, productId, developerPayload,
                  nonce.getNonce())
                  .observeOn(viewScheduler)
                  .doOnCompleted(() -> {
                    analytics.sendAuthorizationSuccessEvent(paymentMethodName);
                    view.hideLoading();
                    navigator.popTransactionAuthorizationView();
                  });
            case Braintree.NonceResult.ERROR:
              view.showError();
            case Braintree.NonceResult.CANCELLED:
            default:
              view.hideLoading();
              return Completable.complete();
          }
        })
        .observeOn(viewScheduler)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          view.hideLoading();
          view.showError();
        });
  }

  private void handleCreditCardEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.creditCardEvent())
        .doOnNext(__ -> view.showLoading())
        .doOnNext(card -> braintree.createNonce(card))
        .observeOn(viewScheduler)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          view.hideLoading();
          view.showError();
        });
  }

  private void onViewCreatedShowProduct() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> view.showLoading())
        .flatMap(__ -> braintree.getConfiguration()
            .flatMapSingle(configuration -> billing.getProduct(sellerId, productId)
                .observeOn(viewScheduler)
                .doOnSuccess(product -> {
                  view.showCreditCardForm(configuration);
                  view.showProduct(product);
                })))
        .doOnNext(__ -> view.hideLoading())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          view.hideLoading();
          view.showError();
        });
  }

  private void onViewCreatedValidatePendingTransaction() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .flatMapSingle(__ -> billing.getTransaction(sellerId, productId)
            .first()
            .toSingle())
        .cast(BraintreeTransaction.class)
        .flatMap(transaction -> {
          if (transaction.isPendingAuthorization()) {
            return Observable.just(transaction);
          }
          return Observable.error(
              new IllegalArgumentException("Transaction must be pending authorization."));
        })
        .observeOn(viewScheduler)
        .doOnNext(transaction -> braintree.createConfiguration(transaction.getToken()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> view.showError());
  }
}
