package cm.aptoide.pt.v8engine.deprecated;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.database.AccessorFactory;
import cm.aptoide.pt.v8engine.deprecated.tables.Downloads;
import cm.aptoide.pt.v8engine.deprecated.tables.Excluded;
import cm.aptoide.pt.v8engine.deprecated.tables.Repo;
import cm.aptoide.pt.v8engine.deprecated.tables.Rollback;
import cm.aptoide.pt.v8engine.deprecated.tables.Scheduled;

public class SQLiteDatabaseHelper extends SQLiteOpenHelper {

  public static final int DATABASE_VERSION = 60;
  public static final String DATABASE_NAME = "aptoide.db";

  private static final String TAG = SQLiteDatabaseHelper.class.getSimpleName();
  private final Context context;

  private Throwable aggregateExceptions;
  private SharedPreferences sharedPreferences;
  private SharedPreferences securePreferences;
  private PackageManager packageManager;

  public SQLiteDatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    this.context = context;
    sharedPreferences = ((V8Engine) context.getApplicationContext()).getDefaultSharedPreferences();
    securePreferences = SecurePreferencesImplementation.getInstance(context.getApplicationContext(),
        sharedPreferences);

    Logger.w(TAG,
        "SQLiteDatabaseHelper() sharedPreferences is null: " + (sharedPreferences == null));
  }

  @Override public void onCreate(SQLiteDatabase db) {
    Logger.w(TAG, "onCreate() called");

    // do nothing here.
    packageManager = context.getPackageManager();
    ManagerPreferences.setNeedsSqliteDbMigration(false, sharedPreferences);
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Logger.w(TAG, "onUpgrade() called with: "
        + "oldVersion = ["
        + oldVersion
        + "], newVersion = ["
        + newVersion
        + "]");

    migrate(db);

    ManagerPreferences.setNeedsSqliteDbMigration(false, sharedPreferences);
    SecurePreferences.setWizardAvailable(true, securePreferences);
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

    ManagerPreferences.setNeedsSqliteDbMigration(false, sharedPreferences);
  }

  /**
   * migrate from whole SQLite db from V7 to V8 Realm db
   */
  private void migrate(SQLiteDatabase db) {
    if (!ManagerPreferences.needsSqliteDbMigration(sharedPreferences)) {
      return;
    }
    Logger.w(TAG, "Migrating database started....");

    try {
      new Repo().migrate(db, AccessorFactory.getAccessorFor(
          ((V8Engine) context.getApplicationContext()
              .getApplicationContext()).getDatabase(), Store.class), packageManager,
          context.getApplicationContext());
    } catch (Exception ex) {
      logException(ex);
    }

    try {
      new Excluded().migrate(db, AccessorFactory.getAccessorFor(
          ((V8Engine) context.getApplicationContext()
              .getApplicationContext()).getDatabase(), Update.class), packageManager,
          context.getApplicationContext());
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
      new Rollback().migrate(db, AccessorFactory.getAccessorFor(
          ((V8Engine) context.getApplicationContext()
              .getApplicationContext()).getDatabase(), cm.aptoide.pt.database.realm.Rollback.class),
          packageManager, context.getApplicationContext());
    } catch (Exception ex) {
      logException(ex);
    }

    try {
      new Scheduled().migrate(db, AccessorFactory.getAccessorFor(
          ((V8Engine) context.getApplicationContext()
              .getApplicationContext()).getDatabase(),
          cm.aptoide.pt.database.realm.Scheduled.class), packageManager,
          context.getApplicationContext()); // X
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
      new Downloads().migrate(AccessorFactory.getAccessorFor(
          ((V8Engine) context.getApplicationContext()
              .getApplicationContext()).getDatabase(), Download.class));
    } catch (Exception ex) {
      logException(ex);
    }

    // table "AmazonABTesting" was deliberedly left out due to its irrelevance in the DB upgrade
    // table "ExcludedAd" was deliberedly left out due to its irrelevance in the DB upgrade

    if (aggregateExceptions != null) {
      CrashReport.getInstance()
          .log(aggregateExceptions);
    }
    Logger.w(TAG, "Migrating database finished.");
  }

  private void logException(Exception ex) {
    CrashReport.getInstance()
        .log(ex);

    if (aggregateExceptions == null) {
      aggregateExceptions = ex;
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      aggregateExceptions.addSuppressed(ex);
    }
  }
}
