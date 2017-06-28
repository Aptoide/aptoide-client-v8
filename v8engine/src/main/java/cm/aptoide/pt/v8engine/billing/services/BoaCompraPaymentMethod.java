package cm.aptoide.pt.v8engine.billing.services;

import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.exception.PaymentAuthorizationAlreadyInitializedException;
import cm.aptoide.pt.v8engine.billing.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.billing.exception.PaymentMethodNotAuthorizedException;
import cm.aptoide.pt.v8engine.billing.repository.AuthorizationRepository;
import cm.aptoide.pt.v8engine.billing.repository.TransactionRepositoryFactory;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class BoaCompraPaymentMethod extends AptoidePaymentMethod {

  private final TransactionRepositoryFactory transactionRepositoryFactory;
  private final AuthorizationRepository authorizationRepository;

  public BoaCompraPaymentMethod(int id, String name, String description,
      TransactionRepositoryFactory transactionRepositoryFactory,
      AuthorizationRepository authorizationRepository) {
    super(id, name, description);
    this.transactionRepositoryFactory = transactionRepositoryFactory;
    this.authorizationRepository = authorizationRepository;
  }

  @Override public Completable process(Product product) {
    return transactionRepositoryFactory.getTransactionRepository(product)
        .createTransaction(getId(), product)
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof RepositoryIllegalArgumentException) {
            return Completable.error(
                new PaymentMethodNotAuthorizedException("Payment not authorized."));
          }
          return Completable.error(throwable);
        });
  }

  public Single<BoaCompraAuthorization> getInitializedAuthorization() {
    return authorizationRepository.getAuthorization(getId())
        .cast(BoaCompraAuthorization.class)
        .flatMap(authorization -> {
          if (authorization.isInitialized()) {
            return Observable.just(authorization);
          }

          if (authorization.isPending() || authorization.isAuthorized()) {
            return Observable.error(new PaymentAuthorizationAlreadyInitializedException());
          }

          return authorizationRepository.createAuthorization(getId())
              .toObservable();
        })
        .first()
        .toSingle();
  }

  public Completable authorizedProcess(Product product) {
    return authorizationRepository.getAuthorization(getId())
        .takeUntil(authorization -> authorization.isAuthorized())
        .flatMapCompletable(authorization -> {

          if (authorization.isInactive() || authorization.isFailed()) {
            return Completable.error(new PaymentFailureException("Payment not authorized."));
          }

          if (authorization.isAuthorized()) {
            return process(product);
          }

          return Completable.complete();
        })
        .toCompletable();
  }
}