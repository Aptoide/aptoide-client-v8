package cm.aptoide.pt.billing;

import rx.Completable;

public interface BillingSyncScheduler {

  Completable scheduleAuthorizationSync(int paymentId);

  Completable scheduleTransactionSync(Product product);
}
