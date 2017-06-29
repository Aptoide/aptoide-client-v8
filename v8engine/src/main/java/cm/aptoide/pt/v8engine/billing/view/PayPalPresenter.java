package cm.aptoide.pt.v8engine.billing.view;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.billing.Billing;
import cm.aptoide.pt.v8engine.billing.PaymentAnalytics;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.product.InAppProduct;
import cm.aptoide.pt.v8engine.billing.product.PaidAppProduct;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import java.io.IOException;
import java.util.Locale;
import rx.Completable;
import rx.Scheduler;

public class PayPalPresenter implements Presenter {

  private static final int PAY_APP_REQUEST_CODE = 12;
  private final PayPalView view;
  private final Billing billing;
  private final ProductProvider productProvider;
  private final PaymentAnalytics analytics;
  private final PaymentNavigator paymentNavigator;
  private final Scheduler viewScheduler;

  public PayPalPresenter(PayPalView view, Billing billing, ProductProvider productProvider,
      PaymentAnalytics analytics, PaymentNavigator paymentNavigator, Scheduler viewScheduler) {
    this.view = view;
    this.billing = billing;
    this.productProvider = productProvider;
    this.analytics = analytics;
    this.paymentNavigator = paymentNavigator;
    this.viewScheduler = viewScheduler;
  }

  @Override public void present() {

    onViewCreatedShowPayPalPayment();

    handlePayPalResultEvent();

    handleErrorDismissEvent();
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }

  private void onViewCreatedShowPayPalPayment() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMapSingle(created -> productProvider.getProduct())
        .observeOn(viewScheduler)
        .doOnNext(product -> paymentNavigator.navigateToPayPalForResult(PAY_APP_REQUEST_CODE,
            product.getPrice()
                .getCurrency(), getPaymentDescription(product), product.getPrice()
                .getAmount()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> hideLoadingAndShowError(throwable));
  }

  private void handlePayPalResultEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> paymentNavigator.payPalResults(PAY_APP_REQUEST_CODE)
            .doOnNext(result -> view.showLoading())
            .doOnNext(result -> analytics.sendPayPalResultEvent(result))
            .flatMapCompletable(result -> processPayPalPayment(result).observeOn(viewScheduler)
                .doOnCompleted(() -> {
                  view.hideLoading();
                  paymentNavigator.popLocalPaymentView();
                })))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> hideLoadingAndShowError(throwable));
  }

  private void handleErrorDismissEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.errorDismisses())
        .doOnNext(product -> paymentNavigator.popLocalPaymentView())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> hideLoadingAndShowError(throwable));
  }

  private Completable processPayPalPayment(PaymentNavigator.PayPalResult result) {
    switch (result.getStatus()) {
      case PaymentNavigator.PayPalResult.SUCCESS:
        return productProvider.getProduct()
            .flatMapCompletable(product -> billing.processPayPalPayment(product,
                result.getPaymentConfirmationId()));
      case PaymentNavigator.PayPalResult.CANCELLED:
      case PaymentNavigator.PayPalResult.ERROR:
      default:
        return Completable.complete();
    }
  }

  private String getPaymentDescription(Product product) {
    if (product instanceof InAppProduct) {
      return String.format(Locale.US, "%s - %s", ((InAppProduct) product).getApplicationName(),
          product.getTitle());
    } else if (product instanceof PaidAppProduct) {
      return product.getTitle();
    }
    throw new IllegalArgumentException(
        "Can NOT provide PayPal payment description. Unknown product.");
  }

  private void hideLoadingAndShowError(Throwable throwable) {
    view.hideLoading();

    if (throwable instanceof IOException) {
      view.showNetworkError();
    } else {
      view.showUnknownError();
    }
  }
}