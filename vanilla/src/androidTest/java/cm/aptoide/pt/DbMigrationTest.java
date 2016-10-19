/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 12/04/2016.
 */

package cm.aptoide.pt;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Pair;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.accessors.ScheduledAccessor;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.table.ColumnDefinition;
import cm.aptoide.pt.table.InstalledTable;
import cm.aptoide.pt.table.ScheduledTable;
import cm.aptoide.pt.table.Table;
import cm.aptoide.pt.table.TableBag;
import cm.aptoide.pt.v8engine.deprecated.SQLiteDatabaseHelper;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static cm.aptoide.pt.table.Table.ColumnType.TEXT;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class) @LargeTest public class DbMigrationTest {

  private static TableBag tableBag;

  private SQLiteDatabase db;
  private SQLiteDatabaseHelper dbHelper;

  @BeforeClass public static void classSetup() {
    tableBag = new TableBag();
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
            + columnDefinition.getDefaultValue() + " " :
            "");
        statement.append(columnDefinition.isUnique() ? "NOT NULL UNIQUE " : "");

        // add column constrains

        count++;
        if (count < max) {
          statement.append(", ");
        }
      }
      // end of table definition
      statement.append(" ); ");
    }

    db.beginTransaction();
    try {
      db.execSQL(statement.toString());
      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }

    //db.beginTransaction();
    //try{
    //  db.execSQL(convertStreamToString(getClass().getResourceAsStream("create_scheduled.txt")));
    //  db.setTransactionSuccessful();
    //}catch (Exception e) {
    //  e.printStackTrace();
    //}finally {
    //  db.endTransaction();
    //}
    //
    //db.beginTransaction();
    //try{
    //  db.execSQL(convertStreamToString(getClass().getResourceAsStream("create_installed.sql")));
    //  db.setTransactionSuccessful();
    //}catch (Exception e) {
    //  e.printStackTrace();
    //}finally {
    //  db.endTransaction();
    //}

  }

  //@Test
  public void scheduledDownloads() {
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
    dbHelper.onUpgrade(db, 0, 0);

    // main thread ID for asserting purposes
    final long mainThreadId = Looper.getMainLooper().getThread().getId();

    // latch to block current thread while the DB requests are made
    final CountDownLatch countDownLatch = new CountDownLatch(nr_values);

    // evaluate
    for (int i = 0; i < nr_values; i++) {
      final int finalI = i;
      String currentMd5 = md5s[finalI];
      accessor.get(currentMd5).subscribe(scheduled -> {

        if (scheduled == null) {
          Assert.fail("Scheduled download is null");
          return;
        }

        Assert.assertNotEquals(mainThreadId, Thread.currentThread().getId());

        // assert data fields
        assertEquals(scheduled.getName(),
            insertedValues[finalI].get(ScheduledTable.name.getName()));

        assertEquals(scheduled.getMd5(), insertedValues[finalI].get(ScheduledTable.md5.getName()));

        assertEquals(scheduled.getIcon(),
            insertedValues[finalI].get(ScheduledTable.icon.getName()));

        countDownLatch.countDown();
      }, err -> {
        Assert.fail(err.getMessage());
      });
    }

    try {
      countDownLatch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
      Assert.fail("Unable to block this thread used in this test while async requests are made.");
    }
  }

  @Test public void installed() {
    // prepare
    final Table installedTable = tableBag.get(TableBag.TableName.INSTALLED);
    final int nr_values = 5;
    final ContentValues[] insertedValues = new ContentValues[nr_values];
    final String[] packageNames = new String[nr_values];
    final InstalledAccessor accessor = AccessorFactory.getAccessorFor(Installed.class);

    db.beginTransaction();
    try {

      for (int i = 0; i < insertedValues.length; i++) {
        insertedValues[i] = new ContentValues();

        putValues(installedTable, insertedValues, i);

        packageNames[i] = cm.aptoide.pt.table.InstalledTable.package_name.getName() + i;

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
    dbHelper.onUpgrade(db, 0, 0);

    // main thread ID for asserting purposes
    final long mainThreadId = Looper.getMainLooper().getThread().getId();

    // latch to block current thread while the DB requests are made
    final CountDownLatch countDownLatch = new CountDownLatch(nr_values);

    // evaluate
    for (int i = 0; i < nr_values; i++) {
      final int finalI = i;
      String currentPackageName = packageNames[finalI];
      accessor.get(currentPackageName).subscribe(installed -> {

        if (installed == null) {
          Assert.fail("Installed download is null");
          return;
        }

        Assert.assertNotEquals(mainThreadId, Thread.currentThread().getId());

        assertEquals(installed.getPackageName(),
            insertedValues[finalI].get(InstalledTable.package_name.getName()));

        // assert data fields
        assertEquals(installed.getName(),
            insertedValues[finalI].get(InstalledTable.name.getName()));

        assertEquals(installed.getSignature(),
            insertedValues[finalI].get(InstalledTable.signature.getName()));

        countDownLatch.countDown();
      }, err -> {
        Assert.fail(err.getMessage());
      });
    }

    try {
      countDownLatch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
      Assert.fail("Unable to block this thread used in this test while async requests are made.");
    }
  }

  private void putValues(Table table, ContentValues[] insertedValues, int i) {
    String columnName;
    for (Pair<ColumnDefinition, Table.ColumnType> column : table.getFields()) {
      columnName = column.first.getName();
      if(!column.first.hasDefaultValue()) {
        switch (column.second){
          case TEXT: {
            insertedValues[i].put(columnName, columnName + i);
            break;
          }
          case INTEGER: {
            insertedValues[i].put(columnName, i);
            break;
          }
          default:{
            throw new UnsupportedOperationException("Unknown type to insert in DB");
          }
        }
      }
    }
  }
}
