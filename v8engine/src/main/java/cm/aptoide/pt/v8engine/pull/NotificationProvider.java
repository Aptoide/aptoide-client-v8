package cm.aptoide.pt.v8engine.pull;

import android.support.annotation.NonNull;
import cm.aptoide.pt.database.accessors.NotificationAccessor;
import cm.aptoide.pt.database.realm.Notification;
import io.realm.Sort;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

/**
 * Created by trinkes on 09/05/2017.
 */

public class NotificationProvider {
  private NotificationAccessor notificationAccessor;

  public NotificationProvider(NotificationAccessor notificationAccessor) {

    this.notificationAccessor = notificationAccessor;
  }

  Completable save(AptoideNotification aptoideNotification) {
    return Completable.fromAction(
        () -> notificationAccessor.insert(convertToNotification(aptoideNotification)));
  }

  @NonNull private Notification convertToNotification(AptoideNotification aptoideNotification) {
    return new Notification(aptoideNotification.getAbTestingGroup(), aptoideNotification.getBody(),
        aptoideNotification.getCampaignId(), aptoideNotification.getImg(),
        aptoideNotification.getLang(), aptoideNotification.getTitle(), aptoideNotification.getUrl(),
        aptoideNotification.getUrlTrack(), aptoideNotification.getTimeStamp(),
        aptoideNotification.getType(), aptoideNotification.isShowed());
  }

  Single<List<AptoideNotification>> getNotifications(Integer[] notificationsIds) {
    return notificationAccessor.getAllSorted(Sort.DESCENDING, notificationsIds)
        .first()
        .flatMap(notifications -> Observable.from(notifications)
            .map(notification -> convertToAptoideNotification(notification))
            .toList())
        .toSingle();
  }

  private AptoideNotification convertToAptoideNotification(Notification notification) {
    return new AptoideNotification(notification.getAbTestingGroup(), notification.getBody(),
        notification.getCampaignId(), notification.getImg(), notification.getLang(),
        notification.getTitle(), notification.getUrl(), notification.getUrlTrack(),
        notification.getTimeStamp(), notification.getType(),
        notification.isShowed());
  }

  public Completable save(List<AptoideNotification> aptideNotifications) {
    return Observable.from(aptideNotifications)
        .map(aptoideNotification -> convertToNotification(aptoideNotification))
        .toList()
        .doOnNext(notifications -> notificationAccessor.insertAll(notifications))
        .toCompletable();
  }
}