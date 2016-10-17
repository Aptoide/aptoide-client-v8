package cm.aptoide.pt.table;

/**
 * Created by sithengineer on 14/10/2016.
 */

class SimpleColumnDefinition extends ColumnDefinition {

  private final boolean isPrimaryKey;
  private final String name;

  SimpleColumnDefinition(String name) {
    this(name, false);
  }

  SimpleColumnDefinition(String name, boolean isPrimaryKey) {
    this.isPrimaryKey = isPrimaryKey;
    this.name = name;
  }

  @Override public String getName() {
    return name;
  }

  @Override public String toString() {
    return ( isPrimaryKey ? "PRIMARY KEY" : "" ) + name;
  }
}
