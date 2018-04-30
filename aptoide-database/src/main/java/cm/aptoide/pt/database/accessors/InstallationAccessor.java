package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.realm.Installation;
import java.util.List;
import rx.Observable;

public class InstallationAccessor extends SimpleAccessor<Installation> {

  public InstallationAccessor(Database db) {
    super(db, Installation.class);
  }

  public Observable<List<Installation>> getInstallationsHistory() {
    return database.getAll(Installation.class);
  }
}
