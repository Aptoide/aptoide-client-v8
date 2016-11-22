/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;
import cm.aptoide.pt.v8engine.payment.BackgroundSync;
import java.io.SyncFailedException;

/**
 * Created by marcelobenites on 22/11/16.
 */

public class SyncAdapterBackgroundSync implements BackgroundSync {

  private final AptoidePreferencesConfiguration configuration;
  private final AccountManager accountManager;

  public SyncAdapterBackgroundSync(AptoidePreferencesConfiguration configuration,
      AccountManager accountManager) {
    this.configuration = configuration;
    this.accountManager = accountManager;
  }

  @Override public void schedule() {
    final Bundle bundle = new Bundle();
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
