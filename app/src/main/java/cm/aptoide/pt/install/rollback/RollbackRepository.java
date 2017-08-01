package cm.aptoide.pt.install.rollback;

import cm.aptoide.pt.database.accessors.RollbackAccessor;
import cm.aptoide.pt.database.realm.Rollback;
import rx.Observable;

public class RollbackRepository {

  private final RollbackAccessor accessor;

  public RollbackRepository(RollbackAccessor accessor) {
    this.accessor = accessor;
  }

  public Observable<Rollback> getNotConfirmedRollback(String packageName) {
    return accessor.getNotConfirmedRollback(packageName);
  }

  public void confirmRollback(Rollback rollback) {
    rollback.setConfirmed(true);
    save(rollback);
  }

  public void save(Rollback rollback) {
    accessor.save(rollback);
  }
}
