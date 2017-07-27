/*
 * Copyright (c) 2016.
 * Modified on 29/08/2016.
 */

package cm.aptoide.pt.v8engine.deprecated.tables;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import cm.aptoide.pt.utils.AptoideUtils;
import io.realm.RealmObject;

/**
 * Created on 24/08/16.
 */
public class Scheduled extends BaseTable {

  // @ColumnDefinition(type = SQLType.TEXT)
  public final static String COLUMN_NAME = "name";
  // @ColumnDefinition(type = SQLType.TEXT)
  public final static String COLUMN_VERSION_NAME = "version_name";
  // @ColumnDefinition(type = SQLType.TEXT)
  public final static String COLUMN_MD5 = "md5";
  // @ColumnDefinition(type = SQLType.TEXT)
  public final static String COLUMN_ICON = "icon";
  // @ColumnDefinition(type = SQLType.TEXT)
  public final static String COLUMN_PACKAGE_NAME = "package_name";
  // @ColumnDefinition(type = SQLType.TEXT)
  public final static String COLUMN_REPO = "repo_name";
  private static final String NAME = "scheduled";

  @Override public String getTableName() {
    return NAME;
  }

  @Override
  public RealmObject convert(Cursor cursor, PackageManager packageManager, Context context) {
    cm.aptoide.pt.database.realm.Scheduled realmObject =
        new cm.aptoide.pt.database.realm.Scheduled();
    realmObject.setMd5(cursor.getString(cursor.getColumnIndex(COLUMN_MD5)));
    realmObject.setPackageName(cursor.getString(cursor.getColumnIndex(COLUMN_PACKAGE_NAME)));
    realmObject.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
    realmObject.setVersionName(cursor.getString(cursor.getColumnIndex(COLUMN_VERSION_NAME)));
    realmObject.setStoreName(cursor.getString(cursor.getColumnIndex(COLUMN_REPO)));
    realmObject.setIcon(cursor.getString(cursor.getColumnIndex(COLUMN_ICON)));

    String cleanIconPath =
        AptoideUtils.IconSizeU.cleanImageUrl(cursor.getString(cursor.getColumnIndex(COLUMN_ICON)));
    realmObject.setIcon(cleanIconPath);
    return realmObject;
  }
}
