/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import cm.aptoide.pt.v8engine.payment.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.payment.repository.PaymentConfirmationRepository;
import java.util.List;
import rx.Observable;
import rx.Single;

public class AptoideBilling {

  private final PaymentConfirmationRepository confirmationRepository;
  private final ProductRepository productRepository;

  public AptoideBilling(PaymentConfirmationRepository confirmationRepository,
      ProductRepository productRepository) {
    this.confirmationRepository = confirmationRepository;
    this.productRepository = productRepository;
  }

  public Single<List<Payment>> getPayments() {
    return productRepository.getPayments();
  }

  public Single<Payment> getPayment(int paymentId) {
    return getPayments().flatMapObservable(payments -> Observable.from(payments)
        .filter(payment -> payment.getId() == paymentId)
        .switchIfEmpty(Observable.error(
            new PaymentFailureException("Payment " + paymentId + "not available")))).toSingle();
  }

  public Observable<PaymentConfirmation> getConfirmation(Product product) {
    return confirmationRepository.getPaymentConfirmation(product)
        .distinctUntilChanged(confirmation -> confirmation.getStatus());
  }

  public Single<Purchase> getPurchase(Product product) {
    return productRepository.getPurchase(product);
  }
}
