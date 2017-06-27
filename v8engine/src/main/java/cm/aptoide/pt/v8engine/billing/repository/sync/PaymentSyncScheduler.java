/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.billing.repository.sync;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.SyncStatusObserver;
import android.os.Bundle;
import cm.aptoide.pt.v8engine.account.AndroidAccountProvider;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.sync.AptoideSyncAdapter;
import rx.Completable;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

public class PaymentSyncScheduler {

  private final AndroidAccountProvider androidAccountProvider;
  private final ProductBundleMapper syncDataConverter;
  private String authority;

  public PaymentSyncScheduler(ProductBundleMapper converter,
      AndroidAccountProvider androidAccountProvider, String authority) {
    this.androidAccountProvider = androidAccountProvider;
    this.syncDataConverter = converter;
    this.authority = authority;
    ContentResolver.setMasterSyncAutomatically(true);
  }

  public Completable scheduleAuthorizationSync(int paymentId) {
    final Bundle bundle = new Bundle();
    bundle.putBoolean(AptoideSyncAdapter.EXTRA_PAYMENT_AUTHORIZATIONS, true);
    bundle.putInt(AptoideSyncAdapter.EXTRA_PAYMENT_ID, paymentId);
    return scheduleOneOffSync(bundle);
  }

  public Completable scheduleTransactionSync(Product product) {
    final Bundle bundle = syncDataConverter.mapToBundle(product);
    bundle.putBoolean(AptoideSyncAdapter.EXTRA_PAYMENT_TRANSACTIONS, true);
    return scheduleOneOffSync(bundle);
  }

  private Completable scheduleOneOffSync(Bundle bundle) {
    return androidAccountProvider.getAndroidAccount()
        .flatMapCompletable(account -> {
          bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
          bundle.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF, true);

          ContentResolver.setSyncAutomatically(account, authority, true);
          return scheduleSync(account, authority, bundle);
        });
  }

  private Completable scheduleSync(Account account, String authority, Bundle bundle) {
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
    })
        .toCompletable();
  }
}
