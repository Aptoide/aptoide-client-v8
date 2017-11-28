package cm.aptoide.pt.billing.view.paypal;

import cm.aptoide.pt.presenter.View;
import rx.Observable;

public interface PayPalView extends View {

  void showLoading();

  void hideLoading();

  void showNetworkError();

  Observable<Void> errorDismisses();
}
