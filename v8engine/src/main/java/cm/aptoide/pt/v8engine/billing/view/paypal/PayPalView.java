package cm.aptoide.pt.v8engine.billing.view.paypal;

import cm.aptoide.pt.v8engine.presenter.View;
import rx.Observable;

public interface PayPalView extends View {

  void showLoading();

  void hideLoading();

  void showNetworkError();

  void showUnknownError();

  Observable<Void> errorDismisses();
}
