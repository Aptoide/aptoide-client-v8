/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 20/05/2016.
 */

package cm.aptoide.pt.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import cm.aptoide.pt.database.migration.Schema;
import cm.aptoide.pt.database.realm.ExcludedAd;
import cm.aptoide.pt.database.realm.ExcludedUpdate;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.database.realm.Updates;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.IdUtils;
import io.realm.Realm;
import io.realm.RealmModel;

/**
 * Created by sithengineer on 19/05/16.
 */
public class SQLiteToRealmMigration {

	private static final String TAG = SQLiteToRealmMigration.class.getName();

	private static final int MINIMUM_DB_VERSION = 0;

	private static volatile boolean IS_MIGRATED = false;

	private final SQLiteDatabase sqliteDb;
	private final Realm realm;

	public SQLiteToRealmMigration(SQLiteDatabase sqliteDatabase, Realm realm) {
		this.sqliteDb = sqliteDatabase;
		this.realm = realm;
	}

	public SQLiteDatabase getSqliteDatabase() {
		return sqliteDb;
	}

	public boolean migrate() {
		if (IS_MIGRATED) {
			throw new IllegalStateException("Database already migrated");
		}

		int currentSqliteDbVersion = sqliteDb.getVersion();
		if (currentSqliteDbVersion < MINIMUM_DB_VERSION) {
			Logger.e(TAG, String.format(Locale.ROOT, "unable to migrate SQLite DB with version %d. Minimum " +
					"required version is %d", currentSqliteDbVersion, MINIMUM_DB_VERSION));
			return false;
		}

		try {
			Realm.deleteRealm(realm.getConfiguration());

			boolean success = true;

//			if(!migrateInstalled()) success = false;
			if (!migrateRepo()) {
				success = false;
			}
			if (!migrateRollbackTbl()) {
				success = false;
			}
			if (!migrateExcluded()) {
				success = false;
			}
			if (!migrateExcludedAds()) {
				success = false;
			}
			//if(!migrateScheduled()) success = false;
			//if(!migrateUpdates()) success = false;

			if (!success) {
				throw new IllegalStateException("");
			}

			IS_MIGRATED = true;
		}
		catch (Exception e) {
			Logger.e(TAG, "", e);
			return false;
		}
		return true;
	}

	private boolean migrate(String tableName, Collection<? extends RealmModel> realmModels) {
		realm.beginTransaction();
		try {
			realm.copyToRealm(realmModels);
			realm.commitTransaction();
			return true;
		}
		catch (Exception e) {
			Logger.e(TAG, "migrate table " + tableName, e);
			realm.cancelTransaction();
			return false;
		}
	}

	private ExcludedAd getExcludedAdFrom(Cursor cursor) {
		ExcludedAd excludedAd = new ExcludedAd();
		excludedAd.setPackageName(cursor.getString(cursor.getColumnIndex(Schema.ExcludedAds.COLUMN_PACKAGE)));
		return excludedAd;
	}

	private boolean migrateExcludedAds() {
		String table_name = Schema.ExcludedAds.getName();
		Cursor cursor = sqliteDb.query(table_name, null, null, null, null, null, null);

		if (cursor == null || cursor.isAfterLast() || cursor.isClosed()) {
			throw new IllegalStateException(table_name + " table is not available");
		}

		ArrayList<ExcludedAd> excludedAds = new ArrayList<>(cursor.getCount());

		while (cursor.moveToNext()) {
			excludedAds.add(getExcludedAdFrom(cursor));
		}

		return migrate(table_name, excludedAds);
	}

	private Updates getUpdateFrom(Cursor cursor) {
		Updates update = new Updates();
		update.setIcon(cursor.getString(cursor.getColumnIndex(Schema.Updates.COLUMN_ICON)));
		update.setMd5(cursor.getString(cursor.getColumnIndex(Schema.Updates.COLUMN_MD5)));
		update.setPackageName(cursor.getString(cursor.getColumnIndex(Schema.Updates.COLUMN_PACKAGE)));
		//update.setAppId(cursor.getInt(cursor.getColumnIndex(Schema.Updates.COLUMN_VERCODE)));
		update.setAlternativeUrl(cursor.getString(cursor.getColumnIndex(Schema.Updates.COLUMN_ALT_URL)));
		update.setFileSize(cursor.getDouble(cursor.getColumnIndex(Schema.Updates.COLUMN_FILESIZE)));
		update.setSignature(cursor.getString(cursor.getColumnIndex(Schema.Updates.COLUMN_SIGNATURE)));
		update.setTimestamp(cursor.getLong(cursor.getColumnIndex(Schema.Updates.COLUMN_TIMESTAMP)));
		update.setUpdateVersionCode(cursor.getString(cursor.getColumnIndex(Schema.Updates.COLUMN_UPDATE_VERCODE)));
		update.setUpdateVersionName(cursor.getString(cursor.getColumnIndex(Schema.Updates.COLUMN_UPDATE_VERNAME)));
		update.setUrl(cursor.getString(cursor.getColumnIndex(Schema.Updates.COLUMN_URL)));
		update.setVersionCode(cursor.getInt(cursor.getColumnIndex(Schema.Updates.COLUMN_VERCODE)));
		return update;
	}

	private boolean migrateUpdates() {
		/*
		String table_name = Schema.Updates.getName();
		Cursor cursor = sqliteDb.query(table_name, null, null, null, null, null, null);

		if (cursor == null || cursor.isAfterLast() || cursor.isClosed()) {
			throw new IllegalStateException(table_name + " table is not available");
		}

		ArrayList<Updates> updates = new ArrayList<>(cursor.getCount());

		while (cursor.moveToNext()) {
			updates.add(getUpdateFrom(cursor));
		}

		return migrate(table_name, updates);
		*/
		return true;
	}

	private Scheduled getScheduledFrom(Cursor cursor) {
		Scheduled scheduled = new Scheduled();
		scheduled.setName(cursor.getString(cursor.getColumnIndex(Schema.Scheduled.COLUMN_NAME)));
		scheduled.setIcon(cursor.getString(cursor.getColumnIndex(Schema.Scheduled.COLUMN_ICON)));
		scheduled.setVersionName(cursor.getString(cursor.getColumnIndex(Schema.Scheduled.COLUMN_VERSION_NAME)));
		// FIXME scheduled.setAppId( ?? );
		return scheduled;
	}

	private boolean migrateScheduled() {
		/*
		String table_name = Schema.Scheduled.getName();
		Cursor cursor = sqliteDb.query(table_name, null, null, null, null, null, null);

		if (cursor == null || cursor.isAfterLast() || cursor.isClosed()) {
			throw new IllegalStateException(table_name + " table is not available");
		}

		ArrayList<Scheduled> scheduledList = new ArrayList<>(cursor.getCount());

		while (cursor.moveToNext()) {
			scheduledList.add(getScheduledFrom(cursor));
		}

		return migrate(table_name, scheduledList);
		*/
		return true;
	}

	private final IdUtils excludedUpdateIdUtils = new IdUtils(0);
	private ExcludedUpdate getExcludedUpdateFrom(Cursor cursor) {
		ExcludedUpdate excludedUpdate = new ExcludedUpdate();
		excludedUpdate.setId(excludedUpdateIdUtils.nextLong());
		excludedUpdate.setName(cursor.getString(cursor.getColumnIndex(Schema.Excluded.COLUMN_NAME)));
		excludedUpdate.setPackageName(cursor.getString(cursor.getColumnIndex(Schema.Excluded.COLUMN_PACKAGE_NAME)));
		excludedUpdate.setIcon(cursor.getString(cursor.getColumnIndex(Schema.Excluded.COLUMN_ICONPATH)));
		// TODO is this to remove?
		// Schema.Excluded.COLUMN_VERCODE
		// Schema.Excluded.COLUMN_VERNAME
		return excludedUpdate;
	}

	private boolean migrateExcluded() {
		String table_name = Schema.Excluded.getName();
		Cursor cursor = sqliteDb.query(table_name, null, null, null, null, null, null);

		if (cursor == null || cursor.isAfterLast() || cursor.isClosed()) {
			throw new IllegalStateException(table_name + " table is not available");
		}

		ArrayList<ExcludedUpdate> excludedUpdates = new ArrayList<>(cursor.getCount());

		while (cursor.moveToNext()) {
			excludedUpdates.add(getExcludedUpdateFrom(cursor));
		}

		return migrate(table_name, excludedUpdates);
	}

	private final IdUtils rollbackIdUtils = new IdUtils(0);
	private Rollback getRollbackFrom(Cursor cursor) {
		Rollback rollback = new Rollback();
		rollback.setMd5(cursor.getString(cursor.getColumnIndex(Schema.RollbackTbl.COLUMN_MD5)));
		rollback.setName(cursor.getString(cursor.getColumnIndex(Schema.RollbackTbl.COLUMN_NAME)));
		rollback.setVersionName(cursor.getString(cursor.getColumnIndex(Schema.RollbackTbl.COLUMN_VERSION)));
		rollback.setIconPath(cursor.getString(cursor.getColumnIndex(Schema.RollbackTbl.COLUMN_ICONPATH)));
		// rollback.setIcon(cursor.getInt(cursor.getColumnIndex(Schema.RollbackTbl.COLUMN_ ?? )));
		rollback.setAction(cursor.getInt(cursor.getColumnIndex(Schema.RollbackTbl.COLUMN_ACTION)));
		rollback.setConfirmed(cursor.getInt(cursor.getColumnIndex(Schema.RollbackTbl.COLUMN_CONFIRMED)));

		//rollback.setId(cursor.getInt(cursor.getColumnIndex(Schema.RollbackTbl.COLUMN_APKID))); // FIXME is this
		// correct ?
		rollback.setId((int)rollbackIdUtils.nextLong());

		//rollback.setPreviousVersionMd5(cursor.getString(cursor.getColumnIndex(Schema.RollbackTbl.COLUMN_ ?? )));
		//rollback.setPreviousVersionName(cursor.getString(cursor.getColumnIndex(Schema.RollbackTbl
//				.COLUMN_PREVIOUS_VERSION)));
		rollback.setStoreName(cursor.getString(cursor.getColumnIndex(Schema.RollbackTbl.COLUMN_REPO)));
		rollback.setTimestamp(
				cursor.getString(cursor.getColumnIndex(Schema.RollbackTbl.COLUMN_TIMESTAMP))
		);

		return rollback;
	}

	private boolean migrateRollbackTbl() {
		String table_name = Schema.RollbackTbl.getName();
		Cursor cursor = sqliteDb.query(table_name, null, null, null, null, null, null);

		if (cursor == null || cursor.isAfterLast() || cursor.isClosed()) {
			throw new IllegalStateException(table_name + " table is not available");
		}

		ArrayList<Rollback> rollbackList = new ArrayList<>(cursor.getCount());

		while (cursor.moveToNext()) {
			rollbackList.add(getRollbackFrom(cursor));
		}

		return migrate(table_name, rollbackList);
	}

	private Store getStoreFrom(Cursor cursor) {
		Store store = new Store();
		store.setStoreId(cursor.getLong(cursor.getColumnIndex(Schema.Repo.COLUMN_ID)));
		store.setDownloads(cursor.getLong(cursor.getColumnIndex(Schema.Repo.COLUMN_DOWNLOADS)));
		store.setStoreName(cursor.getString(cursor.getColumnIndex(Schema.Repo.COLUMN_NAME)));
		store.setIconPath(cursor.getString(cursor.getColumnIndex(Schema.Repo.COLUMN_ICONS_PATH)));
		store.setPasswordSha1(cursor.getString(cursor.getColumnIndex(Schema.Repo.COLUMN_PASSWORD)));
		store.setUsername(cursor.getString(cursor.getColumnIndex(Schema.Repo.COLUMN_USERNAME)));
		store.setTheme(cursor.getString(cursor.getColumnIndex(Schema.Repo.COLUMN_THEME)));
		return store;
	}

	private boolean migrateRepo() {
		String table_name = Schema.Repo.getName();
		Cursor cursor = sqliteDb.query(table_name, null, null, null, null, null, null);

		if (cursor == null || cursor.isAfterLast() || cursor.isClosed()) {
			throw new IllegalStateException(table_name + " table is not available");
		}

		ArrayList<Store> storeList = new ArrayList<>(cursor.getCount());

		while (cursor.moveToNext()) {
			storeList.add(getStoreFrom(cursor));
		}

		return migrate(table_name, storeList);
	}

	private Installed getInstalledFrom(Cursor cursor) {
		Installed installed = new Installed();
		installed.setId(cursor.getInt(cursor.getColumnIndex(Schema.Installed.COLUMN_ID)));
		installed.setName(cursor.getString(cursor.getColumnIndex(Schema.Installed.COLUMN_NAME)));
		installed.setVersionName(cursor.getString(cursor.getColumnIndex(Schema.Installed.COLUMN_VERNAME)));
		installed.setVersionCode(
				Integer.valueOf(cursor.getString(cursor.getColumnIndex(Schema.Installed.COLUMN_VERCODE)), 10 )
		);
		installed.setPackageName(cursor.getString(cursor.getColumnIndex(Schema.Installed.COLUMN_APKID))); // FIXME is
		// this correct?
		installed.setSignature(cursor.getString(cursor.getColumnIndex(Schema.Installed.COLUMN_SIGNATURE)));
		return installed;
	}

	private boolean migrateInstalled() {
		String table_name = Schema.Installed.getName();
		Cursor cursor = sqliteDb.query(table_name, null, null, null, null, null, null);

		if (cursor == null || cursor.isAfterLast() || cursor.isClosed()) {
			throw new IllegalStateException(table_name + " table is not available");
		}

		ArrayList<Installed> storeList = new ArrayList<>(cursor.getCount());

		while (cursor.moveToNext()) {
			storeList.add(getInstalledFrom(cursor));
		}

		return migrate(table_name, storeList);
	}
}
