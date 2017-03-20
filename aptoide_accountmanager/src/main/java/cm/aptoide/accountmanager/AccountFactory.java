package cm.aptoide.accountmanager;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import java.util.List;

public class AccountFactory {

  private final AptoideClientUUID aptoideClientUUID;
  private final ExternalAccountFactory externalAccountFactory;

  public AccountFactory(AptoideClientUUID aptoideClientUUID,
      ExternalAccountFactory externalAccountFactory) {
    this.aptoideClientUUID = aptoideClientUUID;
    this.externalAccountFactory = externalAccountFactory;
  }

  public Account createAccount(String access, List<Store> stores, String id, String name,
      String nickname, String avatar, String refreshToken, String token, String password,
      Account.Type type, String store, String storeAvatar, boolean adultContentEnabled,
      boolean accessConfirmed) {
    final Account aptoideAccount =
        new AptoideAccount(id, name, nickname, avatar, refreshToken, token, password, type, store,
            storeAvatar, adultContentEnabled, getAccessFrom(access), accessConfirmed, stores,
            aptoideClientUUID);
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