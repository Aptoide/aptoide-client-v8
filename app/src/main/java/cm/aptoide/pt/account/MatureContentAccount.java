package cm.aptoide.pt.account;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AdultContent;
import cm.aptoide.accountmanager.Store;
import java.util.List;

public class MatureContentAccount implements Account {

  private final Account wrappedAccount;
  private final AdultContent adultContent;

  public MatureContentAccount(Account wrappedAccount, AdultContent adultContent) {
    this.wrappedAccount = wrappedAccount;
    this.adultContent = adultContent;
  }

  @Override public List<Store> getSubscribedStores() {
    return wrappedAccount.getSubscribedStores();
  }

  @Override public String getId() {
    return wrappedAccount.getId();
  }

  @Override public String getNickname() {
    return wrappedAccount.getNickname();
  }

  @Override public String getAvatar() {
    return wrappedAccount.getAvatar();
  }

  @Override public boolean isAdultContentEnabled() {
    return adultContent.enabled()
        .first()
        .toSingle()
        .toBlocking()
        .value();
  }

  @Override public Access getAccess() {
    return wrappedAccount.getAccess();
  }

  @Override public boolean isAccessConfirmed() {
    return wrappedAccount.isAccessConfirmed();
  }

  @Override public boolean isLoggedIn() {
    return wrappedAccount.isLoggedIn();
  }

  @Override public String getEmail() {
    return wrappedAccount.getEmail();
  }

  @Override public Store getStore() {
    return wrappedAccount.getStore();
  }

  @Override public boolean hasStore() {
    return wrappedAccount.hasStore();
  }

  @Override public boolean isPublicUser() {
    return wrappedAccount.isPublicUser();
  }

  @Override public boolean isPublicStore() {
    return wrappedAccount.isPublicStore();
  }
}
