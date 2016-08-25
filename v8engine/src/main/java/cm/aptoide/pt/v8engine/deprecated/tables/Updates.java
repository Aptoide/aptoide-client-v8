/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 25/08/2016.
 */

package cm.aptoide.pt.v8engine.deprecated.tables;

import android.database.Cursor;

import io.realm.RealmObject;

/**
 * Created by sithengineer on 24/08/16.
 */
public class Updates extends BaseTable {

	// @ColumnDefinition(type = SQLType.TEXT)
	public static final String COLUMN_PACKAGE = "package_name";

	// @ColumnDefinition(type = SQLType.INTEGER)
	public static final String COLUMN_VERCODE = "version_code";

	// @ColumnDefinition(type = SQLType.TEXT)
	public static final String COLUMN_SIGNATURE = "signature";

	// @ColumnDefinition(type = SQLType.DATE)
	public static final String COLUMN_TIMESTAMP = "timestamp";

	// @ColumnDefinition(type = SQLType.TEXT)
	public static final String COLUMN_MD5 = "md5";

	// @ColumnDefinition(type = SQLType.TEXT)
	public static final String COLUMN_REPO = "repo";

	// @ColumnDefinition(type = SQLType.TEXT)
	public static final String COLUMN_URL = "url";

	// @ColumnDefinition(type = SQLType.REAL)
	public static final String COLUMN_FILESIZE = "filesize";

	// @ColumnDefinition(type = SQLType.TEXT)
	public static final String COLUMN_UPDATE_VERNAME = "update_vername";

	// @ColumnDefinition(type = SQLType.TEXT)
	public static final String COLUMN_ALT_URL = "alt_url";

	// @ColumnDefinition(type = SQLType.TEXT)
	public static final String COLUMN_ICON = "icon";

	// @ColumnDefinition(type = SQLType.TEXT)
	public static final String COLUMN_UPDATE_VERCODE = "update_vercode";

	private static final String NAME = "updates";

	@Override
	public String getTableName() {
		return NAME;
	}

	@Override
	public RealmObject convert(Cursor cursor) {
		cm.aptoide.pt.database.realm.Update realmObject = new cm.aptoide.pt.database.realm.Update();

		// TODO: 24/08/16 sithengineer
		realmObject.setIcon(cursor.getString(cursor.getColumnIndex(COLUMN_ICON)));
		realmObject.setMd5(cursor.getString(cursor.getColumnIndex(COLUMN_MD5)));
		realmObject.setPackageName(cursor.getString(cursor.getColumnIndex(COLUMN_PACKAGE)));
		//realmObject.setAppId(cursor.getInt(cursor.getColumnIndex(COLUMN_VERCODE)));
		//realmObject.setAlternativeUrl(cursor.getString(cursor.getColumnIndex(COLUMN_ALT_URL)));
		realmObject.setFileSize(cursor.getDouble(cursor.getColumnIndex(COLUMN_FILESIZE)));
		//realmObject.setSignature(cursor.getString(cursor.getColumnIndex(COLUMN_SIGNATURE)));
		realmObject.setTimestamp(cursor.getLong(cursor.getColumnIndex(COLUMN_TIMESTAMP)));
		realmObject.setUpdateVersionName(cursor.getString(cursor.getColumnIndex(COLUMN_UPDATE_VERNAME)));
		//realmObject.setUrl(cursor.getString(cursor.getColumnIndex(COLUMN_URL)));
		realmObject.setVersionCode(cursor.getInt(cursor.getColumnIndex(COLUMN_VERCODE)));
		try {
			int vercode = Integer.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_UPDATE_VERCODE)), 10);
			realmObject.setUpdateVersionCode(vercode);
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
		}

		return realmObject;
	}

	@Override
	public String[] getColumns() {
		return new String[]{COLUMN_ICON, COLUMN_PACKAGE, COLUMN_VERCODE, COLUMN_SIGNATURE, COLUMN_TIMESTAMP, COLUMN_MD5, COLUMN_REPO, COLUMN_URL,
				COLUMN_FILESIZE, COLUMN_UPDATE_VERNAME, COLUMN_ALT_URL, COLUMN_UPDATE_VERCODE};
	}
}
