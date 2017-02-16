/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import cm.aptoide.pt.v8engine.payment.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.payment.products.AptoideProduct;
import cm.aptoide.pt.v8engine.repository.PaymentAuthorizationFactory;
import cm.aptoide.pt.v8engine.repository.PaymentAuthorizationRepository;
import cm.aptoide.pt.v8engine.repository.PaymentConfirmationRepository;
import cm.aptoide.pt.v8engine.repository.PaymentRepository;
import java.util.Collections;
import java.util.List;
import rx.Completable;
import rx.Observable;

public class AptoidePay {

  private final PaymentConfirmationRepository confirmationRepository;
  private final PaymentAuthorizationRepository authorizationRepository;
  private final PaymentAuthorizationFactory authorizationFactory;
  private final Payer payer;
  private final PaymentRepository paymentRepository;

  public AptoidePay(PaymentConfirmationRepository confirmationRepository,
      PaymentAuthorizationRepository authorizationRepository,
      PaymentAuthorizationFactory authorizationFactory, Payer payer, PaymentRepository paymentRepository) {
    this.confirmationRepository = confirmationRepository;
    this.authorizationRepository = authorizationRepository;
    this.authorizationFactory = authorizationFactory;
    this.payer = payer;
    this.paymentRepository = paymentRepository;
  }

  public Observable<List<Payment>> availablePayments(AptoideProduct product) {
    return paymentRepository.getPayments(product);
  }

  public Observable<Payment> getPayment(int paymentId, AptoideProduct product) {
    return paymentRepository.getPayment(paymentId, product);
  }

  public Completable initiate(Payment payment) {
    if (payment.isAuthorized()) {
      return Completable.complete();
    }
    return authorizationRepository.createPaymentAuthorization(payment.getId());
  }

  public Completable authorize(int paymentId) {
    return authorizationRepository.saveAuthorization(
        authorizationFactory.create(paymentId, Authorization.Status.PENDING, payer.getId()));
  }

  public Completable process(Payment payment) {
    return payment.process();
  }

  public Observable<PaymentConfirmation> getConfirmation(List<Payment> payments) {
    return Observable.from(payments).take(1).map(payment -> payment.getConfirmation());
  }
}
