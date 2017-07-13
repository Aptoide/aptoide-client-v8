package cm.aptoide.pt.v8engine.billing.view;

import com.braintreepayments.api.models.CardBuilder;
import com.braintreepayments.api.models.Configuration;
import com.braintreepayments.api.models.PaymentMethodNonce;
import rx.Observable;

public interface Braintree {

  Observable<NonceResult> getNonce();

  void createNonce(CardBuilder cardBuilder);

  void createConfiguration(String clientToken);

  Observable<Configuration> getConfiguration();

  public static class NonceResult {

    public static final int CANCELLED = 0;
    public static final int ERROR = -1;
    public static final int SUCCESS = 1;

    private final int status;
    private final PaymentMethodNonce nonce;

    public NonceResult(int status, PaymentMethodNonce nonce) {
      this.status = status;
      this.nonce = nonce;
    }

    public int getStatus() {
      return status;
    }

    public String getNonce() {
      return nonce.getNonce();
    }
  }
}
