package cm.aptoide.pt.v8engine.billing.transaction;

import cm.aptoide.pt.v8engine.billing.Product;
import rx.Single;

public interface TransactionService {

  Single<Transaction> getTransaction(String sellerId, String payerId, Product product);

  Single<Transaction> createTransaction(String sellerId, String payerId, int paymentMethodId,
      Product product, String metadata);

  Single<Transaction> createTransaction(String sellerId, String payerId, int paymentMethodId,
      Product product, String metadata, String payload);
}
