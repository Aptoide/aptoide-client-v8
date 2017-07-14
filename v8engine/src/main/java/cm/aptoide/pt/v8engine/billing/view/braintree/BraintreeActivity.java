package cm.aptoide.pt.v8engine.billing.view.braintree;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.v8engine.view.BackButtonActivity;
import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.Card;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.ConfigurationListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.CardBuilder;
import com.braintreepayments.api.models.Configuration;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;

public abstract class BraintreeActivity extends BackButtonActivity
    implements PaymentMethodNonceCreatedListener, BraintreeCancelListener, BraintreeErrorListener,
    ConfigurationListener, Braintree {

  private PublishRelay<NonceResult> resultRelay;
  private PublishRelay<Configuration> configurationRelay;
  private BraintreeFragment braintreeFragment;
  private Configuration configuration;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    resultRelay = PublishRelay.create();
    configurationRelay = PublishRelay.create();
  }

  @Override public void onCancel(int i) {
    resultRelay.call(new NonceResult(NonceResult.CANCELLED, null));
  }

  @Override public void onError(Exception e) {
    resultRelay.call(new NonceResult(NonceResult.ERROR, null));
  }

  @Override public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
    resultRelay.call(new NonceResult(NonceResult.SUCCESS, paymentMethodNonce));
  }

  @Override public void onConfigurationFetched(Configuration configuration) {
    this.configuration = configuration;
    configurationRelay.call(configuration);
  }

  @Override public Observable<NonceResult> getNonce() {
    return resultRelay;
  }

  @Override public void createNonce(CardBuilder cardBuilder) {
    Card.tokenize(braintreeFragment, cardBuilder);
  }

  @Override public void createConfiguration(String clientToken) {
    if (configuration == null) {
      if (braintreeFragment != null) {
        getFragmentManager().beginTransaction()
            .remove(braintreeFragment)
            .commit();
      }

      try {
        braintreeFragment = BraintreeFragment.newInstance(this, clientToken);
      } catch (InvalidArgumentException exception) {
        throw new IllegalArgumentException(exception);
      }
    } else {
      configurationRelay.call(configuration);
    }
  }

  @Override public Observable<Configuration> getConfiguration() {
    return configurationRelay;
  }
}