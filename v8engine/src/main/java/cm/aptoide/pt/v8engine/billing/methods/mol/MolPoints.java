package cm.aptoide.pt.v8engine.billing.methods.mol;

import cm.aptoide.pt.v8engine.billing.PaymentMethod;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.billing.exception.PaymentMethodAlreadyAuthorizedException;
import cm.aptoide.pt.v8engine.billing.exception.PaymentMethodNotAuthorizedException;
import cm.aptoide.pt.v8engine.billing.repository.TransactionRepository;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class MolPoints implements PaymentMethod {

  private final int id;
  private final String name;
  private final String description;
  private final TransactionRepository transactionRepository;

  public MolPoints(int id, String name, String description,
      TransactionRepository transactionRepository) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.transactionRepository = transactionRepository;
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
    return transactionRepository.createTransaction(id, product)
        .flatMapCompletable(transaction -> {
          if (transaction.isPendingAuthorization()) {
            return Completable.error(
                new PaymentMethodNotAuthorizedException("Pending MolPoints local authorization."));
          }

          if (transaction.isFailed()) {
            return Completable.error(new PaymentFailureException("MolPoints payment failed."));
          }

          return Completable.complete();
        });
  }

  public Single<MolTransaction> getTransaction(Product product) {
    return transactionRepository.getTransaction(product)
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
