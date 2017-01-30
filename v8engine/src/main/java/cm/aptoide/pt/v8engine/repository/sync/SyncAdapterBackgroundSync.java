/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.repository.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.SyncStatusObserver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;
import cm.aptoide.pt.v8engine.payment.products.AptoideProduct;
import java.util.Collections;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

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

  public Completable syncAuthorizations(List<String> paymentIds) {
    final Bundle bundle = new Bundle();
    bundle.putBoolean(AptoideSyncAdapter.EXTRA_PAYMENT_AUTHORIZATIONS, true);
    bundle.putString(AptoideSyncAdapter.EXTRA_PAYMENT_IDS, syncDataConverter.toString(paymentIds));
    return sync(bundle);
  }

  public Completable syncConfirmation(AptoideProduct product) {
    final Bundle bundle = syncDataConverter.toBundle(product);
    bundle.putBoolean(AptoideSyncAdapter.EXTRA_PAYMENT_CONFIRMATIONS, true);
    return sync(bundle);
  }

  public Completable syncConfirmation(AptoideProduct product, int paymentId,
      String paymentConfirmationId) {
    final Bundle bundle = syncDataConverter.toBundle(product);
    bundle.putBoolean(AptoideSyncAdapter.EXTRA_PAYMENT_CONFIRMATIONS, true);
    bundle.putString(AptoideSyncAdapter.EXTRA_PAYMENT_CONFIRMATION_ID, paymentConfirmationId);
    bundle.putString(AptoideSyncAdapter.EXTRA_PAYMENT_IDS,
        syncDataConverter.toString(Collections.singletonList(String.valueOf(paymentId))));
    return sync(bundle);
  }

  private Completable sync(Bundle bundle) {
    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF, true);

    return sync(getAccount(), configuration.getContentAuthority(), bundle);
  }

  private Completable sync(Account account, String authority, Bundle bundle) {
    return Observable.create(new Observable.OnSubscribe<Integer>() {
      @Override public void call(Subscriber<? super Integer> subscriber) {
        final Object handle =
            ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE,
                new SyncStatusObserver() {
                  @Override public void onStatusChanged(int which) {
                    if (!subscriber.isUnsubscribed()
                        && which == ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE
                        && !ContentResolver.isSyncActive(account, authority)) {
                      subscriber.onCompleted();
                    }
                  }
                });
        subscriber.add(
            Subscriptions.create(() -> ContentResolver.removeStatusChangeListener(handle)));
        ContentResolver.requestSync(account, authority, bundle);
      }
    }).toCompletable();
  }

  @NonNull private Account getAccount() {
    Account[] accounts = accountManager.getAccountsByType(configuration.getAccountType());
    if (accounts != null && accounts.length > 0) {
      return accounts[0];
    }
    throw new IllegalStateException("User not logged in. Can't sync.");
  }
}
