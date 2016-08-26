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
public class Repo extends BaseTable {

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_URL = "url";

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_APK_PATH = "apk_path";

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_ICONS_PATH = "icons_path";

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_WEBSERVICES_PATH = "webservices_path";

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_HASH = "hash";

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_THEME = "theme";

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_AVATAR = "avatar_url";

	// @ColumnDefinition(type = SQLType.INTEGER)
	public final static String COLUMN_DOWNLOADS = "downloads";

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_DESCRIPTION = "description";

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_VIEW = "list";

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_ITEMS = "items";

	// @ColumnDefinition(type = SQLType.INTEGER)
	public final static String COLUMN_LATEST_TIMESTAMP = "latest_timestamp";

	// @ColumnDefinition(type = SQLType.INTEGER)
	public final static String COLUMN_TOP_TIMESTAMP = "top_timestamp";

	// @ColumnDefinition(type = SQLType.BOOLEAN)
	public final static String COLUMN_IS_USER = "is_user";

	// @ColumnDefinition(type = SQLType.BOOLEAN)
	public final static String COLUMN_FAILED = "is_failed";

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_NAME = "name";

	// from migration script
	public final static String COLUMN_REPO_NAME = "reponame";

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_USERNAME = "username";

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_PASSWORD = "password";

	// @ColumnDefinition(type = SQLType.INTEGER, primaryKey = true, autoIncrement = true)
	public final static String COLUMN_ID = "id_repo";

	// @ColumnDefinition(type = SQLType.TEXT)
	public final static String COLUMN_FEATURED_GRAPHIC_PATH = "featured_graphic_path";

	private static final String NAME = "repo";
	
	@Override
	public String getTableName() {
		return NAME;
	}

	@Override
	public RealmObject convert(Cursor cursor) {
		cm.aptoide.pt.database.realm.Store realmObject = new cm.aptoide.pt.database.realm.Store();
		realmObject.setStoreId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
		realmObject.setDownloads(cursor.getLong(cursor.getColumnIndex(COLUMN_DOWNLOADS)));
		realmObject.setStoreName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
		realmObject.setIconPath(cursor.getString(cursor.getColumnIndex(COLUMN_ICONS_PATH)));
		realmObject.setPasswordSha1(cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD)));
		realmObject.setUsername(cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME)));
		realmObject.setTheme(cursor.getString(cursor.getColumnIndex(COLUMN_THEME)));
		return realmObject;
	}
}
