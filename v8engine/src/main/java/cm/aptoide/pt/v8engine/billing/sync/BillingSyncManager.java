/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.billing.sync;

import cm.aptoide.pt.v8engine.billing.BillingSyncScheduler;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.sync.Sync;
import cm.aptoide.pt.v8engine.sync.SyncScheduler;
import java.util.Set;

public class BillingSyncManager implements BillingSyncScheduler {

  private final BillingSyncFactory syncFactory;
  private final SyncScheduler syncScheduler;
  private final Set<String> currentSyncs;

  public BillingSyncManager(BillingSyncFactory syncFactory, SyncScheduler syncScheduler,
      Set<String> currentSyncs) {
    this.syncFactory = syncFactory;
    this.syncScheduler = syncScheduler;
    this.currentSyncs = currentSyncs;
  }

  @Override public void syncAuthorization(int paymentId) {
    final Sync sync = syncFactory.createAuthorizationSync(paymentId);
    currentSyncs.add(sync.getId());
    syncScheduler.schedule(sync);
  }

  @Override public void syncTransaction(String sellerId, Product product) {
    final Sync sync = syncFactory.createTransactionSync(sellerId, product);
    currentSyncs.add(sync.getId());
    syncScheduler.schedule(sync);
  }

  public void cancelAll() {
    for (String syncId : currentSyncs) {
      syncScheduler.cancel(syncId);
    }
  }
}