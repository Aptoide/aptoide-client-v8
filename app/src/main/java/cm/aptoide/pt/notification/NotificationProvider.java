package cm.aptoide.pt.notification;

import androidx.annotation.NonNull;
import cm.aptoide.pt.database.RoomNotificationPersistence;
import cm.aptoide.pt.database.room.RoomNotification;
import io.realm.Sort;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.Single;

/**
 * Created by trinkes on 09/05/2017.
 */

public class NotificationProvider {

  private final RoomNotificationPersistence roomNotificationPersistence;
  private final Scheduler scheduler;

  public NotificationProvider(RoomNotificationPersistence roomNotificationPersistence,
      Scheduler scheduler) {
    this.scheduler = scheduler;
    this.roomNotificationPersistence = roomNotificationPersistence;
  }

  @NonNull private RoomNotification convertToNotification(AptoideNotification aptoideNotification) {

    return new RoomNotification(aptoideNotification.getExpire(),
        aptoideNotification.getAbTestingGroup(), aptoideNotification.getBody(),
        aptoideNotification.getCampaignId(), aptoideNotification.getImg(),
        aptoideNotification.getLang(), aptoideNotification.getTitle(), aptoideNotification.getUrl(),
        aptoideNotification.getUrlTrack(), aptoideNotification.getNotificationCenterUrlTrack(),
        aptoideNotification.getTimeStamp(), aptoideNotification.getType(),
        aptoideNotification.getDismissed(), aptoideNotification.getAppName(),
        aptoideNotification.getGraphic(), aptoideNotification.getOwnerId(),
        aptoideNotification.isProcessed(), aptoideNotification.getActionStringRes());
  }

  public Single<List<AptoideNotification>> getDismissedNotifications(
      @AptoideNotification.NotificationType Integer[] notificationsTypes, long startTime,
      long endTime) {
    return roomNotificationPersistence.getDismissed(notificationsTypes, startTime, endTime)
        .flatMap(notifications -> Observable.from(notifications)
            .map(notification -> convertToAptoideNotification(notification))
            .toList()
            .toSingle());
  }

  private AptoideNotification convertToAptoideNotification(RoomNotification notification) {
    return new AptoideNotification(notification.getBody(), notification.getImg(),
        notification.getTitle(), notification.getUrl(), notification.getType(),
        notification.getAppName(), notification.getGraphic(), notification.getDismissed(),
        notification.getOwnerId(), notification.getUrlTrack(),
        notification.getNotificationCenterUrlTrack(), notification.isProcessed(),
        notification.getTimeStamp(), notification.getExpire(), notification.getAbTestingGroup(),
        notification.getCampaignId(), notification.getLang(), notification.getActionStringRes());
  }

  public Completable save(List<AptoideNotification> aptideNotifications) {
    return Observable.from(aptideNotifications)
        .map(aptoideNotification -> convertToNotification(aptoideNotification))
        .toList()
        .flatMapCompletable(notifications -> roomNotificationPersistence.insertAll(notifications))
        .toCompletable();
  }

  public Observable<List<AptoideNotification>> getNotifications(int entries) {
    return roomNotificationPersistence.getAllSorted(Sort.DESCENDING)
        .flatMap(notifications -> Observable.from(notifications)
            .map(notification -> convertToAptoideNotification(notification))
            .take(entries)
            .toList());
  }

  public Observable<List<RoomNotification>> getNotifications() {
    return roomNotificationPersistence.getAll();
  }

  public Observable<List<AptoideNotification>> getAptoideNotifications() {
    return roomNotificationPersistence.getAll()
        .flatMap(notifications -> Observable.from(notifications)
            .map(notification -> convertToAptoideNotification(notification))
            .toList());
  }

  public Single<RoomNotification> getLastShowed(Integer[] notificationType) {
    return roomNotificationPersistence.getLastShowed(notificationType);
  }

  public Completable save(RoomNotification notification) {
    return roomNotificationPersistence.insert(notification)
        .subscribeOn(scheduler);
  }

  public Completable save(AptoideNotification notification) {
    return save(convertToNotification(notification));
  }

  public Completable deleteAllForType(int type) {
    return roomNotificationPersistence.deleteAllOfType(type);
  }
}