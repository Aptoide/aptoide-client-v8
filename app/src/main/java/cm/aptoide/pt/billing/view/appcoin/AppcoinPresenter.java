package cm.aptoide.pt.billing.view.appcoin;

import android.os.Bundle;
import cm.aptoide.pt.billing.Billing;
import cm.aptoide.pt.billing.view.BillingNavigator;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.schedulers.Schedulers;

/**
 * Created by jose_messejana on 27-10-2017.
 */

public class AppcoinPresenter implements Presenter {

  private AppcoinFragment view;
  private BillingNavigator billingNavigator;
  private String sellerId;
  private String productId;
  private Billing billing;

  public AppcoinPresenter(AppcoinFragment view, Billing billing, BillingNavigator billingNavigator, String sellerId, String productId){
    this.view = view;
    this.billingNavigator = billingNavigator;
    this.billing = billing;
    this.sellerId = sellerId;
    this.productId = productId;
  }

  @Override public void present() {
    handleProceedButton();
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }

  private void handleProceedButton() {
    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event)).observeOn(Schedulers.io())
        .flatMapCompletable(string -> billing.processLocalPayment(sellerId, productId, null, null)
            .doOnCompleted(() -> {
              billingNavigator.popView();
            }))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
        });

  }

}
