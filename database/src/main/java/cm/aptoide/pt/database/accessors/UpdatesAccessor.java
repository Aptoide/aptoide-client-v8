package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.database.schedulers.RealmSchedulers;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 9/2/16.
 */
public class UpdatesAccessor implements Accessor {

  private final Database database;

  public UpdatesAccessor(Database db) {
    this.database = db;
  }

  public Observable<List<Update>> getAll() {
    return database.getAll(Update.class);
  }

  public Observable<List<Update>> getUpdates() {
    return Observable.fromCallable(() -> Database.get())
        .flatMap(realm -> realm.where(Update.class)
            .equalTo(Update.EXCLUDED, false)
            .findAll()
            .asObservable())
        .unsubscribeOn(RealmSchedulers.getScheduler())
        .flatMap((data) -> database.copyFromRealm(data))
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());
  }
}
