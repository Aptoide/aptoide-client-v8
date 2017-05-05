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
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.v8engine.R;
import com.bumptech.glide.request.target.NotificationTarget;
import io.realm.Sort;
import java.util.List;
import rx.Completable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Created by trinkes on 03/05/2017.
 */

public class NotificationShower {

  public static final int NOTIFICATION_LIMIT_IN_PERIOD = 3;
  public static final long TIME_FRAME_TO_CONSIDER_TO_SHOW_NOTIFICATION =
      MILLISECONDS.convert(1, HOURS);
  private static final String TAG = NotificationShower.class.getSimpleName();

  private NotificationAccessor notificationAccessor;
  private NotificationManager managerNotification;

  NotificationShower(NotificationAccessor notificationAccessor,
      NotificationManager managerNotification) {
    this.notificationAccessor = notificationAccessor;
    this.managerNotification = managerNotification;
  }

  public Completable showNotification(Context context, @NonNull Notification notification,
      int notificationId) {
    return shouldShowNotification(notification).flatMapCompletable(shouldShowNotification -> {
      if (shouldShowNotification) {
        return showNotification(context, notificationId, notification.getTitle(),
            notification.getBody(), notification.getImg(), notification.getUrlTrack(),
            notification.getUrl()).andThen(setNotificationAsViewed(notification));
      } else {
        return Completable.complete();
      }
    });
  }

  private Single<Boolean> shouldShowNotification(Notification notificationToShow) {
    // TODO: 04/05/2017 trinkes consider when notification was not dismissed(should be replaced by the new one)
    // TODO: 04/05/2017 trinkes one day missing

    switch (notificationToShow.getType()) {
      case Notification.CAMPAIGN:
        return Single.just(true);
      case Notification.COMMENT:
      case Notification.LIKE:
        return shouldShowSocial(NOTIFICATION_LIMIT_IN_PERIOD,
            TIME_FRAME_TO_CONSIDER_TO_SHOW_NOTIFICATION);
      case Notification.POPULAR:
        return shouldShowPopular(NOTIFICATION_LIMIT_IN_PERIOD,
            TIME_FRAME_TO_CONSIDER_TO_SHOW_NOTIFICATION);
      default:
        return Single.just(false);
    }
  }

  private Single<Boolean> shouldShowSocial(int occurrencesLimit, long timeFrame) {
    return notificationAccessor.getAllSorted(Sort.DESCENDING, new int[] {
        Notification.COMMENT, Notification.LIKE
    })
        .first()
        .observeOn(Schedulers.computation())
        .map(notifications -> isDisplayedNotificationsLimit(notifications, occurrencesLimit,
            timeFrame))
        .toSingle();
  }

  private Single<Boolean> shouldShowPopular(int occurrencesLimit, long timeFrame) {
    return notificationAccessor.getAllSorted(Sort.DESCENDING, Notification.POPULAR)
        .first()
        .observeOn(Schedulers.computation())
        .map(notifications -> isDisplayedNotificationsLimit(notifications, occurrencesLimit,
            timeFrame))
        .toSingle();
  }

  @NonNull private Boolean isDisplayedNotificationsLimit(List<Notification> notifications,
      int occurrencesLimit, long timeFrame) {
    int occurrences = 0;
    for (int i = 0; i < notifications.size() && occurrences < occurrencesLimit; i++) {
      Notification notification = notifications.get(i);
      if (notification.isShowed()
          && notification.getTimeStamp() > System.currentTimeMillis() - timeFrame) {
        occurrences++;
      }
    }
    return occurrences < NOTIFICATION_LIMIT_IN_PERIOD;
  }

  private Completable showNotification(Context context, int notificationId, String title,
      String body, String imageUrl, String trackUrl, String url) {

    return getPressIntentAction(trackUrl, url, notificationId).flatMap(
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
          new NotificationCompat.Builder(Application.getContext()).setContentIntent(
              pressIntentAction)
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
      RemoteViews expandedView = new RemoteViews(Application.getContext().getPackageName(),
          R.layout.pushnotificationlayout);
      expandedView.setImageViewBitmap(R.id.icon, notification.largeIcon);
      expandedView.setTextViewText(R.id.title, title);
      expandedView.setTextViewText(R.id.description, body);
      notification.bigContentView = expandedView;

      NotificationTarget notificationTarget =
          new NotificationTarget(Application.getContext(), expandedView,
              R.id.PushNotificationImageView, notification, notificationId);
      ImageLoader.with(context).loadImageToNotification(notificationTarget, imageUrl);
    }
    return notification;
  }

  private Single<PendingIntent> getPressIntentAction(String trackUrl, String url,
      int notificationId) {
    return Single.fromCallable(() -> {
      Intent resultIntent = new Intent(Application.getContext(), PullingContentReceiver.class);
      resultIntent.setAction(PullingContentReceiver.NOTIFICATION_PRESSED_ACTION);

      if (!TextUtils.isEmpty(trackUrl)) {
        resultIntent.putExtra(PullingContentReceiver.PUSH_NOTIFICATION_TRACK_URL, trackUrl);
      }
      if (!TextUtils.isEmpty(url)) {
        resultIntent.putExtra(PullingContentReceiver.PUSH_NOTIFICATION_TARGET_URL, url);
      }

      return PendingIntent.getBroadcast(Application.getContext(), notificationId, resultIntent,
          PendingIntent.FLAG_UPDATE_CURRENT);
    }).subscribeOn(Schedulers.computation());
  }

  private Completable setNotificationAsViewed(Notification notification) {
    return Completable.fromAction(() -> {
      notification.setShowed(true);
      notificationAccessor.insert(notification);
    });
  }
}
