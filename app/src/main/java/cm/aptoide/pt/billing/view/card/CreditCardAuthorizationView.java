package cm.aptoide.pt.billing.view.card;

import cm.aptoide.pt.billing.product.Product;
import cm.aptoide.pt.presenter.View;
import com.adyen.core.models.Amount;
import com.adyen.core.models.PaymentMethod;
import com.adyen.core.models.paymentdetails.PaymentDetails;
import rx.Observable;

public interface CreditCardAuthorizationView extends View {

  void showProduct(Product product);

  void showLoading();

  void hideLoading();

  Observable<Void> errorDismisses();

  Observable<PaymentDetails> creditCardDetailsEvent();

  void showNetworkError();

  Observable<Void> cancelEvent();

  void showCvcView(Amount amount, PaymentMethod paymentMethod);

  void showCreditCardView(PaymentMethod paymentMethod, Amount amount,
      boolean cvcStatus, boolean allowSave, String publicKey,
      String generationTime);
}
