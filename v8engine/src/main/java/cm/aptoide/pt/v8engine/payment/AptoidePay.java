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
  private final ProductRepository productRepository;

  public AptoidePay(PaymentConfirmationRepository confirmationRepository,
      PaymentAuthorizationRepository authorizationRepository, ProductRepository productRepository,
      PaymentAuthorizationFactory authorizationFactory) {
    this.confirmationRepository = confirmationRepository;
    this.authorizationRepository = authorizationRepository;
    this.productRepository = productRepository;
    this.authorizationFactory = authorizationFactory;
  }

  public Observable<List<Payment>> availablePayments(AptoideProduct product) {
    return productRepository.getPayments(product)
        .flatMapObservable(payments -> getPaymentsWithAuthorizations(payments));
  }

  public Completable authorize(Payment payment) {
    return authorizationRepository.createPaymentAuthorization(payment.getId())
        .andThen(authorizationRepository.getPaymentAuthorization(payment.getId()))
        .takeUntil(authorization -> authorization.isInitiated() || authorization.isInvalid())
        .filter(authorization -> authorization.isInitiated())
        .doOnNext(authorization -> authorization.authorize())
        .toCompletable();
  }

  public Observable<PaymentConfirmation> getConfirmation(AptoideProduct product) {
    return confirmationRepository.getPaymentConfirmation(product);
  }

  public Completable process(Payment payment) {
    return payment.process();
  }

  private Observable<List<Payment>> getPaymentsWithAuthorizations(List<Payment> payments) {
    return getAuthorizationRequiredPaymentIds(payments).flatMapObservable(
        paymentIds -> authorizationRepository.getPaymentAuthorizations(paymentIds))
        .flatMap(authorizations -> addAuthorization(payments, authorizations))
        .map(success -> payments);
  }

  private Observable<Void> addAuthorization(List<Payment> payments,
      List<Authorization> authorizations) {
    return Observable.from(payments)
        .filter(payment -> {
          if (payment.isAuthorizationRequired()) {
            return true;
          }
          payment.setAuthorization(authorizationFactory.create(payment.getId(), Authorization.Status.NONE));
          return false;
        })
        .flatMap(authorizationRequiredPayment -> Observable.from(authorizations)
            .doOnNext(authorization -> {
              if (authorizationRequiredPayment.getId() == authorization.getPaymentId()) {
                authorizationRequiredPayment.setAuthorization(authorization);
              }
            }))
        .toList()
        .map(success -> null);
  }

  private Single<List<Integer>> getAuthorizationRequiredPaymentIds(List<Payment> payments) {
    return Observable.from(payments)
        .filter(payment -> payment.isAuthorizationRequired())
        .map(payment -> payment.getId())
        .toList()
        .toSingle();
  }
}