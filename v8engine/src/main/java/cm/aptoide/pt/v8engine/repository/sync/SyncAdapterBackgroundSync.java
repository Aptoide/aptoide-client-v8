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
import cm.aptoide.pt.v8engine.payment.product.AptoideProduct;

/**
 * Created by marcelobenites on 22/11/16.
 */

public class SyncAdapterBackgroundSync {

  private final AptoidePreferencesConfiguration configuration;
  private final AccountManager accountManager;
  private final ProductBundleConverter productConverter;

  public SyncAdapterBackgroundSync(AptoidePreferencesConfiguration configuration,
      AccountManager accountManager, ProductBundleConverter productConverter) {
    this.configuration = configuration;
    this.accountManager = accountManager;
    this.productConverter = productConverter;
  }

  public void syncAuthorization(int paymentId) {
    final Bundle bundle = new Bundle();
    bundle.putInt(AptoideSyncAdapter.EXTRA_PAYMENT_ID, paymentId);
    schedule(bundle);
  }

  public void syncConfirmation(AptoideProduct product) {
    schedule(productConverter.toBundle(product));
  }

  public void syncConfirmation(AptoideProduct product, int paymentId, String paymentConfirmationId) {
    final Bundle bundle = productConverter.toBundle(product);
    bundle.putString(AptoideSyncAdapter.EXTRA_PAYMENT_CONFIRMATION_ID, paymentConfirmationId);
    bundle.putInt(AptoideSyncAdapter.EXTRA_PAYMENT_ID, paymentId);
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
