package cm.aptoide.pt.database.accessors;

import io.realm.RealmObject;
import java.util.List;
import rx.Observable;

@Deprecated public class SimpleAccessor<T extends RealmObject> implements Accessor<T> {

  protected final Database database;
  private final Class clazz;

  public SimpleAccessor(Database db, Class<T> clazz) {
    this.database = db;
    this.clazz = clazz;
  }

  @Override public void insertAll(List<T> objects) {
    database.insertAll(objects);
  }

  @Override public void removeAll() {
    if (clazz != null) {
      database.deleteAll(clazz);
    }
  }

  public void insert(T object) {
    database.insert(object);
  }

  public Observable<Long> count() {
    return database.count(clazz);
  }
}
