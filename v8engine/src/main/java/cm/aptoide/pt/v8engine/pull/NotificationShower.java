package cm.aptoide.pt.v8engine.pull;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;
import cm.aptoide.pt.database.accessors.NotificationAccessor;
import cm.aptoide.pt.database.realm.Notification;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import com.bumptech.glide.request.target.NotificationTarget;
import io.realm.Sort;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 03/05/2017.
 */

class NotificationShower {
  private static final String TAG = NotificationShower.class.getSimpleName();

  private NotificationAccessor notificationAccessor;
  private NotificationManager managerNotification;
  private Integer[] commentLikeNotificationIds;
  private Integer[] popularNotificationIds;

  NotificationShower(NotificationAccessor notificationAccessor,
      NotificationManager managerNotification) {
    this.notificationAccessor = notificationAccessor;
    this.managerNotification = managerNotification;
    commentLikeNotificationIds = new Integer[] { Notification.COMMENT, Notification.LIKE };
    popularNotificationIds = new Integer[] { Notification.POPULAR };
  }

  Completable showNotification(Context context, @NonNull Notification notification) {
    return shouldShowNotification(notification, context).flatMapCompletable(
        shouldShowNotification -> {
          if (shouldShowNotification) {
            return showNotification(context, getNotificationId(notification, context),
                notification.getTitle(), notification.getBody(), notification.getImg(),
                notification.getUrlTrack(), notification.getUrl()).andThen(
                setNotificationAsViewed(notification));
          } else {
            return Completable.complete();
          }
        });
  }

  private Single<Boolean> shouldShowNotification(Notification notificationToShow, Context context) {
    switch (notificationToShow.getType()) {
      case Notification.CAMPAIGN:
        return Single.just(true);
      case Notification.COMMENT:
      case Notification.LIKE:
        return shouldShowSocialNotification(context, commentLikeNotificationIds);
      case Notification.POPULAR:
        return shouldShowSocialNotification(context, popularNotificationIds);
      default:
        return Single.just(false);
    }
  }

  private Single<Boolean> shouldShowSocialNotification(Context context,
      Integer[] notificationsIds) {
    if (Build.VERSION.SDK_INT < 18 || getActiveNotificationId(notificationsIds, context) != -1) {
      return Single.just(true);
    } else {
      return notificationAccessor.getAllSorted(Sort.DESCENDING, notificationsIds)
          .first()
          .observeOn(Schedulers.computation())
          .map(notifications -> applySocialPolicies(notifications))
          .toSingle();
    }
  }

  private boolean applySocialPolicies(List<Notification> notifications) {
    return !isShowedLimitReached(notifications, 1, TimeUnit.HOURS.toMillis(1))
        && !isShowedLimitReached(notifications, 3, TimeUnit.DAYS.toMillis(1));
  }

  /**
   * @param notificationIds the ids of notifications that should be considered
   *
   * @return the id of the notification that is currently being displayed or -1 if there is no
   * notification being displayed
   */
  private int getActiveNotificationId(Integer[] notificationIds, Context context) {
    for (final int type : notificationIds) {
      if (isNotificationActive(context, type)) {
        return type;
      }
    }
    return -1;
  }

  @NonNull
  private Boolean isShowedLimitReached(List<Notification> notifications, int occurrencesLimit,
      long timeFrame) {
    int occurrences = 0;
    for (int i = 0; i < notifications.size() && occurrences < occurrencesLimit; i++) {
      Notification notification = notifications.get(i);
      if (notification.getTimeStamp() < System.currentTimeMillis() - timeFrame) {
        break;
      }
      if (notification.isShowed()
          && notification.getTimeStamp() > System.currentTimeMillis() - timeFrame) {
        occurrences++;
      }
    }
    return occurrences >= occurrencesLimit;
  }

  private Completable showNotification(Context context, int notificationId, String title,
      String body, String imageUrl, String trackUrl, String url) {

    return getPressIntentAction(trackUrl, url, notificationId, context).flatMap(
        pressIntentAction -> buildNotification(context, title, body, imageUrl, pressIntentAction,
            notificationId))
        .toObservable()
        .subscribeOn(AndroidSchedulers.mainThread())
        .doOnNext(notification -> managerNotification.notify(notificationId, notification))
        .toCompletable();
  }

  @NonNull private Single<android.app.Notification> buildNotification(Context context, String title,
      String body, String imageUrl, PendingIntent pressIntentAction, int notificationId) {
    return Single.fromCallable(() -> {
      android.app.Notification notification =
          new NotificationCompat.Builder(context).setContentIntent(pressIntentAction)
              .setOngoing(false)
              .setSmallIcon(R.drawable.ic_stat_aptoide_notification)
              .setLargeIcon(ImageLoader.with(context).loadBitmap(imageUrl))
              .setContentTitle(title)
              .setContentText(body)
              .build();
      notification.flags =
          android.app.Notification.DEFAULT_LIGHTS | android.app.Notification.FLAG_AUTO_CANCEL;
      return notification;
    })
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .map(notification -> setExpandedView(context, title, body, imageUrl, notificationId,
            notification));
  }

  private android.app.Notification setExpandedView(Context context, String title, String body,
      String imageUrl, int notificationId, android.app.Notification notification) {

    if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 24 && !TextUtils.isEmpty(imageUrl)) {
      RemoteViews expandedView =
          new RemoteViews(context.getPackageName(), R.layout.pushnotificationlayout);
      expandedView.setImageViewBitmap(R.id.icon, notification.largeIcon);
      expandedView.setTextViewText(R.id.title, title);
      expandedView.setTextViewText(R.id.description, body);
      notification.bigContentView = expandedView;

      NotificationTarget notificationTarget =
          new NotificationTarget(context, expandedView, R.id.PushNotificationImageView,
              notification, notificationId);
      ImageLoader.with(context).loadImageToNotification(notificationTarget, imageUrl);
    }
    return notification;
  }

  private Single<PendingIntent> getPressIntentAction(String trackUrl, String url,
      int notificationId, Context context) {
    return Single.fromCallable(() -> {
      Intent resultIntent = new Intent(context, PullingContentReceiver.class);
      resultIntent.setAction(PullingContentReceiver.NOTIFICATION_PRESSED_ACTION);

      if (!TextUtils.isEmpty(trackUrl)) {
        resultIntent.putExtra(PullingContentReceiver.PUSH_NOTIFICATION_TRACK_URL, trackUrl);
      }
      if (!TextUtils.isEmpty(url)) {
        resultIntent.putExtra(PullingContentReceiver.PUSH_NOTIFICATION_TARGET_URL, url);
      }

      return PendingIntent.getBroadcast(context, notificationId, resultIntent,
          PendingIntent.FLAG_UPDATE_CURRENT);
    }).subscribeOn(Schedulers.computation());
  }

  private Completable setNotificationAsViewed(Notification notification) {
    return Completable.fromAction(() -> {
      notification.setShowed(true);
      notificationAccessor.insert(notification);
    });
  }

  private boolean isNotificationActive(Context context, int notificationId) {
    Intent intent = new Intent(context, PullingContentReceiver.class);
    intent.setAction(PullingContentReceiver.NOTIFICATION_PRESSED_ACTION);
    PendingIntent test =
        PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_NO_CREATE);
    return test != null;
  }

  private int getNotificationId(Notification notification, Context context) {
    switch (notification.getType()) {
      case Notification.CAMPAIGN:
        return Notification.CAMPAIGN;
      case Notification.COMMENT:
      case Notification.LIKE:
        //check if there is an active notification, if it is, return its id
        int activeNotificationId = getActiveNotificationId(commentLikeNotificationIds, context);
        return activeNotificationId == -1 ? notification.getType() : activeNotificationId;
      case Notification.POPULAR:
        return Notification.POPULAR;
      default:
        return notification.getType();
    }
  }
}
