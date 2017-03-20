package cm.aptoide.accountmanager;

import java.util.List;
import rx.Completable;

public interface Account {

  Completable logout();

  Completable refreshToken();

  List<Store> getSubscribedStores();

  String getId();

  String getNickname();

  String getAvatar();

  String getStore();

  String getStoreAvatar();

  boolean isAdultContentEnabled();

  Access getAccess();

  boolean isAccessConfirmed();

  boolean isLoggedIn();

  String getEmail();

  String getToken();

  String getRefreshToken();

  String getPassword();

  Account.Type getType();

  enum Access {
    PUBLIC, PRIVATE, UNLISTED
  }

  public enum Type {
    LOCAL, APTOIDE, GOOGLE, FACEBOOK, ABAN
  }
}
