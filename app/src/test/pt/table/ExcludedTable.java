package cm.aptoide.pt.table;

import android.util.Pair;
import java.util.HashSet;
import java.util.Set;

/**
 * Created on 13/10/2016.
 */

/**
 * This table uses not the installed but the updates table since it is done like this in V7
 */
public class ExcludedTable implements Table {

  public static final ColumnDefinition package_name = new ColumnDefinition<String>("package_name");

  public static final ColumnDefinition name = new ColumnDefinition<String>("name");

  public static final ColumnDefinition iconpath = new ColumnDefinition<String>("iconpath");

  public static final ColumnDefinition vercode = new ColumnDefinition<Integer>("vercode");

  public static final ColumnDefinition version_name = new ColumnDefinition<String>("version_name");

  @Override public String getName() {
    return "excluded";
  }

  @Override public Set<Pair<ColumnDefinition, ColumnType>> getFields() {
    Set<Pair<ColumnDefinition, ColumnType>> fields = new HashSet<>();
    fields.add(Pair.create(package_name, ColumnType.TEXT));
    fields.add(Pair.create(name, ColumnType.TEXT));
    fields.add(Pair.create(iconpath, ColumnType.TEXT));
    fields.add(Pair.create(vercode, ColumnType.INTEGER));
    fields.add(Pair.create(version_name, ColumnType.TEXT));
    return fields;
  }
}
