package cm.aptoide.pt.account.view;

import android.os.Bundle;
import cm.aptoide.pt.billing.view.BillingNavigator;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Observable;

public class PaymentLoginPresenter implements Presenter {

  private final PaymentLoginView view;
  private final BillingNavigator navigator;
  private final int requestCode;

  public PaymentLoginPresenter(PaymentLoginView view, BillingNavigator navigator, int requestCode) {
    this.view = view;
    this.navigator = navigator;
    this.requestCode = requestCode;
  }

  @Override public void present() {

    handleBackButtonAndUpNavigationEvents();

  }

  public void handleBackButtonAndUpNavigationEvents() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> Observable.merge(view.backButtonEvents(), view.upNavigationEvents()))
        .doOnNext(__ -> navigator.popPayerAuthenticationViewWithResult(requestCode, false))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}
