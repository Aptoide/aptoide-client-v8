/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import cm.aptoide.pt.v8engine.repository.PaymentRepository;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by marcelobenites on 18/11/16.
 */

public class AptoideSyncAdapter extends AbstractThreadedSyncAdapter {

  private final List<AbstractSync> syncs;

  public AptoideSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
    super(context, autoInitialize, allowParallelSyncs);
    final PaymentRepository paymentRepository = RepositoryFactory.getPaymentRepository(context);
    syncs = new ArrayList<>();
    syncs.add(new PaymentAuthorizationSync(paymentRepository));
    syncs.add(new PaymentConfirmationSync(paymentRepository));
  }

  @Override public void onPerformSync(Account account, Bundle extras, String authority,
      ContentProviderClient provider, SyncResult syncResult) {
    for (AbstractSync sync: syncs) {
      sync.sync(syncResult);
    }
  }
}