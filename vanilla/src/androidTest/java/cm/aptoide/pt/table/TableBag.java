package cm.aptoide.pt.table;

import java.util.Collections;
import java.util.Map;

/**
 * Created by sithengineer on 14/10/2016.
 */

public class TableBag {

  public enum TableName{
    SCHEDULED
  }

  private Map<TableName, Table> tables;

  public TableBag() {
    tables = Collections.emptyMap();
    init();
  }

  private void init() {
    tables.put(TableName.SCHEDULED, new Scheduled());
  }

  public Table get(TableName tableName) {
    return tables.get(tableName);
  }

  public Iterable<Table> getAll() {
    return tables.values();
  }
}
