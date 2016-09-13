package cm.aptoide.pt.v8engine.install;

import android.content.Context;
import cm.aptoide.pt.database.realm.Rollback;
import rx.Observable;

/**
 * Created by trinkes on 9/8/16.
 */
public interface RollbackInstallationFactory {

  Observable<Rollback> createRollback(RollbackInstallation installation, Rollback.Action action);

  Observable<Rollback> createRollback(Context context, String packageName, Rollback.Action action,
      String icon);
}
