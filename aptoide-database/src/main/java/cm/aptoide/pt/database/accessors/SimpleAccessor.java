package cm.aptoide.pt.database.accessors;

import io.realm.RealmObject;
import java.lang.reflect.Type;
import java.util.List;
import rx.Observable;
import rx.Scheduler;

/**
 * Created on 07/10/16.
 */

public abstract class SimpleAccessor<T extends RealmObject> implements Accessor<T> {

  final Database database;
  final Scheduler observingScheduler;
  private final Class clazz;

  SimpleAccessor(Database db, Scheduler observingScheduler) {
    this.database = db;
    this.observingScheduler = observingScheduler;

    Type[] types = this.getClass()
        .getGenericInterfaces();
    if (types != null && types.length > 0) {
      clazz = types[0].getClass();
    } else {
      clazz = null;
    }
  }

  SimpleAccessor(Database db, Scheduler observingScheduler, Class<T> clazz) {
    this.database = db;
    this.observingScheduler = observingScheduler;
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
