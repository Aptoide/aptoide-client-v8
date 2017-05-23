package cm.aptoide.pt.v8engine.billing.view;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.billing.AptoideBilling;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import java.io.IOException;
import rx.android.schedulers.AndroidSchedulers;

public class PayPalPaymentPresenter implements Presenter {

  private final PayPalPaymentView view;
  private final AptoideBilling billing;
  private final ProductProvider productProvider;

  public PayPalPaymentPresenter(PayPalPaymentView view, AptoideBilling billing,
      ProductProvider productProvider) {
    this.view = view;
    this.billing = billing;
    this.productProvider = productProvider;
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> productProvider.getProduct()
            .doOnSuccess(product -> view.showPayPal(product.getPrice().getCurrency(),
                product.getDescription(), product.getPrice().getAmount()))
            .flatMapObservable(__ -> view.paymentConfirmationId())
            .doOnNext(__ -> view.showLoading())
            .flatMapCompletable(paymentConformationId -> productProvider.getProduct()
                .flatMapCompletable(
                    product -> billing.processPayPalPayment(product, paymentConformationId))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> view.dismiss())))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> hideLoadingAndShowError(throwable));
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

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
