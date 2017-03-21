package cm.aptoide.pt.v8engine.deprecated;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.deprecated.tables.Downloads;
import cm.aptoide.pt.v8engine.deprecated.tables.Excluded;
import cm.aptoide.pt.v8engine.deprecated.tables.Repo;
import cm.aptoide.pt.v8engine.deprecated.tables.Rollback;
import cm.aptoide.pt.v8engine.deprecated.tables.Scheduled;

public class SQLiteDatabaseHelper extends SQLiteOpenHelper {

  private static final String TAG = SQLiteDatabaseHelper.class.getSimpleName();
  private static final int DATABASE_VERSION = 59;
  private Throwable aggregateExceptions;

  public SQLiteDatabaseHelper(Context context) {
    super(context, "aptoide.db", null, DATABASE_VERSION);
  }

  @Override public void onCreate(SQLiteDatabase db) {
    Logger.w(TAG, "onCreate() called");

    // do nothing here.
    ManagerPreferences.setNeedsSqliteDbMigration(false);
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Logger.w(TAG, "onUpgrade() called with: "
        + "oldVersion = ["
        + oldVersion
        + "], newVersion = ["
        + newVersion
        + "]");

    migrate(db);
    checkAndMigrateUserAccount();
    ManagerPreferences.setNeedsSqliteDbMigration(false);
    SecurePreferences.setWizardAvailable(true);
  }

  @Override public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    super.onDowngrade(db, oldVersion, newVersion);
    Logger.w(TAG, "onDowngrade() called with: "
        + "oldVersion = ["
        + oldVersion
        + "], newVersion = ["
        + newVersion
        + "]");
    migrate(db);

    ManagerPreferences.setNeedsSqliteDbMigration(false);
  }

  /**
   * migrate from whole SQLite db from V7 to V8 Realm db
   */
  private void migrate(SQLiteDatabase db) {
    if (!ManagerPreferences.needsSqliteDbMigration()) {
      return;
    }
    Logger.w(TAG, "Migrating database started....");

    try {
      new Repo().migrate(db, AccessorFactory.getAccessorFor(Store.class));
    } catch (Exception ex) {
      logException(ex);
    }

    try {
      new Excluded().migrate(db, AccessorFactory.getAccessorFor(Update.class));
    } catch (Exception ex) {
      logException(ex);
    }

    // recreated upon app install
    //try {
    //  new Installed().migrate(db,
    //      AccessorFactory.getAccessorFor(cm.aptoide.pt.database.realm.Installed.class)); // X
    //} catch (Exception ex) {
    //  logException(ex);
    //}

    try {
      new Rollback().migrate(db,
          AccessorFactory.getAccessorFor(cm.aptoide.pt.database.realm.Rollback.class));
    } catch (Exception ex) {
      logException(ex);
    }

    try {
      new Scheduled().migrate(db,
          AccessorFactory.getAccessorFor(cm.aptoide.pt.database.realm.Scheduled.class)); // X
    } catch (Exception ex) {
      logException(ex);
    }

    // Updates table has changed. The new one has column label the old one doesn't.
    // The updates are going to be obtained from ws
    //try {
    //  new Updates().migrate(db, realm);
    //  // despite the migration, this data should be recreated upon app startup
    //} catch (Exception ex) {
    //  logException(ex);
    //}

    try {
      new Downloads().migrate(AccessorFactory.getAccessorFor(Download.class));
    } catch (Exception ex) {
      logException(ex);
    }

    // table "AmazonABTesting" was deliberedly left out due to its irrelevance in the DB upgrade
    // table "ExcludedAd" was deliberedly left out due to its irrelevance in the DB upgrade

    if (aggregateExceptions != null) {
      CrashReport.getInstance().log(aggregateExceptions);
    }
    Logger.w(TAG, "Migrating database finished.");
  }

  /**
   * This method can be deleted in future releases, when version 8.1.2.1 is no longer supported /
   * relevant
   */
  private void checkAndMigrateUserAccount() {
    final Context appContext = V8Engine.getContext();
    final AccountManager androidAccountManager = AccountManager.get(appContext);
    final android.accounts.Account[] accounts =
        androidAccountManager.getAccountsByType(V8Engine.getConfiguration().getAccountType());

    String[] migrationKeys = {
        "userId", "username", "useravatar", "refresh_token", "access_token",
        "aptoide_account_manager_login_mode", "userRepo", "useravatar", "access", "access"
    };

    try {
      SharedPreferences sharedPreferences = SecurePreferencesImplementation.getInstance(appContext);

      if (!accountNeedsMigration(migrationKeys, sharedPreferences)) {
        return;
      }

      Account androidAccount = accounts[0];
      String encryptedPassword = androidAccountManager.getPassword(androidAccount);
      String plainTextPassword =
          new SecureCoderDecoder.Builder(appContext).create().decrypt(encryptedPassword);


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
      for (String key : migrationKeys) {
        sharedPrefsData = sharedPreferences.getString(key, null);
        androidAccountManager.setUserData(androidAccount, key, sharedPrefsData);
      }

      String matureSwitchKey = "aptoide_account_manager_mature_switch";
      sharedPrefsData = String.valueOf(sharedPreferences.getString(matureSwitchKey, "false"));
      androidAccountManager.setUserData(androidAccount, matureSwitchKey, sharedPrefsData);

      // access_confirmed is registered in the default shared preferences and not in the
      // secure shared preferences
      String accessConfirmedKey = "access_confirmed";
      sharedPrefsData = PreferenceManager.getDefaultSharedPreferences(appContext).getString(accessConfirmedKey,
          "false");
      androidAccountManager.setUserData(androidAccount, accessConfirmedKey, sharedPrefsData);

      // account.name -> user email. we don't need to change this
      androidAccountManager.setPassword(androidAccount, plainTextPassword);

      // remove all keys from shared preferences
      cleanKeysFromPreferences(migrationKeys, sharedPreferences);

      Log.w(TAG, "Account migration from <8.1.2.1 to >8.2.0.0 succeeded");
    } catch (Exception e) {
      Log.e(TAG, "Account migration from <8.1.2.1 to >8.2.0.0 failed", e);
    }
  }

  private void cleanKeysFromPreferences(String[] migrationKeys,
      SharedPreferences sharedPreferences) {
    for (int i = 0; i < migrationKeys.length; ++i) {
      if (sharedPreferences.contains(migrationKeys[i])) {
        sharedPreferences.edit().remove(migrationKeys[i]).commit();
      }
    }
  }

  private boolean accountNeedsMigration(String[] migrationKeys,
      SharedPreferences sharedPreferences) {
    for(int i = 0 ; i < migrationKeys.length ; ++i){
      if(sharedPreferences.contains(migrationKeys[i])){
        return true;
      }
    }
    return false;
  }

  private void logException(Exception ex) {
    CrashReport.getInstance().log(ex);

    if (aggregateExceptions == null) {
      aggregateExceptions = ex;
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      aggregateExceptions.addSuppressed(ex);
    }
  }
}
