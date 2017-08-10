package cm.aptoide.pt.table;

import java.util.HashMap;

/**
 * Created on 14/10/2016.
 */

public class TableBag {

  private HashMap<TableName, Table> tables;

  public TableBag() {
    tables = new HashMap<>();
    init();
  }

  private void init() {
    tables.put(TableName.SCHEDULED, new ScheduledTable());
    tables.put(TableName.EXCLUDED, new ExcludedTable());
  }

  public Table get(TableName tableName) {
    return tables.get(tableName);
  }

  public Iterable<Table> getAll() {
    return tables.values();
  }

  public enum TableName {
    SCHEDULED, EXCLUDED
  }
}
