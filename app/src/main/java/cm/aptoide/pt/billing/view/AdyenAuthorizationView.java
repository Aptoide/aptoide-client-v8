package cm.aptoide.pt.billing.view;

import cm.aptoide.pt.presenter.View;
import com.adyen.core.PaymentRequest;
import rx.Observable;

public interface AdyenAuthorizationView extends View {

  void showLoading();

  void hideLoading();

  Observable<Void> errorDismisses();

  void showNetworkError();

  void showUnknownError();

  Observable<Void> backButtonEvent();

  void showCvvView(PaymentRequest request);
}
