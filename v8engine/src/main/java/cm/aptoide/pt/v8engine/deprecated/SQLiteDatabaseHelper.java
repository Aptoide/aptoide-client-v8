/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.deprecated;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import com.crashlytics.android.Crashlytics;

import cm.aptoide.pt.database.accessors.DeprecatedDatabase;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.v8engine.deprecated.tables.Excluded;
import cm.aptoide.pt.v8engine.deprecated.tables.Installed;
import cm.aptoide.pt.v8engine.deprecated.tables.Repo;
import cm.aptoide.pt.v8engine.deprecated.tables.Rollback;
import cm.aptoide.pt.v8engine.deprecated.tables.Scheduled;
import cm.aptoide.pt.v8engine.deprecated.tables.Updates;
import io.realm.Realm;

/**
 * Created by sithengineer on 24/08/16.
 */
public class SQLiteDatabaseHelper extends SQLiteOpenHelper {

	private static final String TAG = SQLiteDatabaseHelper.class.getSimpleName();
	private static final int DATABASE_VERSION = 43;

	private Throwable agregateExceptions;

	public SQLiteDatabaseHelper(Context context) {
		super(context, "aptoide.db", null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Logger.w(TAG, "onCreate() called");
		// do nothing here.
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Logger.w(TAG, "onUpgrade() called with: " + "oldVersion = [" + oldVersion + "], newVersion = [" + newVersion + "]");
		migrate(db);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		super.onDowngrade(db, oldVersion, newVersion);
		Logger.w(TAG, "onDowngrade() called with: " + "oldVersion = [" + oldVersion + "], newVersion = [" + newVersion + "]");
		migrate(db);
	}

	/**
	 * migrate from whole SQLite db from V7 to V8 Realm db
	 */
	private void migrate(SQLiteDatabase db) {
		if (!ManagerPreferences.needsDbMigration()) {
			return;
		}
		Logger.w(TAG, "Migrating database started....");

		Realm realm = DeprecatedDatabase.get();
		realm.beginTransaction();
		realm.deleteAll();
		realm.commitTransaction();

		try {
			new Repo().migrate(db, realm);
		} catch (Exception ex) {
			logException(ex);
		}

		try {
			new Excluded().migrate(db, realm);
		} catch (Exception ex) {
			logException(ex);
		}

		try {
			new Installed().migrate(db, realm); // X
		} catch (Exception ex) {
			logException(ex);
		}

		try {
			new Rollback().migrate(db, realm);
		} catch (Exception ex) {
			logException(ex);
		}

		try {
			new Scheduled().migrate(db, realm); // X
		} catch (Exception ex) {
			logException(ex);
		}

		try {
			new Updates().migrate(db, realm); // despite the migration, this data should be recreated upon app startup
		} catch (Exception ex) {
			logException(ex);
		}

		// table "AmazonABTesting" was deliberedly left out due to its irrelevance in the DB upgrade
		// table "ExcludedAd" was deliberedly left out due to its irrelevance in the DB upgrade

		if(agregateExceptions!=null) {
			Crashlytics.logException(agregateExceptions);
		}

		ManagerPreferences.setNeedsDbMigration(false);
		Logger.w(TAG, "Migrating database finished with success.");
	}

	private void logException(Exception ex) {
		Logger.e(TAG, ex);

		if(agregateExceptions==null) {
			agregateExceptions = ex;
		}else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			agregateExceptions.addSuppressed(ex);
		}
	}
}
