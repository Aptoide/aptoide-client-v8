package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.realm.Notification;
import java.util.List;
import rx.Observable;

/**
 * Created by trinkes on 03/05/2017.
 */

public class NotificationAccessor extends SimpleAccessor<Notification> {
  NotificationAccessor(Database db) {
    super(db, Notification.class);
  }

  public Observable<List<Notification>> getAll() {
    return database.getAll(Notification.class);
  }
}
