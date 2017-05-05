/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 02/01/2017.
 */

package cm.aptoide.pt.v8engine.payment;

import cm.aptoide.pt.v8engine.payment.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.payment.exception.PaymentNotAuthorizedException;
import cm.aptoide.pt.v8engine.payment.repository.PaymentAuthorizationFactory;
import cm.aptoide.pt.v8engine.payment.repository.PaymentAuthorizationRepository;
import cm.aptoide.pt.v8engine.payment.repository.PaymentConfirmationRepository;
import rx.Completable;
import rx.Observable;

public class AptoidePayment implements Payment {

  private final PaymentConfirmationRepository confirmationRepository;
  private final int id;
  private final String name;
  private final String description;
  private final PaymentAuthorizationRepository authorizationRepository;
  private final boolean authorizationRequired;
  private final PaymentAuthorizationFactory authorizationFactory;
  private final Payer payer;

  public AptoidePayment(int id, String name, String description,
      PaymentConfirmationRepository confirmationRepository,
      PaymentAuthorizationRepository authorizationRepository, boolean authorizationRequired,
      PaymentAuthorizationFactory authorizationFactory, Payer payer) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.confirmationRepository = confirmationRepository;
    this.authorizationRepository = authorizationRepository;
    this.authorizationRequired = authorizationRequired;
    this.authorizationFactory = authorizationFactory;
    this.payer = payer;
  }

  @Override public int getId() {
    return id;
  }

  @Override public String getName() {
    return name;
  }

  @Override public String getDescription() {
    return description;
  }

  @Override public Observable<Authorization> getAuthorization() {
    if (!authorizationRequired) {
      return payer.getId()
          .flatMapObservable(payerId -> Observable.just(
              authorizationFactory.create(id, Authorization.Status.NONE, payerId)));
    }
    return authorizationRepository.getPaymentAuthorization(id);
  }

  @Override public Observable<PaymentConfirmation> getConfirmation(Product product) {
    return confirmationRepository.getPaymentConfirmation(product);
  }

  @Override public Completable process(Product product) {
    if (authorizationRequired) {
      return getAuthorization().distinctUntilChanged()
          .takeUntil(
              authorization -> authorization.isAuthorized())
          .flatMapCompletable(authorization -> {

            if (authorization.isAuthorized()) {
              return confirmationRepository.createPaymentConfirmation(id, product);
            }

            if (authorization.isFailed()) {
              return payer.getId()
                  .flatMapCompletable(payerId -> authorizationRepository.saveAuthorization(
                      authorizationFactory.create(id, Authorization.Status.INACTIVE, payerId)))
                  .andThen(Completable.error(
                      new PaymentFailureException("Payment authorization failed")));
            }

            if (authorization.isPendingInitiation()) {
              return authorizationRepository.createPaymentAuthorization(id);
            }

            if (authorization.isInitiated()) {
              return Completable.error(new PaymentNotAuthorizedException(
                  "Can not process payment since it not authorized."));
            }

            return Completable.complete();
          })
          .toCompletable();
    }
    return confirmationRepository.createPaymentConfirmation(id, product);
  }
}