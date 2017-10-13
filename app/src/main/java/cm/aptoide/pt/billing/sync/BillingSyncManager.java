/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.billing.sync;

import cm.aptoide.pt.billing.BillingSyncScheduler;
import cm.aptoide.pt.sync.Sync;
import cm.aptoide.pt.sync.SyncScheduler;
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

  @Override public void syncAuthorization(String transactionId) {
    final Sync sync = syncFactory.createAuthorizationSync(transactionId);
    currentSyncs.add(sync.getId());
    syncScheduler.schedule(sync);
  }

  @Override public void syncTransaction(String productId) {
    final Sync sync = syncFactory.createTransactionSync(productId);
    currentSyncs.add(sync.getId());
    syncScheduler.schedule(sync);
  }

  @Override public void stopSyncs() {
    for (String syncId : currentSyncs) {
      syncScheduler.cancel(syncId);
    }
  }
}