package cm.aptoide.pt.v8engine.billing.services;

import cm.aptoide.pt.v8engine.billing.Authorization;
import cm.aptoide.pt.v8engine.billing.Payer;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.billing.exception.PaymentNotAuthorizedException;
import cm.aptoide.pt.v8engine.billing.repository.AuthorizationFactory;
import cm.aptoide.pt.v8engine.billing.repository.AuthorizationRepository;
import cm.aptoide.pt.v8engine.billing.repository.PaymentRepositoryFactory;
import rx.Completable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class BoaCompraPayment extends AptoidePayment {

  private final Payer payer;
  private final AuthorizationRepository authorizationRepository;
  private final AuthorizationFactory authorizationFactory;

  public BoaCompraPayment(int id, String name, String description,
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

  public Observable<WebAuthorization> getAuthorization() {
    return authorizationRepository.getPaymentAuthorization(getId())
        .distinctUntilChanged(authorization -> authorization.getStatus())
        .cast(WebAuthorization.class);
  }

  protected Completable checkAuthorization() {
    return getAuthorization().takeUntil(authorization -> authorization.isAuthorized())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMapCompletable(authorization -> {

          if (authorization.isInactive()) {
            return authorizationRepository.createWebPaymentAuthorization(getId());
          }

          if (authorization.isPendingUserConsent()) {
            return Completable.error(new PaymentNotAuthorizedException("Pending web user consent"));
          }

          if (authorization.isAuthorized()) {
            return Completable.complete();
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