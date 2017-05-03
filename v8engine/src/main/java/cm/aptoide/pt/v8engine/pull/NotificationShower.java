package cm.aptoide.pt.v8engine.pull;

import android.app.Notification;
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
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.v8engine.R;
import com.bumptech.glide.request.target.NotificationTarget;
import rx.Completable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by trinkes on 03/05/2017.
 */

public class NotificationShower {

  private NotificationAccessor notificationAccessor;

  NotificationShower(NotificationAccessor notificationAccessor) {
    this.notificationAccessor = notificationAccessor;
  }

  public Completable show(PullingContentService context) {
    return getNotification().flatMapCompletable(
        notification -> showNotification(context, notification));
  }

  public Completable showNotification(Context context,
      cm.aptoide.pt.database.realm.Notification notification) {

    if (notification.getNotificationType()
        == cm.aptoide.pt.database.realm.Notification.NOT_EXISTS) {
      return Completable.complete();
    } else {
      return showNotification(context, notification.getTitle().hashCode(), notification.getTitle(),
          notification.getBody(), notification.getImg(), notification.getUrlTrack(),
          notification.getUrl());
    }
  }

  public Single<cm.aptoide.pt.database.realm.Notification> getNotification() {
    return notificationAccessor.getAll()
        .map(notifications -> notifications.get(0))
        .firstOrDefault(cm.aptoide.pt.database.realm.Notification.createEmptyNotification())
        .toSingle();
  }

  private Completable showNotification(Context context, int notificationId, String title,
      String body, String imageUrl, String trackUrl, String url) {

    return Completable.fromAction(() -> {
      PendingIntent pressIntentAction = getPressIntentAction(trackUrl, url);

      Notification notification =
          buildNotification(context, title, body, imageUrl, pressIntentAction, notificationId);

      final NotificationManager managerNotification =
          (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

      managerNotification.notify(notificationId, notification);
    }).subscribeOn(AndroidSchedulers.mainThread());
  }

  @NonNull private Notification buildNotification(Context context, String title, String body,
      String imageUrl, PendingIntent pressIntentAction, int notificationId) {
    Notification notification =
        new NotificationCompat.Builder(Application.getContext()).setContentIntent(pressIntentAction)
            .setOngoing(false)
            .setSmallIcon(R.drawable.ic_stat_aptoide_notification)
            .setLargeIcon(BitmapFactory.decodeResource(Application.getContext().getResources(),
                Application.getConfiguration().getIcon()))
            .setContentTitle(title)
            .setContentText(body)
            .build();
    notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;

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

  private PendingIntent getPressIntentAction(String trackUrl, String url) {
    Intent resultIntent = new Intent(Application.getContext(), PullingContentReceiver.class);
    resultIntent.setAction(PullingContentReceiver.NOTIFICATION_PRESSED_ACTION);

    if (!TextUtils.isEmpty(trackUrl)) {
      resultIntent.putExtra(PullingContentReceiver.PUSH_NOTIFICATION_TRACK_URL, trackUrl);
    }
    if (!TextUtils.isEmpty(url)) {
      resultIntent.putExtra(PullingContentReceiver.PUSH_NOTIFICATION_TARGET_URL, url);
    }

    return PendingIntent.getBroadcast(Application.getContext(), 0, resultIntent,
        PendingIntent.FLAG_UPDATE_CURRENT);
  }
}
