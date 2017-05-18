/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.payment.repository.sync;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.SyncStatusObserver;
import android.os.Bundle;
import cm.aptoide.pt.v8engine.account.AndroidAccountProvider;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.sync.AptoideSyncAdapter;
import java.util.Collections;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

/**
 * Created by marcelobenites on 22/11/16.
 */

public class PaymentSyncScheduler {

  private final AndroidAccountProvider androidAccountProvider;
  private final PaymentSyncDataConverter syncDataConverter;
  private String authority;

  public PaymentSyncScheduler(PaymentSyncDataConverter converter,
      AndroidAccountProvider androidAccountProvider, String authority) {
    this.androidAccountProvider = androidAccountProvider;
    this.syncDataConverter = converter;
    this.authority = authority;
  }

  public Completable syncAuthorizations(List<String> paymentIds) {
    final Bundle bundle = new Bundle();
    bundle.putBoolean(AptoideSyncAdapter.EXTRA_PAYMENT_AUTHORIZATIONS, true);
    bundle.putString(AptoideSyncAdapter.EXTRA_PAYMENT_IDS, syncDataConverter.toString(paymentIds));
    return sync(bundle);
  }

  private Completable sync(Bundle bundle) {
    return androidAccountProvider.getAndroidAccount()
        .flatMapCompletable(account -> {
          bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
          bundle.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF, true);

          ContentResolver.setSyncAutomatically(account, authority, true);
          return sync(account, authority, bundle);
        });
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
    })
        .toCompletable();
  }

  public Completable syncConfirmation(Product product) {
    final Bundle bundle = syncDataConverter.toBundle(product);
    bundle.putBoolean(AptoideSyncAdapter.EXTRA_PAYMENT_CONFIRMATIONS, true);
    return sync(bundle);
  }

  public Completable syncConfirmation(Product product, int paymentId,
      String paymentConfirmationId) {
    final Bundle bundle = syncDataConverter.toBundle(product);
    bundle.putBoolean(AptoideSyncAdapter.EXTRA_PAYMENT_CONFIRMATIONS, true);
    bundle.putString(AptoideSyncAdapter.EXTRA_PAYMENT_CONFIRMATION_ID, paymentConfirmationId);
    bundle.putString(AptoideSyncAdapter.EXTRA_PAYMENT_IDS,
        syncDataConverter.toString(Collections.singletonList(String.valueOf(paymentId))));
    return sync(bundle);
  }
}
