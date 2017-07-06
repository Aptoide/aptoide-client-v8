package cm.aptoide.pt.v8engine.billing.methods.sandbox;

import cm.aptoide.pt.v8engine.billing.PaymentMethod;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.repository.TransactionRepositorySelector;
import rx.Completable;

public class SandboxPaymentMethod implements PaymentMethod {

  private final int id;
  private final String name;
  private final String description;
  private final TransactionRepositorySelector transactionRepositorySelector;

  public SandboxPaymentMethod(int id, String name, String description,
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
        .createTransaction(getId(), product);
  }
}