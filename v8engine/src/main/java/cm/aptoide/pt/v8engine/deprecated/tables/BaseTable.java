/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 29/08/2016.
 */

package cm.aptoide.pt.v8engine.deprecated.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import cm.aptoide.pt.database.accessors.Accessor;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import io.realm.RealmObject;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by sithengineer on 24/08/16.
 */
public abstract class BaseTable {

  private static final String TAG = BaseTable.class.getSimpleName();

  private static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS ";

  public void migrate(SQLiteDatabase db, Accessor<RealmObject> accessor) {
    Cursor cursor = null;
    try {

      String tableName = getTableName().toLowerCase(Locale.ENGLISH);

      String customQuery = getCustomQuery();
      if (TextUtils.isEmpty(customQuery)) {
        cursor = db.rawQuery("select * from " + tableName, null);
      } else {
        cursor = db.rawQuery(customQuery, null);
      }

      if (cursor == null || cursor.isAfterLast() || cursor.isClosed()) {
        throw new IllegalStateException("Cursor for table " + tableName + " is not available");
      }

      ArrayList<RealmObject> objs = new ArrayList<>();
      RealmObject converted;
      for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
        converted = convert(cursor);
        if (converted != null) objs.add(converted);
      }
      if (objs.size() > 0 && accessor != null) {
        accessor.insertAll(objs);
      }

      if (accessor == null) {
        throw new RuntimeException("Accessor ir null for table " + tableName);
      }

      // delete migrated table
      // TODO: 29/08/16 sithengineer uncomment the following lines when migration script is stable
      //			db.beginTransaction();
      //			db.execSQL(DROP_TABLE_SQL + tableName);
      //			db.endTransaction();

      Logger.d(TAG, "Table " + tableName + " migrated with success.");
    } catch (Exception e) {
      CrashReport.getInstance()
          .log(e);
    } finally {
      if (cursor != null && !cursor.isClosed()) {
        cursor.close();
      }
    }
  }

  public abstract String getTableName();

  public String getCustomQuery() {
    return null;
  }

  public abstract RealmObject convert(Cursor cursor);
}
