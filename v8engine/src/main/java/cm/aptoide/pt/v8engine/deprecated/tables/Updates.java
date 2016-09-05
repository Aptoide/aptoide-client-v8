/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.deprecated.tables;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.text.TextUtils;

import cm.aptoide.pt.database.accessors.DeprecatedDatabase;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.AptoideUtils;
import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by sithengineer on 24/08/16.
 */
public class Updates extends BaseTable {

	// @ColumnDefinition(type = SQLType.TEXT)
	public static final String COLUMN_PACKAGE = "package_name";
	// @ColumnDefinition(type = SQLType.INTEGER)
	public static final String COLUMN_VERCODE = "version_code";
	// @ColumnDefinition(type = SQLType.DATE)
	public static final String COLUMN_TIMESTAMP = "timestamp";
	// @ColumnDefinition(type = SQLType.TEXT)
	public static final String COLUMN_MD5 = "md5";
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
	// @ColumnDefinition(type = SQLType.TEXT)
	public static final String COLUMN_REPO = "repo";
	// @ColumnDefinition(type = SQLType.TEXT)
	public static final String COLUMN_SIGNATURE = "signature";
	private static final String TAG = Updates.class.getSimpleName();
	private static final String NAME = "updates";
	private static final Realm realm = Realm.getDefaultInstance();
	private final PackageManager pm = AptoideUtils.getContext().getPackageManager();

	@Override
	public String getTableName() {
		return NAME;
	}

	@Override
	public RealmObject convert(Cursor cursor) {

		String path = cursor.getString(cursor.getColumnIndex(Updates.COLUMN_URL));
		String packageName = cursor.getString(cursor.getColumnIndex(Updates.COLUMN_PACKAGE));
		if (!TextUtils.isEmpty(path) && !isExcluded(packageName)) {
			try {
				PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
				cm.aptoide.pt.database.realm.Update realmObject = new cm.aptoide.pt.database.realm.Update();

				realmObject.setIcon(cursor.getString(cursor.getColumnIndex(COLUMN_ICON)));
				realmObject.setMd5(cursor.getString(cursor.getColumnIndex(COLUMN_MD5)));
				realmObject.setPackageName(cursor.getString(cursor.getColumnIndex(COLUMN_PACKAGE)));
				realmObject.setAlternativeApkPath(cursor.getString(cursor.getColumnIndex(COLUMN_ALT_URL)));
				realmObject.setFileSize(cursor.getDouble(cursor.getColumnIndex(COLUMN_FILESIZE)));
				realmObject.setTimestamp(cursor.getLong(cursor.getColumnIndex(COLUMN_TIMESTAMP)));

				realmObject.setUpdateVersionName(cursor.getString(cursor.getColumnIndex(COLUMN_UPDATE_VERNAME)));
				if (TextUtils.isEmpty(realmObject.getUpdateVersionName())) {
					realmObject.setUpdateVersionName(packageInfo.versionName);
				}

				realmObject.setApkPath(cursor.getString(cursor.getColumnIndex(COLUMN_URL)));
				realmObject.setVersionCode(cursor.getInt(cursor.getColumnIndex(COLUMN_VERCODE)));
				try {
					int colIndex = cursor.getColumnIndex(COLUMN_UPDATE_VERCODE);
					if(!cursor.isNull(colIndex)){
						int vercode = Integer.valueOf(cursor.getString(colIndex), 10);
						realmObject.setUpdateVersionCode(vercode);
					}
				} catch (NumberFormatException ex) {
					ex.printStackTrace();
				}

				return realmObject;
			} catch (PackageManager.NameNotFoundException ex) {
				Logger.e(TAG, ex);
			}
		}

		return null;
	}

	private boolean isExcluded(String packageName) {
		return DeprecatedDatabase.get().where(Update.class).equalTo(Update.PACKAGE_NAME, packageName).equalTo(Update.EXCLUDED, true).findFirst() != null;
	}
}
