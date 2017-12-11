package cm.aptoide.pt.account;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AccountPersistence;
import cm.aptoide.pt.preferences.AdultContent;
import rx.Completable;
import rx.Single;

public class MatureContentPersistence implements AccountPersistence {
  private final AccountPersistence wrappedAccountPersistence;
  private final AdultContent adultContent;

  public MatureContentPersistence(AccountPersistence persistence, AdultContent adultContent) {
    this.wrappedAccountPersistence = persistence;
    this.adultContent = adultContent;
  }

  @Override public Completable saveAccount(Account account) {
    final Completable saveAdultSwitch =
        account.isAdultContentEnabled() ? adultContent.enable() : adultContent.disable();
    return wrappedAccountPersistence.saveAccount(account)
        .andThen(saveAdultSwitch)
        .onErrorResumeNext(err -> saveAdultSwitch);
  }

  @Override public Single<Account> getAccount() {
    return wrappedAccountPersistence.getAccount()
        .map(account -> new MatureContentAccount(account, adultContent));
  }

  @Override public Completable removeAccount() {
    return wrappedAccountPersistence.removeAccount()
        .andThen(adultContent.disable());
  }
}
