/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import cm.aptoide.pt.v8engine.payment.repository.PaymentAuthorizationFactory;
import cm.aptoide.pt.v8engine.payment.repository.PaymentAuthorizationRepository;
import cm.aptoide.pt.v8engine.payment.repository.PaymentConfirmationRepository;
import cm.aptoide.pt.v8engine.payment.repository.PaymentRepository;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class AptoidePay {

  private final PaymentConfirmationRepository confirmationRepository;
  private final PaymentAuthorizationRepository authorizationRepository;
  private final PaymentAuthorizationFactory authorizationFactory;
  private final PaymentRepository paymentRepository;
  private final ProductRepository productRepository;
  private final Payer payer;

  public AptoidePay(PaymentConfirmationRepository confirmationRepository,
      PaymentAuthorizationRepository authorizationRepository,
      PaymentAuthorizationFactory authorizationFactory, PaymentRepository paymentRepository,
      ProductRepository productRepository, Payer payer) {
    this.confirmationRepository = confirmationRepository;
    this.authorizationRepository = authorizationRepository;
    this.authorizationFactory = authorizationFactory;
    this.paymentRepository = paymentRepository;
    this.productRepository = productRepository;
    this.payer = payer;
  }

  public Observable<List<Payment>> payments() {
    return paymentRepository.getPayments(payer.getId());
  }

  public Observable<Payment> payment(int paymentId) {
    return paymentRepository.getPayment(paymentId, payer.getId());
  }

  public Completable initiate(Payment payment) {
    if (payment.getAuthorization()
        .isAuthorized() || payment.getAuthorization()
        .isInitiated()) {
      return Completable.complete();
    }
    return authorizationRepository.createPaymentAuthorization(payment.getId());
  }

  public Completable authorize(int paymentId) {
    return authorizationRepository.saveAuthorization(
        authorizationFactory.create(paymentId, Authorization.Status.PENDING, payer.getId()));
  }

  public Completable process(Payment payment, Product product) {
    return payment.process(product);
  }

  public Observable<PaymentConfirmation> confirmation(Product product) {
    return paymentRepository.getConfirmation(product, payer.getId());
  }

  public Single<Purchase> purchase(Product product) {
    return productRepository.getPurchase(product);
  }
}
