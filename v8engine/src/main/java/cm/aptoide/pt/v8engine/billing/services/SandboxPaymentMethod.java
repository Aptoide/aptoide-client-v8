package cm.aptoide.pt.v8engine.billing.services;

import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.repository.TransactionRepositoryFactory;
import rx.Completable;

public class SandboxPaymentMethod extends AptoidePaymentMethod {

  private final TransactionRepositoryFactory transactionRepositoryFactory;

  public SandboxPaymentMethod(int id, String name, String description,
      TransactionRepositoryFactory transactionRepositoryFactory) {
    super(id, name, description);
    this.transactionRepositoryFactory = transactionRepositoryFactory;
  }

  @Override public Completable process(Product product) {
    return transactionRepositoryFactory.getTransactionRepository(product)
        .createTransaction(getId(), product);
  }
}