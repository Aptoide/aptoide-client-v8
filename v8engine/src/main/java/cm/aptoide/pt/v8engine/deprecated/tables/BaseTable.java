/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 29/08/2016.
 */

package cm.aptoide.pt.v8engine.deprecated.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Locale;

import cm.aptoide.pt.logger.Logger;
import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by sithengineer on 24/08/16.
 */
public abstract class BaseTable {

	private static final String TAG = BaseTable.class.getSimpleName();

	private static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS ";

	public abstract String getTableName();

	public abstract RealmObject convert(Cursor cursor);

	public String getCustomQuery() {
		return null;
	}

	public void migrate(SQLiteDatabase db, Realm realm) {
		Cursor cursor = null;
		try {

			String tableName = getTableName().toLowerCase(Locale.ENGLISH);

			String customQuery = getCustomQuery();
			if(TextUtils.isEmpty(customQuery)) {
				cursor = db.rawQuery("select * from " + tableName, null);
			} else {
				cursor = db.rawQuery(customQuery, null);
			}

			if (cursor == null || cursor.isAfterLast() || cursor.isClosed()) {
				throw new IllegalStateException("Cursor for table " + tableName + " is not available");
			}

			ArrayList<RealmObject> objs = new ArrayList<>();
			RealmObject converted;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				converted = convert(cursor);
				if(converted!=null) objs.add(converted);
			}
			if (objs.size() > 0) {
				realm.beginTransaction();
				realm.copyToRealmOrUpdate(objs);
				realm.commitTransaction();
			}

			// delete migrated table
			// FIXME: 29/08/16 sithengineer uncomment the following lines when migration script is stable
//			db.beginTransaction();
//			db.execSQL(DROP_TABLE_SQL + tableName);
//			db.endTransaction();

			Logger.d(TAG, "Table " + tableName + " migrated with success.");
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}
}
