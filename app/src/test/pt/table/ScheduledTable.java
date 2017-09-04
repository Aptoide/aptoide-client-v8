package cm.aptoide.pt.table;

import android.util.Pair;
import java.util.HashSet;
import java.util.Set;

/**
 * Created on 13/10/2016.
 */

public class ScheduledTable implements Table {

  public static final ColumnDefinition package_name = new ColumnDefinition("package_name");
  public static final ColumnDefinition name = new ColumnDefinition("name");
  public static final ColumnDefinition version_name = new ColumnDefinition("version_name");
  public static final ColumnDefinition md5 = new ColumnDefinition("md5");
  public static final ColumnDefinition repo_name = new ColumnDefinition("repo_name");
  public static final ColumnDefinition icon = new ColumnDefinition("icon");

  @Override public String getName() {
    return "scheduled";
  }

  @Override public Set<Pair<ColumnDefinition, ColumnType>> getFields() {
    Set<Pair<ColumnDefinition, ColumnType>> fields = new HashSet<>();
    fields.add(Pair.create(package_name, ColumnType.TEXT));
    fields.add(Pair.create(name, ColumnType.TEXT));
    fields.add(Pair.create(version_name, ColumnType.TEXT));
    fields.add(Pair.create(md5, ColumnType.TEXT));
    fields.add(Pair.create(repo_name, ColumnType.TEXT));
    fields.add(Pair.create(icon, ColumnType.TEXT));
    return fields;
  }
}
