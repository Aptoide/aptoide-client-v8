/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt.database.accessors;

import io.realm.RealmObject;
import java.util.List;

/**
 * Created on 02/09/16.
 */
public interface Accessor<T extends RealmObject> {
  void insertAll(List<T> objects);

  void removeAll();
}
