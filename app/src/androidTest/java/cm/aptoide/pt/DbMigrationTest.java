/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 12/04/2016.
 */

package cm.aptoide.pt;

import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;
import androidx.test.filters.LargeTest;
import androidx.test.runner.AndroidJUnit4;
import cm.aptoide.pt.table.ColumnDefinition;
import cm.aptoide.pt.table.Table;
import cm.aptoide.pt.table.TableBag;
import java.util.Set;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class) @LargeTest public class DbMigrationTest {

  private static TableBag tableBag;

  private SQLiteDatabase db;

  @BeforeClass public static void classSetup() {
    tableBag = new TableBag();
  }

  @Before public void setup() {
    db = SQLiteDatabase.openDatabase(":memory:", null, SQLiteDatabase.OPEN_READWRITE);

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
}
