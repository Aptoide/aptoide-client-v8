package cm.aptoide.pt.v8engine.billing.methods.braintree;

import cm.aptoide.pt.v8engine.billing.PaymentMethod;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.billing.exception.PaymentMethodNotAuthorizedException;
import cm.aptoide.pt.v8engine.billing.repository.TransactionRepository;
import rx.Completable;

public class BraintreeCreditCard implements PaymentMethod {

  private final int id;
  private final String name;
  private final String description;
  private final TransactionRepository transactionRepository;

  public BraintreeCreditCard(int id, String name, String description,
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
    return transactionRepository.createTransaction(getId(), product)
        .flatMapCompletable(transaction -> {
          if (transaction.isPendingAuthorization()) {
            return Completable.error(new PaymentMethodNotAuthorizedException(
                "Pending Braintree Credit Cards authorization."));
          }

          if (transaction.isFailed()) {
            return Completable.error(
                new PaymentFailureException("Braintree Credit Cards payment failed."));
          }

          return Completable.complete();
        });
  }
}
