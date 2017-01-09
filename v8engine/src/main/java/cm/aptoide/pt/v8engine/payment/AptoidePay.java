/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import cm.aptoide.pt.v8engine.payment.products.AptoideProduct;
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
  private ProductRepository productRepository;

  public AptoidePay(PaymentConfirmationRepository confirmationRepository,
      PaymentAuthorizationRepository authorizationRepository, ProductRepository productRepository) {
    this.confirmationRepository = confirmationRepository;
    this.authorizationRepository = authorizationRepository;
    this.productRepository = productRepository;
  }

  public Observable<List<Payment>> availablePayments(AptoideProduct product) {
    return productRepository.getPayments(product)
        .flatMapObservable(payments -> getPaymentsWithAuthorizations(payments));
  }

  public Completable authorize(Payment payment) {
    return authorizationRepository.getPaymentAuthorization(payment.getId())
        .distinctUntilChanged(authorization -> authorization.getStatus())
        .flatMap(authorization -> {

          if (authorization.isAuthorized()) {
            return Observable.just(authorization);
          }

          if (authorization.isPending()) {
            return Observable.empty();
          }

          if (authorization.isInvalid()) {
            return authorizationRepository.createPaymentAuthorization(payment.getId())
                .andThen(Observable.empty());
          }

          authorization.authorize();
          return Observable.empty();
        })
        .first()
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

  private Observable<Void> addAuthorization(List<Payment> payments, List<Authorization> authorizations) {
    return Observable.from(authorizations)
        .flatMap(authorization -> Observable.from(payments)
            .filter(payment -> isPaymentAuthorization(authorization, payment))
            .doOnNext(orderedPayment -> orderedPayment.setAuthorization(authorization))
            .toList())
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

  private boolean isPaymentAuthorization(Authorization authorization, Payment payment) {
    return payment.isAuthorizationRequired() && (payment.getId() == authorization.getPaymentId());
  }
}