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

  @Override public String getTableName() {
    return NAME;
  }

  @Override
  public RealmObject convert(Cursor cursor, PackageManager packageManager, Context context) {
    cm.aptoide.pt.database.realm.Update realmObject = new cm.aptoide.pt.database.realm.Update();

    realmObject.setPackageName(cursor.getString(cursor.getColumnIndex(COLUMN_PACKAGE_NAME)));
    realmObject.setLabel(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
    realmObject.setVersionCode(cursor.getInt(cursor.getColumnIndex(COLUMN_VERCODE)));
    realmObject.setUpdateVersionName(cursor.getString(cursor.getColumnIndex(COLUMN_VERNAME)));
    realmObject.setExcluded(true);

    String cleanIconPath = AptoideUtils.IconSizeU.cleanImageUrl(
        cursor.getString(cursor.getColumnIndex(COLUMN_ICONPATH)));
    realmObject.setIcon(cleanIconPath);

    // are this columns to remove?
    // COLUMN_NAME
    return realmObject;
  }
}
