package cm.aptoide.pt.v8engine.billing.view;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.billing.Billing;
import cm.aptoide.pt.v8engine.billing.methods.braintree.BraintreeTransaction;
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
  private final int paymentId;

  public BraintreePresenter(BraintreeCreditCardView view, Braintree braintree,
      ProductProvider productProvider, Billing billing, BillingNavigator navigator,
      Scheduler viewScheduler, int paymentId) {
    this.view = view;
    this.braintree = braintree;
    this.productProvider = productProvider;
    this.billing = billing;
    this.navigator = navigator;
    this.viewScheduler = viewScheduler;
    this.paymentId = paymentId;
  }

  @Override public void present() {

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

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> view.showLoading())
        .flatMap(__ -> braintree.getConfiguration())
        .doOnNext(configuration -> view.showCreditCardForm(configuration))
        .doOnNext(__ -> view.hideLoading())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();

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

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> braintree.getNonce())
        .flatMapCompletable(nonce -> productProvider.getProduct()
            .observeOn(viewScheduler)
            .flatMapCompletable(product -> {
              switch (nonce.getStatus()) {
                case Braintree.NonceResult.SUCCESS:
                  return billing.processLocalPayment(paymentId, product, nonce.getNonce())
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

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.errorDismissedEvent())
        .doOnNext(dismiss -> navigator.popTransactionAuthorizationView())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}
