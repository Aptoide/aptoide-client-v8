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
import cm.aptoide.pt.v8engine.repository.ProductRepository;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

/**
 * Created by marcelobenites on 8/12/16.
 */
public class AptoidePay {

  private final PaymentConfirmationRepository confirmationRepository;
  private final PaymentAuthorizationRepository authorizationRepository;
  private final PaymentAuthorizationFactory authorizationFactory;
  private final Payer payer;
  private final ProductRepository productRepository;

  public AptoidePay(PaymentConfirmationRepository confirmationRepository,
      PaymentAuthorizationRepository authorizationRepository, ProductRepository productRepository,
      PaymentAuthorizationFactory authorizationFactory, Payer payer) {
    this.confirmationRepository = confirmationRepository;
    this.authorizationRepository = authorizationRepository;
    this.productRepository = productRepository;
    this.authorizationFactory = authorizationFactory;
    this.payer = payer;
  }

  public Observable<List<Payment>> availablePayments(AptoideProduct product) {
    return productRepository.getPayments(product)
        .flatMapObservable(payments -> Observable.combineLatest(getConfirmations(product, payments),
            getAuthorizations(payments, payer.getId()), Observable.just(payments),
            (confirmations, authorizations, paymentList) -> {
              if (payments.isEmpty() || authorizations.isEmpty() || confirmations.isEmpty()) {
                return Observable.<List<Payment>>empty();
              }
              return getCompletePayments(payments, authorizations, payer.getId(), confirmations);
            }))
        .flatMap(result -> result);
  }

  public Completable initiate(Authorization authorization) {
    if (authorization.isInitiated()) {
      return Completable.complete();
    }
    return authorizationRepository.createPaymentAuthorization(authorization.getPaymentId());
  }

  public Observable<Authorization> getAuthorization(int paymentId) {
    return authorizationRepository.getPaymentAuthorization(paymentId, payer.getId());
  }

  public Completable authorize(int paymentId) {
    return authorizationRepository.saveAuthorization(
        authorizationFactory.create(paymentId, Authorization.Status.PENDING, payer.getId()));
  }

  private Observable<List<PaymentConfirmation>> getConfirmations(AptoideProduct product,
      List<Payment> payments) {
    return getPaymentIds(payments).flatMapObservable(
        paymentIds -> confirmationRepository.getPaymentConfirmations(product, payer.getId(),
            paymentIds));
  }

  public Completable process(Payment payment) {
    return Completable.defer(() -> {
      if (isAuthorized(payment)) {
        return payment.process();
      }
      return Completable.error(new PaymentFailureException("Payment not authorized."));
    });
  }

  private Observable<List<Authorization>> getAuthorizations(List<Payment> payments,
      String payerId) {
    return getPaymentIds(payments).flatMapObservable(
        paymentIds -> authorizationRepository.getPaymentAuthorizations(paymentIds, payerId));
  }

  private Single<List<Integer>> getPaymentIds(List<Payment> payments) {
    return Observable.from(payments).map(payment -> payment.getId()).toList().toSingle();
  }

  private Observable<List<Payment>> getCompletePayments(List<Payment> payments,
      List<Authorization> authorizations, String payerId, List<PaymentConfirmation> confirmations) {
    return Observable.zip(Observable.from(payments), Observable.from(authorizations),
        Observable.from(confirmations), (payment, authorization, confirmation) -> {
          if (!payment.isAuthorizationRequired()) {
            payment.setAuthorization(
                authorizationFactory.create(payment.getId(), Authorization.Status.NONE, payerId));
          } else {
            payment.setAuthorization(authorization);
          }
          payment.setConfirmation(confirmation);
          return payment;
        }).toList();
  }

  private boolean isAuthorized(Payment payment) {
    if (payment.isAuthorizationRequired()) {
      return payment.getAuthorization().isAuthorized();
    }
    return true;
  }

  public Observable<Payment.Status> getStatus(List<Payment> payments) {
    return Observable.from(payments).map(payment -> payment.getStatus()).toList().map(status -> {
      if (status.contains(Payment.Status.COMPLETED)) {
        return Payment.Status.COMPLETED;
      }
      if (status.contains(Payment.Status.PENDING)) {
        return Payment.Status.PENDING;
      }
      return Payment.Status.NEW;
    });
  }
}
