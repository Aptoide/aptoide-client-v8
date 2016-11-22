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
import cm.aptoide.pt.database.realm.PaymentAuthorization;
import cm.aptoide.pt.v8engine.repository.PaymentAuthorizationRepository;
import cm.aptoide.pt.v8engine.repository.PaymentConfirmationRepository;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcelobenites on 18/11/16.
 */

public class AptoideSyncAdapter extends AbstractThreadedSyncAdapter {

  private final List<AbstractSync> syncs;

  public AptoideSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
    super(context, autoInitialize, allowParallelSyncs);
    final PaymentAuthorizationRepository authorizationRepository =
        RepositoryFactory.getPaymentAuthorizationRepository(context);
    final PaymentConfirmationRepository paymentConfirmationRepository =
        RepositoryFactory.getPaymentConfirmationRepository(context);
    syncs = new ArrayList<>();
    syncs.add(new PaymentAuthorizationSync(authorizationRepository));
    syncs.add(new PaymentConfirmationSync(paymentConfirmationRepository));
  }

  @Override public void onPerformSync(Account account, Bundle extras, String authority,
      ContentProviderClient provider, SyncResult syncResult) {
    for (AbstractSync sync : syncs) {
      sync.sync(syncResult);
    }
  }
}