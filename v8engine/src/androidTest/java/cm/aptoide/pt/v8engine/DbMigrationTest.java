/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 12/04/2016.
 */

package cm.aptoide.pt.v8engine;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Pair;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.ScheduledAccessor;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.v8engine.deprecated.SQLiteDatabaseHelper;
import cm.aptoide.pt.v8engine.table.ColumnDefinition;
import cm.aptoide.pt.v8engine.table.Table;
import cm.aptoide.pt.v8engine.table.TableBag;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DbMigrationTest {

  private static TableBag tableBag;

  private SQLiteDatabase db;
  private SQLiteDatabaseHelper dbHelper;

  @BeforeClass
  public static void classSetup() {
    tableBag = new TableBag();
  }

  @Before
  public void setup() {
    db = SQLiteDatabase.openDatabase(":memory:", null, SQLiteDatabase.OPEN_READWRITE);
    dbHelper = new SQLiteDatabaseHelper(InstrumentationRegistry.getTargetContext());

    StringBuilder statement = new StringBuilder();
    // db table creation script
    // initial table creation
    Table table = tableBag.get(TableBag.TableName.SCHEDULED);
    statement.append("CREATE TABLE " + table.getName() + " ( ");
    // table column definition
    for (Pair<ColumnDefinition, Table.ColumnType> column : table.getFields()) {
      statement.append(column.first); // column name
      statement.append(" ");
      statement.append(column.second); // column type
    }

    // end of table definition
    statement.append(");");

    db.beginTransaction();
    db.execSQL(statement.toString());
    db.endTransaction();
  }

  @Test
  public void scheduledDownloads () {
    // prepare
    final Table schedule = tableBag.get(TableBag.TableName.SCHEDULED);
    final int nr_values = 5;
    final ContentValues[] insertedValues = new ContentValues[nr_values];
    final String[] md5s = new String[nr_values];
    final ScheduledAccessor accessor = AccessorFactory.getAccessorFor(Scheduled.class);

    String columnName;
    for (int i = 0; i < insertedValues.length; i++) {
      insertedValues[i] = new ContentValues();
      for (Pair<ColumnDefinition, Table.ColumnType> column : schedule.getFields()) {
        columnName = column.first.toString();
        md5s[i] = columnName + i;
        insertedValues[i].put(columnName, md5s[i]);
      }
      db.insert(schedule.getName(), null, insertedValues[i]);
    }

    // trigger migration
    dbHelper.onUpgrade(db, 1, 2);

    // evaluate
    for (int i = 0; i < nr_values; i++) {
      final int finalI = i;
      accessor.get(md5s[finalI]).subscribe(scheduled -> {
        // assert data fields
        assertEquals(scheduled.getName(), insertedValues[finalI].get(
            cm.aptoide.pt.v8engine.table.Scheduled.name.getName()
          )
        );

        assertEquals(scheduled.getMd5(), insertedValues[finalI].get(
            cm.aptoide.pt.v8engine.table.Scheduled.md5.getName()
            )
        );

        assertEquals(scheduled.getIcon(), insertedValues[finalI].get(
            cm.aptoide.pt.v8engine.table.Scheduled.icon.getName()
            )
        );


      });
    }

  }
}
