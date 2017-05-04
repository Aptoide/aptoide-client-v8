package cm.aptoide.pt.v8engine.pull;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
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
    return notificationAccessor.getAllSorted(Sort.DESCENDING, notificationToShow.getType())
        .first()
        .observeOn(Schedulers.computation())
        .map(notifications -> {
          int occurrences = 0;
          for (int i = 0; i < notifications.size() && occurrences < NOTIFICATION_LIMIT_IN_PERIOD;
              i++) {
            Notification notification = notifications.get(i);
            if (notification.isShowed()
                && notification.getTimeStamp()
                > System.currentTimeMillis() - TIME_FRAME_TO_CONSIDER_TO_SHOW_NOTIFICATION) {
              occurrences++;
            }
          }
          return occurrences < NOTIFICATION_LIMIT_IN_PERIOD;
        })
        .toSingle();
  }

  private Completable showNotification(Context context, int notificationId, String title,
      String body, String imageUrl, String trackUrl, String url) {

    return Completable.fromAction(() -> {
      PendingIntent pressIntentAction = getPressIntentAction(trackUrl, url, notificationId);

      android.app.Notification notification =
          buildNotification(context, title, body, imageUrl, pressIntentAction, notificationId);

      managerNotification.notify(notificationId, notification);
    }).subscribeOn(AndroidSchedulers.mainThread());
  }

  @NonNull
  private android.app.Notification buildNotification(Context context, String title, String body,
      String imageUrl, PendingIntent pressIntentAction, int notificationId) {
    android.app.Notification notification =
        new NotificationCompat.Builder(Application.getContext()).setContentIntent(pressIntentAction)
            .setOngoing(false)
            .setSmallIcon(R.drawable.ic_stat_aptoide_notification)
            .setLargeIcon(BitmapFactory.decodeResource(Application.getContext().getResources(),
                Application.getConfiguration().getIcon()))
            .setContentTitle(title)
            .setContentText(body)
            .build();
    notification.flags =
        android.app.Notification.DEFAULT_LIGHTS | android.app.Notification.FLAG_AUTO_CANCEL;

    if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 24 && !TextUtils.isEmpty(imageUrl)) {

      RemoteViews expandedView = new RemoteViews(Application.getContext().getPackageName(),
          R.layout.pushnotificationlayout);
      expandedView.setImageViewBitmap(R.id.icon,
          BitmapFactory.decodeResource(Application.getContext().getResources(),
              Application.getConfiguration().getIcon()));
      expandedView.setTextViewText(R.id.text1, title);
      expandedView.setTextViewText(R.id.description, body);
      notification.bigContentView = expandedView;

      NotificationTarget notificationTarget =
          new NotificationTarget(Application.getContext(), expandedView,
              R.id.PushNotificationImageView, notification, notificationId);
      ImageLoader.with(context).loadImageToNotification(notificationTarget, imageUrl);
    }
    return notification;
  }

  private PendingIntent getPressIntentAction(String trackUrl, String url, int notificationId) {
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
  }

  private Completable setNotificationAsViewed(Notification notification) {
    return Completable.fromAction(() -> {
      notification.setShowed(true);
      notificationAccessor.insert(notification);
    });
  }
}
