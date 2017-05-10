package cm.aptoide.pt.v8engine.pull;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import com.bumptech.glide.request.target.NotificationTarget;
import rx.Completable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 09/05/2017.
 */

public class SystemNotificationShower {
  private static final String TAG = SystemNotificationShower.class.getSimpleName();
  private Context context;
  private NotificationManager notificationManager;

  public SystemNotificationShower(Context context, NotificationManager notificationManager) {
    this.context = context;
    this.notificationManager = notificationManager;
  }

  public Completable showNotification(AptoideNotification aptoideNotification, int notificationId) {
    return mapToAndroidNotification(aptoideNotification, notificationId).doOnSuccess(
        notification -> notificationManager.notify(notificationId, notification)).toCompletable();
  }

  private Single<Notification> mapToAndroidNotification(AptoideNotification aptoideNotification,
      int notificationId) {
    return getPressIntentAction(aptoideNotification.getUrlTrack(), aptoideNotification.getUrl(),
        aptoideNotification.getType(), context).flatMap(
        pressIntentAction -> buildNotification(context, aptoideNotification.getTitle(),
            aptoideNotification.getBody(), aptoideNotification.getImg(), pressIntentAction,
            notificationId));
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
}
