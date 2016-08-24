/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 24/08/2016.
 */

package cm.aptoide.pt.v8engine.deprecated.tables;

import android.database.Cursor;

import io.realm.RealmObject;

/**
 * Created by sithengineer on 24/08/16.
 */
public final class Installed extends BaseTable {

	public static final String NAME = "installed";

	// @ColumnDefinition(type = SQLType.INTEGER, primaryKey = true, autoIncrement = true)
	public static final String COLUMN_ID = "id_installed";

	// @ColumnDefinition(type = SQLType.TEXT, unique = true, onConflict = OnConflict.REPLACE)
	public final static String COLUMN_APKID = "package_name";

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_NAME = "name";

	// @ColumnDefinition(type = SQLType.INTEGER, defaultValue = "0")
	public final static String COLUMN_VERCODE = "version_code";

	// @ColumnDefinition(type = SQLType.TEXT, defaultValue = "")
	public final static String COLUMN_VERNAME = "version_name";

	// @ColumnDefinition(type = SQLType.TEXT, defaultValue = "")
	public final static String COLUMN_SIGNATURE = "signature";

	@Override
	public String getTableName() {
		return NAME;
	}

	@Override
	public RealmObject convert(Cursor cursor) {
		cm.aptoide.pt.database.realm.Installed realmObject = new cm.aptoide.pt.database.realm.Installed();

		// TODO: 24/08/16 sithengineer

		return realmObject;
	}

	@Override
	public String[] getColumns() {
		return new String[]{Installed.COLUMN_APKID, Installed.COLUMN_ID, Installed.COLUMN_NAME, Installed.COLUMN_SIGNATURE, Installed.COLUMN_VERCODE,
				Installed.COLUMN_VERNAME};
	}
}
