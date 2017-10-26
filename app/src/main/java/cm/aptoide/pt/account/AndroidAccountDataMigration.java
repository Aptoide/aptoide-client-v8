package cm.aptoide.pt.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import cm.aptoide.pt.deprecated.SQLiteDatabaseHelper;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import rx.Completable;
import rx.Scheduler;

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

  private static final String[] NEW_STORE_MIGRATION_KEYS = {
      "storeAvatar", "account_store_download_count", "account_store_id", "account_store_theme",
      "account_store_username", "account_store_password"
  };
  private static final Object MIGRATION_LOCK = new Object();

  private final SharedPreferences secureSharedPreferences;

  private final SharedPreferences defaultSharedPreferences;
  private final AccountManager accountManager;
  private final SecureCoderDecoder secureCoderDecoder;
  private final int currentVersion;
  private final String accountType;
  private final String databasePath;
  private final String applicationVersionName;
  private final Scheduler scheduler;

  private int oldVersion;

  public AndroidAccountDataMigration(SharedPreferences secureSharedPreferences,
      SharedPreferences defaultSharedPreferences, AccountManager accountManager,
      SecureCoderDecoder secureCoderDecoder, int currentVersion, String databasePath,
      String accountType, String applicationVersionName, Scheduler scheduler) {
    this.secureSharedPreferences = secureSharedPreferences;
    this.defaultSharedPreferences = defaultSharedPreferences;
    this.accountManager = accountManager;
    this.secureCoderDecoder = secureCoderDecoder;
    this.currentVersion = currentVersion;
    this.databasePath = databasePath;
    this.accountType = accountType;
    this.oldVersion = -1;
    this.applicationVersionName = applicationVersionName;
    this.scheduler = scheduler;
  }

  public Completable migrate() {
    return Completable.defer(() -> {
      //
      // this code avoids the lock in case we already migrated the account
      //
      if (isMigrated()) {
        return Completable.complete();
      }
      synchronized (MIGRATION_LOCK) {
        generateOldVersion();
        if (isMigrated()) {
          return Completable.complete();
        }

        Log.i(TAG, String.format("Migrating from version %d to %d", oldVersion, currentVersion));

        return migrateAccountFromPreviousThan43().andThen(migrateAccountFrom43to59())
            .andThen(migrateAccountFromVersion59To60())
            .andThen(cleanShareDialogShowPref())
            .doOnCompleted(() -> markMigrated());
      }
    })
        .subscribeOn(scheduler);
  }

  /**
   * This method is responsible for cleaning a preference that allowing
   * the share dialog on app install to show. This preference should be cleaned every time
   * we upgrade to a new major version (X.X.0.0)
   */
  private Completable cleanShareDialogShowPref() {
    String oldVersionName =
        ManagerPreferences.getPreviewDialogPrefVersionCleaned(defaultSharedPreferences);
    if (!oldVersionName.equals(applicationVersionName)) {

      if (getMajorIntFromVersionName(oldVersionName) < getMajorIntFromVersionName(
          applicationVersionName)) {

        return Completable.defer(() -> Completable.fromCallable(() -> {
          ManagerPreferences.setShowPreviewDialog(true, defaultSharedPreferences);
          ManagerPreferences.setPreviewDialogPrefVersionCleaned(applicationVersionName,
              defaultSharedPreferences);
          return Completable.complete();
        }));
      }
    }
    return Completable.complete();
  }

  private int getMajorIntFromVersionName(String versionName) {
    int res = 0;
    if (versionName != null) {
      String[] parts = versionName.split("\\.");
      if (parts != null && parts.length > 1) {
        try {
          res = Integer.parseInt(parts[1]);
        } catch (Exception e) {

        }
      }
    }
    return res;
  }

  private boolean isMigrated() {
    return oldVersion == currentVersion;
  }

  //V7
  private Completable migrateAccountFromPreviousThan43() {
    if (oldVersion < 43) {
      Log.w(TAG, "migrateAccountFromPreviousThan43");
      return Completable.defer(() -> Completable.fromCallable(() -> {
        //
        // migration from v7 to this v8
        //

        // here we will migrate from V7 directly to the new Account Manager and Account
        // all data is saved in shared prefs except the refresh token, which was saved in the
        // secure shared prefs

        Log.i(TAG, "migrating from v7");

        final android.accounts.Account[] accounts = accountManager.getAccountsByType(accountType);
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
        if (!sharedPrefsData.isEmpty()) {
          accountManager.setUserData(oldAccount, accountManagerLoginModeKeyNew, sharedPrefsData);
        }

        cleanKeysFromPreferences(MIGRATION_KEYS, defaultSharedPreferences);
        cleanKeysFromPreferences(MIGRATION_KEYS, secureSharedPreferences);

        return Completable.complete();
      }));
    }
    return Completable.complete();
  }

  //v8
  private Completable migrateAccountFrom43to59() {
    if (oldVersion >= 43 && oldVersion < 59) {
      Log.w(TAG, "migrateAccountFrom43to59");
      return Completable.defer(() -> Completable.fromCallable(() -> {

        //
        // migration from an older v8.x to this v8
        //

        final Account[] accounts = accountManager.getAccountsByType(accountType);

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

        String accountManagerLoginModeKeyOld = "loginType";
        String accountManagerLoginModeKeyNew = "aptoide_account_manager_login_mode";
        sharedPrefsData = defaultSharedPreferences.getString(accountManagerLoginModeKeyOld, "");
        if (!sharedPrefsData.isEmpty()) {
          accountManager.setUserData(oldAccount, accountManagerLoginModeKeyNew, sharedPrefsData);
        }

        // remove all keys from shared preferences
        cleanKeysFromPreferences(MIGRATION_KEYS, defaultSharedPreferences);
        cleanKeysFromPreferences(MIGRATION_KEYS, secureSharedPreferences);

        Log.i(TAG, "Account migration from <8.1.2.1 to >8.2.0.0 succeeded");
        return Completable.complete();
      }));
    }
    return Completable.complete();
  }

  private Completable migrateAccountFromVersion59To60() {
    if (oldVersion < 60) {
      Log.w(TAG, "migrateAccountFromVersion59To60");
      return Completable.defer(() -> Completable.fromCallable(() -> {
        final android.accounts.Account[] accounts = accountManager.getAccountsByType(accountType);
        final Account oldAccount = accounts[0];

        for (String key : NEW_STORE_MIGRATION_KEYS) {
          if (key.equals("account_store_download_count") || key.equals("account_store_id")) {
            accountManager.setUserData(oldAccount, key, "0");
          } else {
            accountManager.setUserData(oldAccount, key, "");
          }
        }
        return Completable.complete();
      }));
    }
    return Completable.complete();
  }

  private void cleanKeysFromPreferences(String[] migrationKeys,
      SharedPreferences sharedPreferences) {
    for (int i = 0; i < migrationKeys.length; ++i) {
      if (sharedPreferences.contains(migrationKeys[i])) {
        sharedPreferences.edit()
            .remove(migrationKeys[i])
            .commit();
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
    oldVersion = SQLiteDatabaseHelper.OLD_DATABASE_VERSION;
  }
}
