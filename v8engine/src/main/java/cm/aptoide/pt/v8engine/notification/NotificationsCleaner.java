package cm.aptoide.pt.v8engine.notification;

import cm.aptoide.pt.database.accessors.NotificationAccessor;
import java.util.ArrayList;
import rx.Completable;

/**
 * Created by trinkes on 29/05/2017.
 */

public class NotificationsCleaner {

  private NotificationAccessor notificationAccessor;

  public NotificationsCleaner(NotificationAccessor notificationAccessor) {
    this.notificationAccessor = notificationAccessor;
  }

  public Completable cleanNotifications(String id) {
    ArrayList<String> idsList = new ArrayList<>(2);
    idsList.add(id);
    //where there is no login the account and the id is "" and those notifications should't be removed
    idsList.add("");
    return notificationAccessor.deleteAllExcluding(idsList);
  }
}
