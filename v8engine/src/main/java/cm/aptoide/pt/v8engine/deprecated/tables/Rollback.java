/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 24/08/2016.
 */

package cm.aptoide.pt.v8engine.deprecated.tables;

import android.database.Cursor;
import android.text.TextUtils;

import io.realm.RealmObject;

/**
 * Created by sithengineer on 24/08/16.
 */
public class Rollback extends BaseTable {

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_ACTION = "action";

	// @ColumnDefinition(type = SQLType.TEXT, defaultValue = "")
	public final static String COLUMN_MD5 = "md5";

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_ICONPATH = "icon_path";

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_APKID = "package_name";

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_VERSION = "version";

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_NAME = "name";

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_TIMESTAMP = "timestamp";

	// @ColumnDefinition(type = SQLType.INTEGER)
	public final static String COLUMN_CONFIRMED = "confirmed";

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_REPO = "reponame";

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_IS_TRUSTED = "isTrusted";

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_PREVIOUS_VERSION = "previous_version";

	private static final String NAME = "rollbacktbl";

	@Override
	public String getTableName() {
		return NAME;
	}

	@Override
	public RealmObject convert(Cursor cursor) {
		cm.aptoide.pt.database.realm.Rollback realmObject = new cm.aptoide.pt.database.realm.Rollback();

		realmObject.setAppName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
		realmObject.setIconPath(cursor.getString(cursor.getColumnIndex(COLUMN_ICONPATH)));
		realmObject.setVersionName(cursor.getString(cursor.getColumnIndex(COLUMN_VERSION)));
		realmObject.setAction(cursor.getString(cursor.getColumnIndex(COLUMN_ACTION)));
		realmObject.setPackageName(cursor.getString(cursor.getColumnIndex(COLUMN_APKID)));
		realmObject.setMd5(cursor.getString(cursor.getColumnIndex(COLUMN_MD5)));

		String timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP));
		if(!TextUtils.isEmpty(timestamp)) {
			realmObject.setTimestamp(Long.parseLong(timestamp) * 1000);
		}

		realmObject.setConfirmed(cursor.getInt(cursor.getColumnIndex(COLUMN_CONFIRMED))==1);

		// ??  = cursor.getString(cursor.getColumnIndex(COLUMN_PREVIOUS_VERSION));

		return realmObject;
	}
}
