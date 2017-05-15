package cm.aptoide.pt.v8engine.billing.view;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.billing.AptoideBilling;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import java.io.IOException;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;

public class PayPalAuthorizationPresenter implements Presenter {

  private final PayPalAuthorizationView view;
  private final AptoideBilling billing;
  private final int paymentId;

  private final long appId;
  private final String storeName;
  private final boolean sponsored;

  private final int apiVersion;
  private final String type;
  private final String sku;
  private final String packageName;
  private final String developerPayload;

  public PayPalAuthorizationPresenter(PayPalAuthorizationView view, AptoideBilling billing,
      int paymentId, long appId, String storeName, boolean sponsored, int apiVersion, String type,
      String sku, String packageName, String developerPayload) {
    this.view = view;
    this.billing = billing;
    this.paymentId = paymentId;
    this.appId = appId;
    this.storeName = storeName;
    this.sponsored = sponsored;
    this.apiVersion = apiVersion;
    this.type = type;
    this.sku = sku;
    this.packageName = packageName;
    this.developerPayload = developerPayload;
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(created -> view.showPayPalAuthorization())
        .flatMap(__ -> view.authorizationCode())
        .doOnNext(__ -> view.showLoading())
        .flatMap(authorizationCode -> getProduct().flatMapCompletable(
            product -> billing.process(paymentId, product, authorizationCode)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> view.dismiss()))
            .toObservable())
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__  -> {}, throwable -> hideLoadingAndShowError(throwable));
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

  private Single<Product> getProduct() {

    if (storeName != null) {
      return billing.getPaidAppProduct(appId, storeName, sponsored);
    }

    if (sku != null) {
      return billing.getInAppProduct(apiVersion, packageName, sku, type, developerPayload);
    }

    return Single.error(new IllegalStateException("No product information provided to presenter."));
  }
}
