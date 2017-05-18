package cm.aptoide.accountmanager;

import android.text.TextUtils;
import java.util.List;

public class AccountFactory {

  private final ExternalAccountFactory externalAccountFactory;
  private final AccountService accountService;

  public AccountFactory(ExternalAccountFactory externalAccountFactory,
      AccountService accountService) {
    this.externalAccountFactory = externalAccountFactory;
    this.accountService = accountService;
  }

  public Account createAccount(String access, List<Store> stores, String id, String name,
      String nickname, String avatar, String refreshToken, String token, String password,
      Account.Type type, String store, String storeAvatar, boolean adultContentEnabled,
      boolean accessConfirmed) {
    final Account aptoideAccount =
        new AptoideAccount(id, name, nickname, avatar, refreshToken, token, password, type, store,
            storeAvatar, adultContentEnabled, getAccessFrom(access), accessConfirmed, stores,
            accountService);
    switch (type) {
      case APTOIDE:
        return aptoideAccount;
      case FACEBOOK:
        return externalAccountFactory.createFacebookAccount(aptoideAccount);
      case GOOGLE:
        return externalAccountFactory.createGoogleAccount(aptoideAccount);
      case ABAN:
        return externalAccountFactory.createABANAccount(aptoideAccount);
      default:
        throw new IllegalArgumentException("Illegal account type " + type);
    }
  }

  private Account.Access getAccessFrom(String serverAccess) {
    return TextUtils.isEmpty(serverAccess) ? Account.Access.UNLISTED
        : Account.Access.valueOf(serverAccess.toUpperCase());
  }
}