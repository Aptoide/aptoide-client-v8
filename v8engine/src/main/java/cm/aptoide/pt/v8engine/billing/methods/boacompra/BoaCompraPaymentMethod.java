package cm.aptoide.pt.v8engine.billing.methods.boacompra;

import cm.aptoide.pt.v8engine.billing.PaymentMethod;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.billing.exception.PaymentMethodAlreadyAuthorizedException;
import cm.aptoide.pt.v8engine.billing.exception.PaymentMethodNotAuthorizedException;
import cm.aptoide.pt.v8engine.billing.repository.AuthorizationRepository;
import cm.aptoide.pt.v8engine.billing.repository.TransactionRepositorySelector;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class BoaCompraPaymentMethod implements PaymentMethod {

  private final int id;
  private final String name;
  private final String description;
  private final TransactionRepositorySelector transactionRepositorySelector;
  private final AuthorizationRepository authorizationRepository;

  public BoaCompraPaymentMethod(int id, String name, String description,
      TransactionRepositorySelector transactionRepositorySelector,
      AuthorizationRepository authorizationRepository) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.transactionRepositorySelector = transactionRepositorySelector;
    this.authorizationRepository = authorizationRepository;
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

  @Override public Completable process(Product product) {
    return transactionRepositorySelector.select(product)
        .createTransaction(getId(), product)
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof RepositoryIllegalArgumentException) {
            return Completable.error(
                new PaymentMethodNotAuthorizedException("Payment not authorized."));
          }
          return Completable.error(throwable);
        });
  }

  public Single<BoaCompraAuthorization> getAuthorization() {
    return authorizationRepository.getAuthorization(getId())
        .cast(BoaCompraAuthorization.class)
        .flatMap(authorization -> {
          if (authorization.isInitialized()) {
            return Observable.just(authorization);
          }

          if (authorization.isPending() || authorization.isAuthorized()) {
            return Observable.error(new PaymentMethodAlreadyAuthorizedException());
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