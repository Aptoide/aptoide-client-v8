package cm.aptoide.pt.v8engine.billing.view.braintree;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.billing.Billing;
import cm.aptoide.pt.v8engine.billing.transaction.braintree.BraintreeTransaction;
import cm.aptoide.pt.v8engine.billing.view.BillingNavigator;
import cm.aptoide.pt.v8engine.billing.view.ProductProvider;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;

public class BraintreePresenter implements Presenter {

  private final BraintreeCreditCardView view;
  private final Braintree braintree;
  private final ProductProvider productProvider;
  private final Billing billing;
  private final BillingNavigator navigator;
  private final Scheduler viewScheduler;
  private final int paymentMethodId;

  public BraintreePresenter(BraintreeCreditCardView view, Braintree braintree,
      ProductProvider productProvider, Billing billing, BillingNavigator navigator,
      Scheduler viewScheduler, int paymentMethodId) {
    this.view = view;
    this.braintree = braintree;
    this.productProvider = productProvider;
    this.billing = billing;
    this.navigator = navigator;
    this.viewScheduler = viewScheduler;
    this.paymentMethodId = paymentMethodId;
  }

  @Override public void present() {

    onViewCreatedValidatePendingTransaction();

    onViewCreatedShowProduct();

    onViewCreatedProcessCreditCardPayment();

    handleCreditCardEvent();

    handleErrorDismissEvent();

    handleCancellationEvent();
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }

  private void handleCancellationEvent() {
    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .flatMap(product -> Observable.merge(view.cancellationEvent(), view.tapOutsideSelection()))
        .doOnNext(__ -> navigator.popTransactionAuthorizationView())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> navigator.popTransactionAuthorizationView());
  }

  private void handleErrorDismissEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.errorDismissedEvent())
        .doOnNext(dismiss -> navigator.popTransactionAuthorizationView())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void onViewCreatedProcessCreditCardPayment() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> braintree.getNonce())
        .flatMapCompletable(nonce -> productProvider.getProduct()
            .observeOn(viewScheduler)
            .flatMapCompletable(product -> {
              switch (nonce.getStatus()) {
                case Braintree.NonceResult.SUCCESS:
                  return billing.processLocalPayment(paymentMethodId, product, nonce.getNonce())
                      .observeOn(viewScheduler)
                      .doOnCompleted(() -> {
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
            }))
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
            .flatMapSingle(configuration -> productProvider.getProduct()
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
        .flatMapSingle(__ -> productProvider.getProduct())
        .flatMapSingle(product -> billing.getTransaction(product)
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
