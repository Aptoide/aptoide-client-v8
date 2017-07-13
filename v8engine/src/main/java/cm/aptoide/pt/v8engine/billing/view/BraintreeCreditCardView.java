package cm.aptoide.pt.v8engine.billing.view;

import cm.aptoide.pt.v8engine.presenter.View;
import com.braintreepayments.api.models.CardBuilder;
import com.braintreepayments.api.models.Configuration;
import rx.Observable;

public interface BraintreeCreditCardView extends View {

  void showCreditCardForm(Configuration configuration);

  Observable<CardBuilder> creditCardEvent();

  void showLoading();

  void hideLoading();

  void showError();

  Observable<Void> errorDismissedEvent();

}