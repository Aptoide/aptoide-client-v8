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
import rx.android.schedulers.AndroidSchedulers;

public class PayPalPresenter implements Presenter {

  private final PayPalView view;
  private final Billing billing;
  private final ProductProvider productProvider;
  private final PaymentAnalytics analytics;

  public PayPalPresenter(PayPalView view, Billing billing, ProductProvider productProvider,
      PaymentAnalytics analytics) {
    this.view = view;
    this.billing = billing;
    this.productProvider = productProvider;
    this.analytics = analytics;
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

  private void handleErrorDismissEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.errorDismisses())
        .doOnNext(product -> view.dismiss())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> hideLoadingAndShowError(throwable));
  }

  private void handlePayPalResultEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.results())
        .doOnNext(__ -> view.showLoading())
        .doOnNext(result -> analytics.sendPayPalResultEvent(result))
        .doOnNext(result -> dismissOnPayPalError(result))
        .filter(result -> result.getStatus() == PayPalView.PayPalResult.SUCCESS)
        .flatMapCompletable(result -> productProvider.getProduct()
            .flatMapCompletable(
                product -> billing.processPayPalPayment(product, result.getPaymentConfirmationId()))
            .observeOn(AndroidSchedulers.mainThread())
            .doOnCompleted(() -> view.dismiss()))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> hideLoadingAndShowError(throwable));
  }

  private void onViewCreatedShowPayPalPayment() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMapSingle(created -> productProvider.getProduct())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(product -> view.showPayPal(product.getPrice()
            .getCurrency(), getPaymentDescription(product), product.getPrice()
            .getAmount()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> hideLoadingAndShowError(throwable));
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

  private void dismissOnPayPalError(PayPalView.PayPalResult result) {
    switch (result.getStatus()) {
      case PayPalView.PayPalResult.CANCELLED:
      case PayPalView.PayPalResult.ERROR:
        view.hideLoading();
        view.dismiss();
        break;
      case PayPalView.PayPalResult.SUCCESS:
      default:
    }
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