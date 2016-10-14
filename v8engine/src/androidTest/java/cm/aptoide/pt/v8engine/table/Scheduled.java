package cm.aptoide.pt.v8engine.table;

import android.util.Pair;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by sithengineer on 13/10/2016.
 */

public class Scheduled implements Table {

  public static final ColumnDefinition package_name = new SimpleColumnDefinition("package_name");
  public static final ColumnDefinition name = new SimpleColumnDefinition("name");
  public static final ColumnDefinition version_name = new SimpleColumnDefinition("version_name");
  public static final ColumnDefinition md5 = new SimpleColumnDefinition("md5");
  public static final ColumnDefinition repo_name = new SimpleColumnDefinition("repo_name");
  public static final ColumnDefinition icon = new SimpleColumnDefinition("icon");

  @Override public String getName() {
    return "scheduled";
  }

  @Override public Set<Pair<ColumnDefinition, ColumnType>> getFields() {
    Set<Pair<ColumnDefinition, ColumnType>> fields = Collections.emptySet();
    fields.add(Pair.create(package_name, ColumnType.TEXT));
    fields.add(Pair.create(name, ColumnType.TEXT));
    fields.add(Pair.create(version_name, ColumnType.TEXT));
    fields.add(Pair.create(md5, ColumnType.TEXT));
    fields.add(Pair.create(repo_name, ColumnType.TEXT));
    fields.add(Pair.create(icon, ColumnType.TEXT));
    return fields;
  }
}
