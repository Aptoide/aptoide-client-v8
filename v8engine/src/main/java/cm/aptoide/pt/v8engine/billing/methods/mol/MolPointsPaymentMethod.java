package cm.aptoide.pt.v8engine.billing.methods.mol;

import cm.aptoide.pt.v8engine.billing.PaymentMethod;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.billing.exception.PaymentLocalProcessingRequiredException;
import cm.aptoide.pt.v8engine.billing.exception.PaymentMethodAlreadyAuthorizedException;
import cm.aptoide.pt.v8engine.billing.repository.TransactionRepositorySelector;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class MolPointsPaymentMethod implements PaymentMethod {

  private final int id;
  private final String name;
  private final String description;
  private final TransactionRepositorySelector transactionRepositorySelector;

  public MolPointsPaymentMethod(int id, String name, String description,
      TransactionRepositorySelector transactionRepositorySelector) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.transactionRepositorySelector = transactionRepositorySelector;
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
        .createTransaction(id, product)
        .andThen(transactionRepositorySelector.select(product)
            .getTransaction(product))
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof RepositoryIllegalArgumentException) {
            return transactionRepositorySelector.select(product)
                .getTransaction(product);
          }
          return Observable.error(throwable);
        })
        .takeUntil(transaction -> transaction.isCompleted())
        .flatMap(transaction -> {

          if (transaction.isPendingAuthorization()) {
            return Observable.error(new PaymentLocalProcessingRequiredException(
                "MOL Points requires website authorization."));
          }

          if (transaction.isFailed() || transaction.isNew()) {
            return Observable.error(new PaymentFailureException(
                "MOL Points transaction failed with status: " + transaction.getStatus()));
          }

          return Observable.empty();
        })
        .toCompletable();
  }

  public Single<MolTransaction> getTransaction(Product product) {
    return transactionRepositorySelector.select(product)
        .getTransaction(product)
        .takeUntil(transaction -> transaction.isPendingAuthorization())
        .cast(MolTransaction.class)
        .flatMap(transaction -> {

          if (!transaction.isPendingAuthorization()) {
            return Observable.error(new PaymentMethodAlreadyAuthorizedException());
          }
          return Observable.just(transaction);
        })
        .toSingle();
  }
}
