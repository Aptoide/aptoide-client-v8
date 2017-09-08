package cm.aptoide.pt.networking;

import android.accounts.AccountManager;
import cm.aptoide.pt.account.AndroidAccountProvider;
import rx.Completable;
import rx.Single;

public class AuthenticationPersistence {

  public static final String ACCOUNT_ACCESS_TOKEN = "access_token";
  public static final String ACCOUNT_REFRESH_TOKEN = "refresh_token";
  public static final String ACCOUNT_TYPE = "aptoide_account_manager_login_mode";

  private final AndroidAccountProvider androidAccountProvider;
  private final AccountManager androidAccountManager;

  public AuthenticationPersistence(AndroidAccountProvider androidAccountProvider,
      AccountManager androidAccountManager) {
    this.androidAccountProvider = androidAccountProvider;
    this.androidAccountManager = androidAccountManager;
  }

  public Completable removeAuthentication() {
    return androidAccountProvider.removeAndroidAccount();
  }

  public Single<Authentication> getAuthentication() {
    return androidAccountProvider.getAndroidAccount()
        .map(androidAccount -> {
          return new Authentication(androidAccount.name,
              androidAccountManager.getUserData(androidAccount, ACCOUNT_REFRESH_TOKEN),
              androidAccountManager.getUserData(androidAccount, ACCOUNT_ACCESS_TOKEN),
              androidAccountManager.getPassword(androidAccount),
              androidAccountManager.getUserData(androidAccount, ACCOUNT_TYPE));
        })
        .onErrorReturn(throwable -> new Authentication("", "", "", "", ""));
  }

  public Completable updateAuthentication(String accessToken) {
    return androidAccountProvider.getAndroidAccount()
        .doOnSuccess(androidAccount -> {
          androidAccountManager.setUserData(androidAccount, ACCOUNT_ACCESS_TOKEN, accessToken);
        })
        .toCompletable();
  }

  public Completable createAuthentication(String email, String password, String refreshToken,
      String accessToken, String type) {
    return androidAccountProvider.createAndroidAccount(email)
        .doOnSuccess(androidAccount -> {
          androidAccountManager.setUserData(androidAccount, ACCOUNT_REFRESH_TOKEN, refreshToken);
          androidAccountManager.setUserData(androidAccount, ACCOUNT_ACCESS_TOKEN, accessToken);
          androidAccountManager.setPassword(androidAccount, password);
          androidAccountManager.setUserData(androidAccount, ACCOUNT_TYPE, type);
        })
        .toCompletable();
  }
}
