/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 29/08/2016.
 */

package cm.aptoide.pt.v8engine.deprecated.tables;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.text.TextUtils;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import io.realm.RealmObject;

/**
 * Created by sithengineer on 24/08/16.
 *
 * see method "getStartupInstalled()" in class "AptoideDatabase.java" from v7 code to understand
 * the reason why of this code
 */
public final class Installed extends BaseTable {

  private static final String TAG = Installed.class.getSimpleName();
  private final PackageManager pm = AptoideUtils.getContext()
      .getPackageManager();
  /*
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
  */
  private Updates updatesTable = new Updates();

  @Override public String getTableName() {
    return updatesTable.getTableName();
  }

  @Override public RealmObject convert(Cursor cursor) {

    String path = cursor.getString(cursor.getColumnIndex(Updates.COLUMN_URL));
    String packageName = cursor.getString(cursor.getColumnIndex(Updates.COLUMN_PACKAGE));
    if (TextUtils.isEmpty(path)) {
      try {
        PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
        ApplicationInfo appInfo = packageInfo.applicationInfo;

        if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
          return new cm.aptoide.pt.database.realm.Installed(packageInfo);
        }
      } catch (PackageManager.NameNotFoundException ex) {
        CrashReport.getInstance()
            .log(ex);
      }
    }
    return null;
  }
}
