package cm.aptoide.accountmanager;

import java.util.List;

/**
 * User account information such as subscribed stores, credentials, preferences.
 */
public interface Account {

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
   * Returns the Store of the user.
   * Attention: If the user does not have a store, store is not null.
   *
   * @return Store of the user.
   */
  Store getStore();

  /**
   * Returns true if the user has a store
   *
   * @return True, if the user has a store
   */
  boolean hasStore();

  /**
   * True if the user is public.
   *
   * On the android team: public means access = public
   * On the android team: private means access = private or unlisted
   *
   * @return True if the user is public. False if user is private.
   */
  boolean isPublicUser();

  /**
   * True if the store is public.
   *
   * @return True if the store is public. False if store is private.
   */
  boolean isPublicStore();

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
}