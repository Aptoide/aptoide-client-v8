package cm.aptoide.pt.v8engine.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Build;
import rx.Completable;
import rx.Scheduler;
import rx.Single;

public class AndroidAccountProvider {

  private final AccountManager androidAccountManager;
  private final String accountType;
  private final Scheduler scheduler;

  public AndroidAccountProvider(AccountManager androidAccountManager, String accountType,
      Scheduler scheduler) {
    this.androidAccountManager = androidAccountManager;
    this.accountType = accountType;
    this.scheduler = scheduler;
  }

  public Single<Account> getAndroidAccount() {
    return Single.defer(() -> {
      final Account[] accounts = androidAccountManager.getAccountsByType(accountType);

      if (accounts.length == 0) {
        return Single.error(new IllegalStateException("No account found."));
      }
      return Single.just(accounts[0]);
    })
        .observeOn(scheduler);
  }

  public Single<Account> createAndroidAccount(String email, String password) {
    final Account androidAccount = new Account(email, accountType);
    try {
      androidAccountManager.addAccountExplicitly(androidAccount, password, null);
    } catch (SecurityException e) {
      return Single.error(e);
    }
    return Single.just(androidAccount);
  }

  public Completable removeAndroidAccount() {
    return getAndroidAccount().doOnSuccess(androidAccount -> {
      if (Build.VERSION.SDK_INT >= 22) {
        androidAccountManager.removeAccountExplicitly(androidAccount);
      } else {
        androidAccountManager.removeAccount(androidAccount, null, null);
      }
    })
        .toCompletable();
  }
}