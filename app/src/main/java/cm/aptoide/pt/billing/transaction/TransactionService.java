package cm.aptoide.pt.billing.transaction;

import cm.aptoide.pt.billing.Product;
import rx.Single;

public interface TransactionService {

  Single<Transaction> getTransaction(String merchantName, String customerId, Product product);

  Single<Transaction> createTransaction(String merchantName, String customerId, int paymentMethodId,
      Product product, String metadata);

  Single<Transaction> createTransaction(String merchantName, String customerId, int paymentMethodId,
      Product product, String metadata, String payload);
}
