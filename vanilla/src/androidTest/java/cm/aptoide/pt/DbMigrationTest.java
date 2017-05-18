/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 12/04/2016.
 */

package cm.aptoide.pt;

import android.content.ContentValues;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Pair;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.ScheduledAccessor;
import cm.aptoide.pt.database.accessors.UpdateAccessor;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.table.ColumnDefinition;
import cm.aptoide.pt.table.ExcludedTable;
import cm.aptoide.pt.table.ScheduledTable;
import cm.aptoide.pt.table.Table;
import cm.aptoide.pt.table.TableBag;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.deprecated.SQLiteDatabaseHelper;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class) @LargeTest public class DbMigrationTest {

  private static TableBag tableBag;

  private static AtomicInteger dbVersion;
  private SQLiteDatabase db;
  private SQLiteDatabaseHelper dbHelper;

  @BeforeClass public static void classSetup() {
    tableBag = new TableBag();
    dbVersion = new AtomicInteger(0);
  }

  static String convertStreamToString(java.io.InputStream is) {
    java.util.Scanner s = new java.util.Scanner(is, "UTF-8").useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }

  @Before public void setup() {
    db = SQLiteDatabase.openDatabase(":memory:", null, SQLiteDatabase.OPEN_READWRITE);
    dbHelper = new SQLiteDatabaseHelper(InstrumentationRegistry.getTargetContext());

    StringBuilder statement = new StringBuilder();
    // db table creation script
    // initial table creation
    for (Table table : tableBag.getAll()) {
      statement.append("CREATE TABLE " + table.getName() + " ( ");
      // table column definition
      Set<Pair<ColumnDefinition, Table.ColumnType>> tableFields = table.getFields();
      int count = 0, max = tableFields.size();
      for (Pair<ColumnDefinition, Table.ColumnType> column : tableFields) {
        ColumnDefinition columnDefinition = column.first;
        statement.append(columnDefinition.getName());
        statement.append(" ");
        statement.append(column.second); // column type
        statement.append(" ");
        statement.append(columnDefinition.isPrimaryKey() ? "PRIMARY KEY " : "");
        statement.append(columnDefinition.isAutoIncrement() ? "AUTOINCREMENT " : "");
        statement.append(columnDefinition.hasDefaultValue() ? "DEFAULT "
            + columnDefinition.getDefaultValue()
            + " " : "");
        statement.append(columnDefinition.isUnique() ? "NOT NULL UNIQUE " : "");

        // add column constrains

        count++;
        if (count < max) {
          statement.append(", ");
        }
      }
      // end of table definition
      statement.append(" ); ");

      db.beginTransaction();
      try {
        db.execSQL(statement.toString());
        db.setTransactionSuccessful();
        statement.delete(0, statement.length());
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        db.endTransaction();
      }
    }
  }

  @Test public void scheduledDownloads() {
    // prepare
    final Table scheduleTable = tableBag.get(TableBag.TableName.SCHEDULED);
    final int nr_values = 5;
    final ContentValues[] insertedValues = new ContentValues[nr_values];
    final String[] md5s = new String[nr_values];
    final ScheduledAccessor accessor = AccessorFactory.getAccessorFor(Scheduled.class);

    db.beginTransaction();
    try {

      for (int i = 0; i < insertedValues.length; i++) {
        insertedValues[i] = new ContentValues();

        putValues(scheduleTable, insertedValues, i);

        md5s[i] = ScheduledTable.md5.getName() + i;

        db.insertOrThrow(scheduleTable.getName(), null, insertedValues[i]);
      }

      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }

    // trigger migration
    ManagerPreferences.setNeedsSqliteDbMigration(true);
    int newVersion = dbVersion.incrementAndGet();
    dbHelper.onUpgrade(db, newVersion - 1, newVersion);

    // main thread ID for asserting purposes
    final long mainThreadId = Looper.getMainLooper()
        .getThread()
        .getId();

    // evaluate
    for (int i = 0; i < nr_values; i++) {
      final int finalI = i;
      String currentMd5 = md5s[finalI];

      TestSubscriber<Scheduled> testSubscriber = TestSubscriber.create();
      accessor.get(currentMd5)
          .first()
          .subscribe(testSubscriber);
      testSubscriber.awaitTerminalEvent();
      testSubscriber.assertCompleted();
      Scheduled scheduled = testSubscriber.getOnNextEvents()
          .get(0);

      if (scheduled == null) {
        fail("Scheduled download is null");
        return;
      }

      assertNotEquals(mainThreadId, Thread.currentThread()
          .getId());

      // assert data fields
      assertEquals(scheduled.getName(), insertedValues[finalI].get(ScheduledTable.name.getName()));

      assertEquals(scheduled.getMd5(), insertedValues[finalI].get(ScheduledTable.md5.getName()));

      assertEquals(scheduled.getIcon(), insertedValues[finalI].get(ScheduledTable.icon.getName()));
    }
  }

  private void putValues(Table table, ContentValues[] insertedValues, int i) {
    String columnName;
    for (Pair<ColumnDefinition, Table.ColumnType> column : table.getFields()) {
      columnName = column.first.getName();
      if (!column.first.hasDefaultValue()) {
        switch (column.second) {
          case TEXT: {
            insertedValues[i].put(columnName, columnName + i);
            break;
          }
          case INTEGER: {
            insertedValues[i].put(columnName, i);
            break;
          }
          default: {
            throw new UnsupportedOperationException("Unknown type to insert in DB");
          }
        }
      }
    }
  }

  @Test public void excluded() {
    // prepare
    final Table installedTable = tableBag.get(TableBag.TableName.EXCLUDED);
    final int nr_values = 2;
    final ContentValues[] insertedValues = new ContentValues[nr_values];
    final String[] packageNames = new String[nr_values];
    final UpdateAccessor accessor = AccessorFactory.getAccessorFor(Update.class);

    PackageManager pm = AptoideUtils.getContext()
        .getPackageManager();
    List<PackageInfo> packageInfoList = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);

    db.beginTransaction();
    try {

      for (int i = 0; i < insertedValues.length; i++) {
        insertedValues[i] = new ContentValues();

        PackageInfo packageInfo = packageInfoList.get(i);
        packageNames[i] = packageInfo.packageName;
        insertedValues[i].put(ExcludedTable.package_name.getName(), packageNames[i]);
        insertedValues[i].put(ExcludedTable.name.getName(), packageInfo.versionName);
        insertedValues[i].put(ExcludedTable.vercode.getName(), packageInfo.versionCode);
        insertedValues[i].put(ExcludedTable.version_name.getName(), packageInfo.versionName);
        insertedValues[i].put(ExcludedTable.iconpath.getName(), "app-icon");

        db.insertOrThrow(installedTable.getName(), null, insertedValues[i]);
      }

      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }

    // trigger migration
    ManagerPreferences.setNeedsSqliteDbMigration(true);
    int newVersion = dbVersion.incrementAndGet();
    dbHelper.onUpgrade(db, newVersion - 1, newVersion);

    // main thread ID for asserting purposes
    final long mainThreadId = Looper.getMainLooper()
        .getThread()
        .getId();

    // evaluate
    for (int i = 0; i < nr_values; i++) {
      final int finalI = i;
      String currentPackageName = packageNames[finalI];

      TestSubscriber<Update> testSubscriber = TestSubscriber.create();
      accessor.get(currentPackageName)
          .first()
          .subscribe(testSubscriber);
      testSubscriber.awaitTerminalEvent();
      testSubscriber.assertCompleted();
      Update excluded = testSubscriber.getOnNextEvents()
          .get(0);

      if (excluded == null) {
        fail("Installed download is null");
        return;
      }

      assertNotEquals(mainThreadId, Thread.currentThread()
          .getId());

      // assert data fields
      assertEquals(excluded.getPackageName(),
          insertedValues[finalI].get(ExcludedTable.package_name.getName()));

      assertEquals(excluded.getLabel(), insertedValues[finalI].get(ExcludedTable.name.getName()));

      assertEquals(excluded.getVersionCode(),
          insertedValues[finalI].get(ExcludedTable.vercode.getName()));

      assertEquals(excluded.getUpdateVersionName(),
          insertedValues[finalI].get(ExcludedTable.version_name.getName()));

      assertEquals(excluded.getIcon(),
          insertedValues[finalI].get(ExcludedTable.iconpath.getName()));
    }
  }
}
