package cm.aptoide.pt.table;

import android.util.Pair;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by sithengineer on 13/10/2016.
 */

public class InstalledTable implements Table {

  public static final ColumnDefinition id_installed =
      new ColumnDefinition<Integer>("id_installed", true, true);

  public static final ColumnDefinition package_name =
      new ColumnDefinition<String>("package_name", true, OnConflictStrategy.REPLACE);

  public static final ColumnDefinition name = new ColumnDefinition<String>("name");

  public static final ColumnDefinition version_code =
      new ColumnDefinition<Integer>("version_code").withDefaultValue(0);

  public static final ColumnDefinition version_name =
      new ColumnDefinition<String>("version_name").withDefaultValue("''");

  public static final ColumnDefinition signature =
      new ColumnDefinition<String>("signature").withDefaultValue("''");

  @Override public String getName() {
    return "installed";
  }

  @Override public Set<Pair<ColumnDefinition, ColumnType>> getFields() {
    Set<Pair<ColumnDefinition, ColumnType>> fields = new HashSet<>();
    fields.add(Pair.create(id_installed, ColumnType.INTEGER));
    fields.add(Pair.create(package_name, ColumnType.TEXT));
    fields.add(Pair.create(name, ColumnType.TEXT));
    fields.add(Pair.create(version_code, ColumnType.INTEGER));
    fields.add(Pair.create(version_name, ColumnType.TEXT));
    fields.add(Pair.create(signature, ColumnType.TEXT));
    return fields;
  }
}
