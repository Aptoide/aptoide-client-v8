package cm.aptoide.pt.v8engine.billing;

import rx.Completable;

public interface BillingSyncScheduler {

  Completable scheduleAuthorizationSync(int paymentId);

  Completable scheduleTransactionSync(Product product);
}
