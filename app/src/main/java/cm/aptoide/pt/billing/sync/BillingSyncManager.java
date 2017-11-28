/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.billing.sync;

import cm.aptoide.pt.billing.BillingSyncScheduler;
import cm.aptoide.pt.sync.Sync;
import cm.aptoide.pt.sync.SyncScheduler;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BillingSyncManager implements BillingSyncScheduler {

  private final BillingSyncFactory syncFactory;
  private final SyncScheduler syncScheduler;
  private final Set<String> currentSyncs;
  private final Map<String, String> authorizationSyncs;

  public BillingSyncManager(BillingSyncFactory syncFactory, SyncScheduler syncScheduler,
      Set<String> currentSyncs, Map<String, String> authorizationSyncs) {
    this.syncFactory = syncFactory;
    this.syncScheduler = syncScheduler;
    this.currentSyncs = currentSyncs;
    this.authorizationSyncs = authorizationSyncs;
  }

  @Override public void syncAuthorization(String transactionId) {
    final Sync sync = syncFactory.createAuthorizationSync(transactionId);
    currentSyncs.add(sync.getId());
    authorizationSyncs.put(transactionId, sync.getId());
    syncScheduler.schedule(sync);
  }

  @Override public void syncTransaction(String productId) {
    final Sync sync = syncFactory.createTransactionSync(productId);
    currentSyncs.add(sync.getId());
    syncScheduler.schedule(sync);
  }

  @Override public void stopSyncs() {

    final Set<String> cancelledSyncIds = new HashSet<>();
    for (String syncId : currentSyncs) {
      syncScheduler.cancel(syncId);
      cancelledSyncIds.add(syncId);
    }

    for (String cancelledSyncId : cancelledSyncIds) {
      currentSyncs.remove(cancelledSyncId);
      authorizationSyncs.remove(cancelledSyncId);
    }
  }

  @Override public void cancelAuthorizationSync(String transactionId) {
    final String syncId = authorizationSyncs.get(transactionId);

    if (syncId != null) {
      syncScheduler.cancel(syncId);
      authorizationSyncs.remove(syncId);
      currentSyncs.remove(syncId);
    }
  }
}