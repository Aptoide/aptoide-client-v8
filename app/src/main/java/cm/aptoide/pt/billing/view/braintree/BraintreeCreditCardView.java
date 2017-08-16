package cm.aptoide.pt.billing.view.braintree;

import cm.aptoide.pt.billing.Product;
import cm.aptoide.pt.presenter.View;
import com.braintreepayments.api.models.CardBuilder;
import com.braintreepayments.api.models.Configuration;
import rx.Observable;

public interface BraintreeCreditCardView extends View {

  void showCreditCardForm(Configuration configuration);

  void showLoading();

  void hideLoading();

  void showError();

  void showProduct(Product product);

  Observable<CardBuilder> creditCardEvent();

  Observable<Void> errorDismissedEvent();

  Observable<Void> cancelEvent();
}