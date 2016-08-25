/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 25/08/2016.
 */

package cm.aptoide.pt.v8engine.deprecated.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import cm.aptoide.pt.logger.Logger;
import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by sithengineer on 24/08/16.
 */
public abstract class BaseTable {

	private static final String TAG = BaseTable.class.getSimpleName();

	public abstract String getTableName();

	public abstract RealmObject convert(Cursor cursor);

	public abstract String[] getColumns();

	public void migrate(SQLiteDatabase db, Realm realm) {
		Cursor cursor = null;
		try {

			// TODO: 24/08/16 sithengineer check if database exists first

			String tableName = getTableName();
			cursor = db.query(tableName, getColumns(), null, null, null, null, null);

			if (cursor == null || cursor.isAfterLast() || cursor.isClosed()) {
				throw new IllegalStateException(tableName + " table is not available");
			}

			ArrayList<RealmObject> objs = new ArrayList<>(cursor.getCount());
			while (cursor.moveToNext()) {
				objs.add(convert(cursor));
			}
			realm.beginTransaction();
			realm.copyToRealmOrUpdate(objs);
			realm.commitTransaction();
			Logger.i(TAG, "Table " + tableName + " migrated with success.");
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}
}
