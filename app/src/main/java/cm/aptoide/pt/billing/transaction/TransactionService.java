package cm.aptoide.pt.billing.transaction;

import java.util.List;
import rx.Single;

public interface TransactionService {

  Single<Transaction> getTransaction(long productId);

  Single<Transaction> createTransaction(long productId, long serviceId, String payload);
}
