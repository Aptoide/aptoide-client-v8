package cm.aptoide.pt.v8engine.billing.services;

import cm.aptoide.pt.v8engine.billing.Authorization;
import cm.aptoide.pt.v8engine.billing.Payer;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.billing.exception.PaymentNotAuthorizedException;
import cm.aptoide.pt.v8engine.billing.repository.AuthorizationFactory;
import cm.aptoide.pt.v8engine.billing.repository.AuthorizationRepository;
import cm.aptoide.pt.v8engine.billing.repository.PaymentRepositoryFactory;
import cm.aptoide.pt.v8engine.billing.services.AptoidePayment;
import rx.Completable;
import rx.Observable;

public class AuthorizedPayment extends AptoidePayment {

  private final Payer payer;
  private final AuthorizationRepository authorizationRepository;
  private final AuthorizationFactory authorizationFactory;

  public AuthorizedPayment(int id, String name, String description,
      PaymentRepositoryFactory paymentRepositoryFactory, Payer payer,
      AuthorizationRepository authorizationRepository, AuthorizationFactory authorizationFactory) {
    super(id, name, description, paymentRepositoryFactory);
    this.payer = payer;
    this.authorizationRepository = authorizationRepository;
    this.authorizationFactory = authorizationFactory;
  }

  @Override public Completable process(Product product) {
    return checkAuthorization().andThen(super.process(product));
  }

  public Observable<? extends Authorization> getAuthorization() {
    return authorizationRepository.getPaymentAuthorization(getId())
        .distinctUntilChanged(authorization -> authorization.getStatus());
  }

  protected Completable checkAuthorization() {
    return getAuthorization().takeUntil(authorization -> authorization.isAuthorized())
        .flatMapCompletable(authorization -> {

          if (authorization.isAuthorized()) {
            return Completable.complete();
          }

          if (authorization.isPendingUserConsent()) {
            return Completable.error(
                new PaymentNotAuthorizedException("Authorization missing user consent."));
          }

          if (authorization.isFailed()) {
            return payer.getId()
                .flatMapCompletable(payerId -> authorizationRepository.saveAuthorization(
                    authorizationFactory.create(getId(), Authorization.Status.INACTIVE, payerId)))
                .andThen(
                    Completable.error(new PaymentFailureException("Payment authorization failed")));
          }

          return Completable.complete();
        })
        .toCompletable();
  }
}