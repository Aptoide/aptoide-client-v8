/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 29/08/2016.
 */

package cm.aptoide.pt.v8engine.deprecated.tables;

import android.database.Cursor;

import io.realm.RealmObject;

/**
 * Created by sithengineer on 24/08/16.
 */
public class Excluded extends BaseTable {

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_PACKAGE_NAME = "package_name";

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_NAME = "name";

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_ICONPATH = "iconpath";

	// @ColumnDefinition(type = SQLType.INTEGER)
	public final static String COLUMN_VERCODE = "vercode";

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_VERNAME = "version_name";

	private static final String NAME = "excluded";

	@Override
	public String getTableName() {
		return NAME;
	}

	@Override
	public RealmObject convert(Cursor cursor) {
		cm.aptoide.pt.database.realm.Update realmObject = new cm.aptoide.pt.database.realm.Update();

		realmObject.setPackageName(cursor.getString(cursor.getColumnIndex(COLUMN_PACKAGE_NAME)));
		realmObject.setLabel(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
		realmObject.setIcon(cursor.getString(cursor.getColumnIndex(COLUMN_ICONPATH)));
		realmObject.setVersionCode(cursor.getInt(cursor.getColumnIndex(COLUMN_VERCODE)));
		realmObject.setUpdateVersionName(cursor.getString(cursor.getColumnIndex(COLUMN_VERNAME)));
		realmObject.setExcluded(true);
		// are this columns to remove?
		// COLUMN_NAME
		return realmObject;
	}
}
