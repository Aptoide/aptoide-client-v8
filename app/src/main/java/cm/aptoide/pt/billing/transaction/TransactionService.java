package cm.aptoide.pt.billing.transaction;

import java.util.List;
import rx.Single;

public interface TransactionService {

  Single<List<Transaction>> getTransactions();

  Single<Transaction> createTransaction(long productId, int serviceId, String payload);
}
