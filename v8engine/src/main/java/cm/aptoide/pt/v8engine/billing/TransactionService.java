package cm.aptoide.pt.v8engine.billing;

import rx.Single;

public interface TransactionService {

  Single<Transaction> getTransaction(Product product, String payerId);

  Single<Transaction> createTransaction(Product product, int paymentMethodId, String payerId,
      String metadata);

  Single<Transaction> createTransaction(Product product, int paymentMethodId, String payerId);
}
