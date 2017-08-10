package cm.aptoide.pt.table;

import android.util.Pair;
import java.util.Set;

/**
 * Created on 13/10/2016.
 */

public interface Table {

  /**
   * @return Table name
   */
  String getName();

  /**
   * @return a Set with columns defined by the tuple: (Field Name as {@link String}, Column type as
   * {@link ColumnType})
   */
  Set<Pair<ColumnDefinition, ColumnType>> getFields();

  enum ColumnType {
    NULL, INTEGER, REAL, TEXT, BLOB
  }
}
