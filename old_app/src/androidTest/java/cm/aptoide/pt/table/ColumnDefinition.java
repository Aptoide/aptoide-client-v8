package cm.aptoide.pt.table;

/**
 * Created on 14/10/2016.
 */

public class ColumnDefinition<T> {

  private final boolean isPrimaryKey;
  private final boolean autoIncrement;
  private final String name;
  private final boolean isUnique;
  private final OnConflictStrategy onConflictStrategy;

  private T defaultValue;

  ColumnDefinition(String name) {
    this(false, false, name, false, OnConflictStrategy.NONE);
  }

  private ColumnDefinition(boolean isPrimaryKey, boolean autoIncrement, String name,
      boolean isUnique, OnConflictStrategy onConflictStrategy) {
    this.isPrimaryKey = isPrimaryKey;
    this.autoIncrement = autoIncrement;
    this.name = name;
    this.isUnique = isUnique;
    this.onConflictStrategy = onConflictStrategy;
  }

  ColumnDefinition(String name, boolean isPrimaryKey) {
    this(isPrimaryKey, false, name, false, OnConflictStrategy.NONE);
  }

  ColumnDefinition(String name, boolean isPrimaryKey, boolean autoIncrement) {
    this(isPrimaryKey, autoIncrement, name, false, OnConflictStrategy.NONE);
  }

  ColumnDefinition(String name, boolean isUnique, OnConflictStrategy onConflictStrategy) {
    this(false, false, name, isUnique, OnConflictStrategy.NONE);
  }

  public ColumnDefinition withDefaultValue(T defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  public boolean isPrimaryKey() {
    return isPrimaryKey;
  }

  public boolean isAutoIncrement() {
    return autoIncrement;
  }

  public boolean isUnique() {
    return isUnique;
  }

  public OnConflictStrategy getOnConflictStrategy() {
    return onConflictStrategy;
  }

  public boolean hasDefaultValue() {
    return defaultValue != null;
  }

  public T getDefaultValue() {
    return defaultValue;
  }

  @Override public String toString() {
    return getName();
  }

  public String getName() {
    return name;
  }
}
