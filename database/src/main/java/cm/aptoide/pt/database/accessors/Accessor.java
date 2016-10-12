/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.database.accessors;

import io.realm.RealmObject;
import java.util.List;

/**
 * Created by sithengineer on 02/09/16.
 */
public interface Accessor<T extends RealmObject> {
  void insertAll(List<T> objects);
  void removeAll();
}
