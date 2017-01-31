/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

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
        .flatMapObservable(payments -> getPaymentsWithAuthorizations(payments, payer.getId()));
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
    return authorizationRepository.saveAuthorization(authorizationFactory.create(paymentId,
        Authorization.Status.PENDING, payer.getId()));
  }

  public Observable<PaymentConfirmation> getConfirmation(AptoideProduct product) {
    return confirmationRepository.getPaymentConfirmation(product, payer.getId());
  }

  public Completable process(Payment payment) {
    return payment.process();
  }

  private Observable<List<Payment>> getPaymentsWithAuthorizations(List<Payment> payments,
      String payerId) {
    return getAuthorizationRequiredPaymentIds(payments).flatMapObservable(
        paymentIds -> authorizationRepository.getPaymentAuthorizations(paymentIds, payerId))
        .flatMap(authorizations -> addAuthorizations(payments, authorizations, payerId))
        .map(success -> payments);
  }

  private Observable<Void> addAuthorizations(List<Payment> payments,
      List<Authorization> authorizations, String payerId) {
    return Observable.from(payments)
        .flatMap(payment -> addAuthorization(authorizations, payment).map(success -> payment))
        .doOnNext(payment -> {
          if (payment.isAuthorizationRequired()) {
            if (payment.getAuthorization() == null) {
              payment.setAuthorization(
                  authorizationFactory.create(payment.getId(), Authorization.Status.INACTIVE,
                      payerId));
            }
          } else {
            payment.setAuthorization(
              authorizationFactory.create(payment.getId(), Authorization.Status.NONE, payerId));
          }
        })
        .toList()
        .map(success -> null);
  }

  private Observable<Void> addAuthorization(List<Authorization> payment,
      Payment authorizationRequiredPayment) {
    return Observable.from(payment).doOnNext(authorization -> {
      if (authorizationRequiredPayment.getId() == authorization.getPaymentId()) {
        authorizationRequiredPayment.setAuthorization(authorization);
      }
    }).toList().map(success -> null);
  }

  private Single<List<Integer>> getAuthorizationRequiredPaymentIds(List<Payment> payments) {
    return Observable.from(payments)
        .filter(payment -> payment.isAuthorizationRequired())
        .map(payment -> payment.getId())
        .toList()
        .toSingle();
  }

}
