package cm.aptoide.pt.v8engine.billing.view.braintree;

import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.presenter.View;
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

  Observable<Void> cancellationEvent();

  Observable<Void> tapOutsideSelection();
}