package cm.aptoide.pt.v8engine.notification;

import cm.aptoide.pt.database.accessors.NotificationAccessor;
import io.realm.Sort;
import java.util.ArrayList;
import java.util.List;
import rx.Completable;
import rx.Observable;

/**
 * Created by trinkes on 29/05/2017.
 */

public class NotificationsCleaner {

  private NotificationAccessor notificationAccessor;

  public NotificationsCleaner(NotificationAccessor notificationAccessor) {
    this.notificationAccessor = notificationAccessor;
  }

  public Completable cleanOtherUsersNotifications(String id) {
    List<String> idsList = new ArrayList<>(2);
    idsList.add(id);
    //where there is no login the account and the id is "" and those notifications should't be removed
    idsList.add("");
    return notificationAccessor.deleteAllExcluding(idsList);
  }

  public Completable cleanLimitExceededNotifications(int limit) {
    return notificationAccessor.getAllSorted(Sort.DESCENDING)
        .first()
        .flatMap(notifications -> {
          if (notifications.size() > limit) {
            return Observable.from(notifications.subList(limit, notifications.size()))
                .map(notification -> notification.getKey())
                .toList()
                .flatMapCompletable(
                    keys -> notificationAccessor.delete(keys.toArray(new String[keys.size()])));
          } else {
            return Observable.empty();
          }
        })
        .toCompletable();
  }
}
