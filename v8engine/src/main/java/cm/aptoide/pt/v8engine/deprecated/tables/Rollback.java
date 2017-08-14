/*
 * Copyright (c) 2016.
 * Modified on 24/08/2016.
 */

package cm.aptoide.pt.v8engine.deprecated.tables;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.text.TextUtils;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.deprecated.OldActionsMap;
import io.realm.RealmObject;

/**
 * Created on 24/08/16.
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

  private static final String NAME = "rollbacktbl";

  @Override public String getTableName() {
    return NAME;
  }

  @Override
  public RealmObject convert(Cursor cursor, PackageManager packageManager, Context context) {

    String oldActionAsString = cursor.getString(cursor.getColumnIndex(COLUMN_ACTION));
    int oldActionMergeCharPosition = oldActionAsString.lastIndexOf('|');
    if (oldActionMergeCharPosition > -1) {
      // this must be done to extract the referrer from the old Rollback.Action field
      oldActionAsString = oldActionAsString.substring(0, oldActionMergeCharPosition);
    }
    cm.aptoide.pt.database.realm.Rollback.Action oldAction =
        OldActionsMap.getActionFor(oldActionAsString);
    if (oldAction != null) {

      cm.aptoide.pt.database.realm.Rollback realmObject =
          new cm.aptoide.pt.database.realm.Rollback();

      realmObject.setConfirmed(true);
      realmObject.setAction(oldAction.name());

      realmObject.setAppName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
      realmObject.setVersionName(cursor.getString(cursor.getColumnIndex(COLUMN_VERSION)));
      realmObject.setPackageName(cursor.getString(cursor.getColumnIndex(COLUMN_APKID)));
      realmObject.setMd5(cursor.getString(cursor.getColumnIndex(COLUMN_MD5)));

      String timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP));
      if (!TextUtils.isEmpty(timestamp)) {
        realmObject.setTimestamp(Long.parseLong(timestamp) * 1000);
      }

      realmObject.setConfirmed(cursor.getInt(cursor.getColumnIndex(COLUMN_CONFIRMED)) == 1);

      String cleanIconPath = AptoideUtils.IconSizeU.cleanImageUrl(
          cursor.getString(cursor.getColumnIndex(COLUMN_ICONPATH)));
      realmObject.setIconPath(cleanIconPath);

      return realmObject;
    }

    return null;
  }
}
