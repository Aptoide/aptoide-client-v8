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

  public Completable initiate(Payment payment) {
    if (isAuthorized(payment)) {
      return Completable.complete();
    }
    return authorizationRepository.createPaymentAuthorization(payment.getId());
  }

  public Observable<Authorization> getAuthorization(int paymentId) {
    return authorizationRepository.getPaymentAuthorization(paymentId, payer.getId());
  }

  public Completable authorize(int paymentId) {
    return authorizationRepository.saveAuthorization(
        authorizationFactory.create(paymentId, Authorization.Status.PENDING, payer.getId()));
  }

  public Completable process(Payment payment) {
    return Completable.defer(() -> {
      if (isAuthorized(payment)) {
        return payment.process();
      }
      return Completable.error(new PaymentFailureException("Payment not authorized."));
    });
  }

  private boolean isAuthorized(Payment payment) {
    return payment.getAuthorization().getStatus().equals(Authorization.Status.ACTIVE);
  }

  public Observable<Payment.Status> getStatus(List<Payment> payments) {
    return Observable.from(payments).map(payment -> payment.getStatus()).toList().map(status -> {
      if (status.contains(Payment.Status.COMPLETED)) {
        return Payment.Status.COMPLETED;
      }

      if (status.contains(Payment.Status.PROCESSING)) {
        return Payment.Status.PROCESSING;
      }
      return Payment.Status.NEW;
    });
  }
}
