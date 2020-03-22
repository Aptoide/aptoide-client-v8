package cm.aptoide.pt.database;

import androidx.annotation.NonNull;
import cm.aptoide.pt.database.accessors.Accessor;
import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.realm.Store;
import io.realm.RealmObject;

/**
 * Instead of getting an accessor from here, use a Repository
 */
@Deprecated public final class AccessorFactory {

  @NonNull
  public static <T extends RealmObject, A extends Accessor> A getAccessorFor(Database database,
      Class<T> clazz) {
    if (clazz.equals(Store.class)) {
      return (A) new StoreAccessor(database);
    }

    throw new RuntimeException("Create accessor for class " + clazz.getName());
  }
}
