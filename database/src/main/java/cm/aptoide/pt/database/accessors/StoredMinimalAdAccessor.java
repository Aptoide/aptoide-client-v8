package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.realm.StoredMinimalAd;
import java.util.List;
import rx.Observable;

/**
 * Created on 11/10/2016.
 */

public class StoredMinimalAdAccessor extends SimpleAccessor<StoredMinimalAd> {

  StoredMinimalAdAccessor(Database db) {
    super(db, StoredMinimalAd.class);
  }

  public Observable<StoredMinimalAd> get(String packageName) {
    return database.get(StoredMinimalAd.class, StoredMinimalAd.PACKAGE_NAME, packageName);
  }

  public void remove(StoredMinimalAd storeMinimalAd) {
    database.delete(StoredMinimalAd.class, StoredMinimalAd.PACKAGE_NAME,
        storeMinimalAd.getPackageName());
  }

  public void insert(StoredMinimalAd storedMinimalAd) {
    database.insert(storedMinimalAd);
  }

  public Observable<List<StoredMinimalAd>> getAll() {
    return database.getAll(StoredMinimalAd.class);
  }
}
