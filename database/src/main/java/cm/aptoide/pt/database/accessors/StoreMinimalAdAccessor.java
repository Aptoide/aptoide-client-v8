package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.realm.StoredMinimalAd;
import rx.Observable;

/**
 * Created on 11/10/2016.
 */

public class StoreMinimalAdAccessor extends SimpleAccessor<StoredMinimalAd> {

  StoreMinimalAdAccessor(Database db) {
    super(db, StoredMinimalAd.class);
  }

  public Observable<StoredMinimalAd> get(String packageName) {
    return database.get(StoredMinimalAd.class, StoredMinimalAd.PACKAGE_NAME, packageName);
  }

  public void remove(StoredMinimalAd storeMinimalAd) {
    database.delete(StoredMinimalAd.class, StoredMinimalAd.PACKAGE_NAME,
        storeMinimalAd.getPackageName());
  }
}
