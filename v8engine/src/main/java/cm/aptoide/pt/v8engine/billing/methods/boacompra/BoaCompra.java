package cm.aptoide.pt.v8engine.billing.methods.boacompra;

import cm.aptoide.pt.v8engine.billing.PaymentMethod;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.billing.exception.PaymentMethodAlreadyAuthorizedException;
import cm.aptoide.pt.v8engine.billing.exception.PaymentMethodNotAuthorizedException;
import cm.aptoide.pt.v8engine.billing.repository.AuthorizationRepository;
import cm.aptoide.pt.v8engine.billing.repository.TransactionRepository;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class BoaCompra implements PaymentMethod {

  private final int id;
  private final String name;
  private final String description;
  private final TransactionRepository transactionRepository;
  private final AuthorizationRepository authorizationRepository;

  public BoaCompra(int id, String name, String description,
      TransactionRepository transactionRepository,
      AuthorizationRepository authorizationRepository) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.transactionRepository = transactionRepository;
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
    return transactionRepository.createTransaction(getId(), product)
        .flatMapCompletable(transaction -> {
          if (transaction.isPendingAuthorization()) {
            return Completable.error(
                new PaymentMethodNotAuthorizedException("Pending Boa Compra local authorization."));
          }

          if (transaction.isFailed()) {
            return Completable.error(new PaymentFailureException("Boa Compra payment failed."));
          }

          return Completable.complete();
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

  public Completable processAuthorized(Product product) {
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