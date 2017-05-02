package cm.aptoide.pt.v8engine.pull;

import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;
import cm.aptoide.pt.v8engine.account.AndroidAccountProvider;
import cm.aptoide.pt.v8engine.sync.AptoideSyncAdapter;
import rx.Completable;

/**
 * Created by trinkes on 02/05/2017.
 */

public class NotificationSyncScheduler {
  private AndroidAccountProvider androidAccountProvider;
  private String authority;

  public NotificationSyncScheduler(AndroidAccountProvider androidAccountProvider,
      String authority) {
    this.androidAccountProvider = androidAccountProvider;
    this.authority = authority;
  }

  public Completable startSync() {
    return androidAccountProvider.getAndroidAccount()
        .onErrorReturn(throwable -> null)
        .flatMapCompletable(account -> sync(account, authority, createBundle()));
  }

  private Bundle createBundle() {
    Bundle bundle = new Bundle();
    bundle.putBoolean(AptoideSyncAdapter.EXTRA_CAMPAIGN_NOTIFICATION, true);
    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
    return bundle;
  }

  private Completable sync(Account account, String authority, Bundle bundle) {
    return Completable.fromAction(() -> {
      ContentResolver.setSyncAutomatically(account, authority, true);
      ContentResolver.requestSync(account, authority, bundle);
    });
  }
}
