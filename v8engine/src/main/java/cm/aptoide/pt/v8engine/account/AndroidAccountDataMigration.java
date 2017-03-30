package cm.aptoide.pt.v8engine.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.deprecated.SQLiteDatabaseHelper;
import rx.Completable;

/**
 * This can be deleted in future releases, when version 8.1.2.1 is no longer supported /
 * relevant
 */
public class AndroidAccountDataMigration {

  private static final String TAG = AndroidAccountDataMigration.class.getName();

  private static final String[] MIGRATION_KEYS = {
      "userId", "username", "useravatar", "refresh_token", "access_token",
      "aptoide_account_manager_login_mode", "userRepo", "access"
  };
  private static final Object MIGRATION_LOCK = new Object();

  private final SharedPreferences secureSharedPreferences;

  private final SharedPreferences defaultSharedPreferences;
  private final AccountManager accountManager;
  private final SecureCoderDecoder secureCoderDecoder;
  private final int currentVersion;
  private String databasePath;

  private int oldVersion;

  public AndroidAccountDataMigration(SharedPreferences secureSharedPreferences,
      SharedPreferences defaultSharedPreferences, AccountManager accountManager,
      SecureCoderDecoder secureCoderDecoder, int currentVersion, String databasePath) {
    this.secureSharedPreferences = secureSharedPreferences;
    this.defaultSharedPreferences = defaultSharedPreferences;
    this.accountManager = accountManager;
    this.secureCoderDecoder = secureCoderDecoder;
    this.currentVersion = currentVersion;
    this.databasePath = databasePath;
  }

  Completable migrate() {
    return Completable.defer(() -> {
      //
      // this code avoids the lock in case we already migrated the account
      //
      if (isMigrated()) {
        return Completable.complete();
      }
      synchronized (MIGRATION_LOCK) {
        if (isMigrated()) {
          return Completable.complete();
        }

        generateOldVersion();

        Log.i(TAG, String.format("Migrating from version %d to %d", oldVersion, currentVersion));

        if (oldVersion < 43) {
          return migrateAccountFromV7();
        }

        return migrateAccountFromV8();
      }
    });
  }

  private boolean isMigrated() {
    return oldVersion == currentVersion;
  }

  private Completable migrateAccountFromV7() {
    return Completable.defer(() -> Completable.fromCallable(() -> {
      //
      // migration from v7 to this v8
      //

      // here we will migrate from V7 directly to the new Account Manager and Account
      // all data is saved in shared prefs except the refresh token, which was saved in the
      // secure shared prefs

      Log.i(TAG, "migrating from v7");

      final android.accounts.Account[] accounts =
          accountManager.getAccountsByType(V8Engine.getConfiguration().getAccountType());
      final Account oldAccount = accounts[0];

      String sharedPrefsData;
      for (String key : MIGRATION_KEYS) {
        sharedPrefsData = defaultSharedPreferences.getString(key, null);
        if (!TextUtils.isEmpty(sharedPrefsData)) {
          accountManager.setUserData(oldAccount, key, sharedPrefsData);
        }
      }

      for (String key : MIGRATION_KEYS) {
        sharedPrefsData = secureSharedPreferences.getString(key, null);
        if (!TextUtils.isEmpty(sharedPrefsData)) {
          accountManager.setUserData(oldAccount, key, sharedPrefsData);
        }
      }

      String refreshToken = accountManager.blockingGetAuthToken(oldAccount, "Full access", false);
      if (!TextUtils.isEmpty(refreshToken)) {
        accountManager.setUserData(oldAccount, "refresh_token", refreshToken);
      }

      String matureSwitchKey = "aptoide_account_manager_mature_switch";
      sharedPrefsData = defaultSharedPreferences.getString(matureSwitchKey, "false");
      accountManager.setUserData(oldAccount, matureSwitchKey, sharedPrefsData);

      String accountManagerLoginModeKeyOld = "loginType";
      String accountManagerLoginModeKeyNew = "aptoide_account_manager_login_mode";
      sharedPrefsData = defaultSharedPreferences.getString(accountManagerLoginModeKeyOld, "");
      accountManager.setUserData(oldAccount, accountManagerLoginModeKeyNew, sharedPrefsData);

      cleanKeysFromPreferences(MIGRATION_KEYS, defaultSharedPreferences);
      cleanKeysFromPreferences(MIGRATION_KEYS, secureSharedPreferences);

      markMigrated();
      return Completable.complete();
    }));
  }

  private Completable migrateAccountFromV8() {
    return Completable.defer(() -> Completable.fromCallable(() -> {

      //
      // migration from an older v8.x to this v8
      //

      final Account[] accounts =
          accountManager.getAccountsByType(V8Engine.getConfiguration().getAccountType());

      if (!accountHasKeysForMigration(MIGRATION_KEYS, secureSharedPreferences)
          || accounts.length == 0) {

        Log.e(TAG,
            "Account migration from <8.1.2.1 to >8.2.0.0 failed. the required keys were not available.");
        markMigrated();
        return Completable.complete();
      }

      final Account oldAccount = accounts[0];

      String encryptedPassword = accountManager.getPassword(oldAccount);
      // new SecureCoderDecoder.Builder(context).create()
      String plainTextPassword = secureCoderDecoder.decrypt(encryptedPassword);

      if (oldVersion <= 55 || TextUtils.isEmpty(plainTextPassword)) {
        // previously we store the password encrypted but we stopped doing it at DB version 55
        plainTextPassword = encryptedPassword;
      }

      /*

      androidAccountManager.setUserData(androidAccount, USER_ID, account.getId());
      androidAccountManager.setUserData(androidAccount, USER_NICK_NAME, account.getNickname());
      androidAccountManager.setUserData(androidAccount, USER_AVATAR, account.getAvatar());
      androidAccountManager.setUserData(androidAccount, REFRESH_TOKEN, account.getRefreshToken());
      androidAccountManager.setUserData(androidAccount, ACCESS_TOKEN, account.getToken());
      androidAccountManager.setUserData(androidAccount, LOGIN_MODE, account.getType().name());
      androidAccountManager.setUserData(androidAccount, USER_REPO, account.getStore());
      androidAccountManager.setUserData(androidAccount, REPO_AVATAR, account.getStoreAvatar());
      androidAccountManager.setUserData(androidAccount, ACCESS, account.getAccess().name());

      androidAccountManager.setUserData(androidAccount, MATURE_SWITCH,
          String.valueOf(account.isAdultContentEnabled()));
      androidAccountManager.setUserData(androidAccount, ACCESS_CONFIRMED,
          String.valueOf(account.isAccessConfirmed()));

      */

      String sharedPrefsData;
      for (String key : MIGRATION_KEYS) {
        sharedPrefsData = secureSharedPreferences.getString(key, null);
        accountManager.setUserData(oldAccount, key, sharedPrefsData);
      }

      String matureSwitchKey = "aptoide_account_manager_mature_switch";
      sharedPrefsData = secureSharedPreferences.getString(matureSwitchKey, "false");
      accountManager.setUserData(oldAccount, matureSwitchKey, sharedPrefsData);

      // access_confirmed is registered in the default shared preferences and not in the
      // secure shared preferences
      String accessConfirmedKey = "access_confirmed";
      sharedPrefsData =
          Boolean.toString(defaultSharedPreferences.getBoolean(accessConfirmedKey, false));
      accountManager.setUserData(oldAccount, accessConfirmedKey, sharedPrefsData);

      // account.name -> user email. we don't need to change this
      accountManager.setPassword(oldAccount, plainTextPassword);

      // remove all keys from shared preferences
      cleanKeysFromPreferences(MIGRATION_KEYS, defaultSharedPreferences);
      cleanKeysFromPreferences(MIGRATION_KEYS, secureSharedPreferences);

      Log.i(TAG, "Account migration from <8.1.2.1 to >8.2.0.0 succeeded");
      markMigrated();
      return Completable.complete();
    }));
  }

  private void cleanKeysFromPreferences(String[] migrationKeys,
      SharedPreferences sharedPreferences) {
    for (int i = 0; i < migrationKeys.length; ++i) {
      if (sharedPreferences.contains(migrationKeys[i])) {
        sharedPreferences.edit().remove(migrationKeys[i]).commit();
      }
    }
  }

  private void markMigrated() {
    oldVersion = currentVersion;
  }

  private boolean accountHasKeysForMigration(String[] migrationKeys,
      SharedPreferences sharedPreferences) {
    for (int i = 0; i < migrationKeys.length; ++i) {
      if (sharedPreferences.contains(migrationKeys[i])) {
        return true;
      }
    }
    return false;
  }

  private void generateOldVersion() {
    int oldVersion = SQLiteDatabaseHelper.DATABASE_VERSION;
    try {
      SQLiteDatabase db =
          SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READONLY, null);
      oldVersion = db.getVersion();
      if (db.isOpen()) {
        db.close();
      }
    } catch (Exception ex) {
      // db does not exist. it's a fresh install
    }
    ;
    this.oldVersion = oldVersion;
  }
}
