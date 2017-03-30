package cm.aptoide.accountmanager;

import cm.aptoide.pt.annotation.Partners;
import java.util.List;
import rx.Completable;

/**
 * User account information such as subscribed stores, credentials, preferences.
 */
public interface Account {

  /**
   * Changes state of the account to logged out. This method should not be called directly use
   * {@link AptoideAccountManager#logout()} instead.
   *
   * @return Completable to perform logout.
   *
   * @see AptoideAccountManager#login(Type, String, String, String)
   */
  Completable logout();

  /**
   * Refreshes the account token. This method should not be called directly use
   * {@link AptoideAccountManager#refreshToken()} instead.
   *
   * @return Completable to perform logout.
   */
  Completable refreshToken();

  /**
   * Returns the stores which this account has subscribed to.
   *
   * @see Store
   */
  List<Store> getSubscribedStores();

  /**
   * Returns the id of the account.
   */
  String getId();

  /**
   * Returns the user's nickname.
   */
  String getNickname();

  /**
   * Returns the user's avatar URL.
   */
  String getAvatar();

  /**
   * Returns the user's store name.
   */
  String getStoreName();

  /**
   * Returns the user's store avatar URL.
   */
  String getStoreAvatar();

  /**
   * Returns whether adult content should be displayed for account.
   */
  boolean isAdultContentEnabled();

  /**
   * Returns account information access level e.g. whether user's nickname and avatar are
   * going to be visible in social timeline.
   *
   * @see Access
   */
  Access getAccess();

  /**
   * Returns whether user confirmed its access level or not.
   *
   * @see #getAccess()
   */
  boolean isAccessConfirmed();

  /**
   * Returns whether user is logged in or not.
   */
  boolean isLoggedIn();

  /**
   * Returns user's e-mail.
   */
  String getEmail();

  /**
   * Returns access token for server side interaction. The token may expire according to server
   * rules.
   *
   * @see #refreshToken()
   */
  String getAccessToken();

  /**
   * Returns refresh token used to refresh access token when it expires.
   *
   * @see #refreshToken()
   */
  String getRefreshToken();

  /**
   * Returns account's password.
   */
  String getPassword();

  /**
   * Returns account's type.
   *
   * @see Type
   */
  Account.Type getType();

  /**
   * Account information access level.
   */
  enum Access {
    /**
     * Account information is going to be visible e.g. user's nickname and avatar are
     * going to be visible in social timeline.
     */
    PUBLIC, /**
     * Account information is going to be hidden e.g. user's nickname and avatar are
     * not going to be visible in social timeline.
     */
    PRIVATE, /**
     * User did not confirm the account access level yet. By all means account is considered
     * {@link #PRIVATE}.
     */
    UNLISTED
  }

  /**
   * Account type.
   */
  public enum Type {
    /**
     * Default account when user did not login yet.
     */
    LOCAL, /**
     * Account created when user has logged in using Aptoide services.
     */
    APTOIDE, /**
     * Account created when user has logged in using Google services.
     */
    GOOGLE, /**
     * Account created when user has logged in using Facebook services.
     */
    FACEBOOK, /**
     * Account created when user has logged in using ABAN services.
     */
    @Partners ABAN
  }
}
