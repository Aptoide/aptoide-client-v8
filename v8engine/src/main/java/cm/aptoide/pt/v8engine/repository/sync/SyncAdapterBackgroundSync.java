/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.repository.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;
import cm.aptoide.pt.v8engine.payment.products.AptoideProduct;
import java.util.Collections;
import java.util.List;

/**
 * Created by marcelobenites on 22/11/16.
 */

public class SyncAdapterBackgroundSync {

  private final AptoidePreferencesConfiguration configuration;
  private final AccountManager accountManager;
  private final SyncDataConverter syncDataConverter;

  public SyncAdapterBackgroundSync(AptoidePreferencesConfiguration configuration,
      AccountManager accountManager, SyncDataConverter converter) {
    this.configuration = configuration;
    this.accountManager = accountManager;
    this.syncDataConverter = converter;
  }

  public void syncAuthorization(int paymentId) {
    syncAuthorizations(Collections.singletonList(String.valueOf(paymentId)));
  }

  public void syncAuthorizations(List<String> paymentIds) {
    final Bundle bundle = new Bundle();
    bundle.putBoolean(AptoideSyncAdapter.EXTRA_PAYMENT_AUTHORIZATIONS, true);
    bundle.putString(AptoideSyncAdapter.EXTRA_PAYMENT_IDS, syncDataConverter.toString(paymentIds));
    schedule(bundle);
  }

  public void syncConfirmation(AptoideProduct product) {
    final Bundle bundle = syncDataConverter.toBundle(product);
    bundle.putBoolean(AptoideSyncAdapter.EXTRA_PAYMENT_CONFIRMATIONS, true);
    schedule(bundle);
  }

  public void syncConfirmation(AptoideProduct product, int paymentId,
      String paymentConfirmationId) {
    final Bundle bundle = syncDataConverter.toBundle(product);
    bundle.putBoolean(AptoideSyncAdapter.EXTRA_PAYMENT_CONFIRMATIONS, true);
    bundle.putString(AptoideSyncAdapter.EXTRA_PAYMENT_CONFIRMATION_ID, paymentConfirmationId);
    bundle.putString(AptoideSyncAdapter.EXTRA_PAYMENT_IDS,
        syncDataConverter.toString(Collections.singletonList(String.valueOf(paymentId))));
    schedule(bundle);
  }

  private void schedule(Bundle bundle) {
    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
    ContentResolver.requestSync(getAccount(), configuration.getSyncAdapterAuthority(), bundle);
  }

  @NonNull private Account getAccount() {
    Account[] accounts = accountManager.getAccountsByType(configuration.getAccountType());
    if (accounts != null && accounts.length > 0) {
      return accounts[0];
    }
    throw new IllegalStateException("User not logged in. Can't sync.");
  }
}
