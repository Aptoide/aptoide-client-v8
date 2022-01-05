package cm.aptoide.pt.app.migration;

import rx.Observable;

public interface AppcMigrationPersistence {

  Observable<Boolean> isAppMigrated(String packageName);

  void insert(String packageName);
}
