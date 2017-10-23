package cm.aptoide.pt.billing.payment;

import cm.aptoide.pt.navigator.FragmentNavigator;
import com.adyen.core.PaymentRequest;
import com.adyen.core.models.paymentdetails.CreditCardPaymentDetails;
import com.adyen.ui.fragments.CreditCardFragmentBuilder;
import rx.Observer;
import rx.observables.SyncOnSubscribe;

public class OnSubscribeCreditCardFragment
    extends SyncOnSubscribe<FragmentNavigator, CreditCardPaymentDetails> {

  private final FragmentNavigator navigator;
  private final PaymentRequest paymentRequest;
  private final boolean replace;

  private CreditCardPaymentDetails details;

  public OnSubscribeCreditCardFragment(FragmentNavigator navigator,
      PaymentRequest paymentRequest, boolean replace) {
    this.paymentRequest = paymentRequest;
    this.navigator = navigator;
    this.replace = replace;
  }

  @Override protected FragmentNavigator generateState() {
    navigator.navigateTo(
        new CreditCardFragmentBuilder().setPaymentMethod(paymentRequest.getPaymentMethod())
            .setPublicKey(paymentRequest.getPublicKey())
            .setGenerationtime(paymentRequest.getGenerationTime())
            .setAmount(paymentRequest.getAmount())
            .setShopperReference(paymentRequest.getShopperReference())
            .setCVCFieldStatus(CreditCardFragmentBuilder.CvcFieldStatus.REQUIRED)
            .setCreditCardInfoListener(
                details -> OnSubscribeCreditCardFragment.this.details = details)
            .build(), replace);
    return navigator;
  }

  @Override protected FragmentNavigator next(FragmentNavigator navigator,
      Observer<? super CreditCardPaymentDetails> observer) {

    if (details != null) {
      observer.onNext(details);
      observer.onCompleted();
    }

    return navigator;
  }
}
