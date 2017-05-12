package cm.aptoide.pt.v8engine.billing.services.web;

import cm.aptoide.pt.v8engine.billing.Authorization;
import cm.aptoide.pt.v8engine.billing.Payer;
import cm.aptoide.pt.v8engine.billing.authorizations.WebAuthorization;
import cm.aptoide.pt.v8engine.billing.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.billing.exception.PaymentNotAuthorizedException;
import cm.aptoide.pt.v8engine.billing.repository.PaymentAuthorizationFactory;
import cm.aptoide.pt.v8engine.billing.repository.PaymentAuthorizationRepository;
import cm.aptoide.pt.v8engine.billing.repository.PaymentRepositoryFactory;
import cm.aptoide.pt.v8engine.billing.services.AuthorizationPayment;
import rx.Completable;
import rx.Observable;

public class WebAuthorizationPayment extends AuthorizationPayment {

  private final PaymentAuthorizationRepository authorizationRepository;
  private final PaymentAuthorizationFactory authorizationFactory;
  private final Payer payer;

  public WebAuthorizationPayment(int id, String name, String description,
      PaymentRepositoryFactory paymentRepositoryFactory,
      PaymentAuthorizationRepository authorizationRepository, boolean authorizationRequired,
      PaymentAuthorizationFactory authorizationFactory, Payer payer) {
    super(id, name, description, paymentRepositoryFactory);
    this.authorizationRepository = authorizationRepository;
    this.authorizationFactory = authorizationFactory;
    this.payer = payer;
  }

  @Override public Completable authorize() {
    return getAuthorization().takeUntil(authorization -> authorization.isAuthorized())
        .flatMapCompletable(authorization -> {

          if (authorization.isAuthorized()) {
            return Completable.complete();
          }

          if (authorization.isPendingUserConsent()) {
            return authorizationRepository.createPaymentAuthorization(getId())
                .andThen(Completable.error(
                    new PaymentNotAuthorizedException("Authorization missing user consent.")));
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

  @Override public Observable<WebAuthorization> getAuthorization() {
    return authorizationRepository.getPaymentAuthorization(getId())
        .distinctUntilChanged(authorization -> authorization.getStatus())
        .cast(WebAuthorization.class);
  }
}