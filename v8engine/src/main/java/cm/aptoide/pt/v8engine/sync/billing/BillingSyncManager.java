/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.sync.billing;

import cm.aptoide.pt.v8engine.billing.BillingSyncScheduler;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.sync.SyncScheduler;

public class BillingSyncManager implements BillingSyncScheduler {

  private final BillingSyncFactory syncFactory;
  private final SyncScheduler syncScheduler;

  public BillingSyncManager(BillingSyncFactory syncFactory, SyncScheduler syncScheduler) {
    this.syncFactory = syncFactory;
    this.syncScheduler = syncScheduler;
  }

  @Override public void syncAuthorization(int paymentId) {
    syncScheduler.schedule(syncFactory.createAuthorizationSync(paymentId));
  }

  @Override public void syncTransaction(Product product) {
    syncScheduler.schedule(syncFactory.createTransactionSync(product));
  }
}