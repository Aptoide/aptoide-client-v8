package cm.aptoide.pt.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.NotificationApplicationView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.install.installer.RootInstallErrorNotification;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.presenter.Presenter;
import com.bumptech.glide.request.target.NotificationTarget;
import rx.Completable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by trinkes on 09/05/2017.
 */

public class SystemNotificationShower implements Presenter {

  private final NavigationTracker navigationTracker;
  private Context context;
  private NotificationManager notificationManager;
  private NotificationIdsMapper notificationIdsMapper;
  private NotificationCenter notificationCenter;
  private NotificationAnalytics notificationAnalytics;
  private CrashReport crashReport;
  private NotificationProvider notificationProvider;
  private NotificationApplicationView view;
  private CompositeSubscription subscriptions;

  public SystemNotificationShower(Context context, NotificationManager notificationManager,
      NotificationIdsMapper notificationIdsMapper, NotificationCenter notificationCenter,
      NotificationAnalytics notificationAnalytics, CrashReport crashReport,
      NotificationProvider notificationProvider,
      NotificationApplicationView notificationApplicationView, CompositeSubscription subscriptions,
      NavigationTracker navigationTracker) {
    this.context = context;
    this.notificationManager = notificationManager;
    this.notificationIdsMapper = notificationIdsMapper;
    this.notificationCenter = notificationCenter;
    this.notificationAnalytics = notificationAnalytics;
    this.crashReport = crashReport;
    this.notificationProvider = notificationProvider;
    this.subscriptions = subscriptions;
    view = notificationApplicationView;
    this.navigationTracker = navigationTracker;
  }

  @Override public void present() {
    setNotificationPressSubscribe();
    setNotificationDismissSubscribe();
    setNotificationBootCompletedSubscribe();
    showNewNotification();
  }

  private void showNewNotification() {
    subscriptions.add(notificationCenter.getNewNotifications()
        .flatMapCompletable(aptoideNotification -> {
          int notificationId =
              notificationIdsMapper.getNotificationId(aptoideNotification.getType());
          notificationAnalytics.sendPushNotficationImpressionEvent(aptoideNotification.getType(),
              aptoideNotification.getAbTestingGroup(), aptoideNotification.getCampaignId(),
              aptoideNotification.getUrl());
          return mapToAndroidNotification(aptoideNotification, notificationId).doOnSuccess(
              notification -> notificationManager.notify(notificationId, notification))
              .toCompletable();
        })
        .subscribe(notification -> {
        }, throwable -> crashReport.log(throwable)));
  }

  private Single<Notification> mapToAndroidNotification(AptoideNotification aptoideNotification,
      int notificationId) {
    return getPressIntentAction(aptoideNotification.getUrlTrack(), aptoideNotification.getUrl(),
        notificationId, context).flatMap(
        pressIntentAction -> buildNotification(context, aptoideNotification.getTitle(),
            aptoideNotification.getBody(), aptoideNotification.getImg(), pressIntentAction,
            notificationId, getOnDismissAction(notificationId), aptoideNotification.getAppName(),
            aptoideNotification.getGraphic()));
  }

  private Single<PendingIntent> getPressIntentAction(String trackUrl, String url,
      int notificationId, Context context) {
    return Single.fromCallable(() -> {
      Intent resultIntent = new Intent(context, NotificationReceiver.class);
      resultIntent.setAction(NotificationReceiver.NOTIFICATION_PRESSED_ACTION);

      resultIntent.putExtra(NotificationReceiver.NOTIFICATION_NOTIFICATION_ID, notificationId);

      if (!TextUtils.isEmpty(trackUrl)) {
        resultIntent.putExtra(NotificationReceiver.NOTIFICATION_TRACK_URL, trackUrl);
      }
      if (!TextUtils.isEmpty(url)) {
        resultIntent.putExtra(NotificationReceiver.NOTIFICATION_TARGET_URL, url);
      }

      return PendingIntent.getBroadcast(context, notificationId, resultIntent,
          PendingIntent.FLAG_UPDATE_CURRENT);
    })
        .subscribeOn(Schedulers.computation());
  }

  @NonNull private Single<android.app.Notification> buildNotification(Context context, String title,
      String body, String iconUrl, PendingIntent pressIntentAction, int notificationId,
      PendingIntent onDismissAction, String appName, String graphic) {
    return Single.fromCallable(() -> {
      NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
          createNotificationChannel("system_notification", "System Notification"));
      android.app.Notification notification = builder.setContentIntent(pressIntentAction)
              .setOngoing(false)
              .setSmallIcon(R.drawable.ic_stat_aptoide_notification)
              .setLargeIcon(ImageLoader.with(context)
                  .loadBitmap(iconUrl))
              .setContentTitle(title)
              .setContentText(body)
              .setDeleteIntent(onDismissAction)
              .build();
      notification.flags =
          android.app.Notification.DEFAULT_LIGHTS | android.app.Notification.FLAG_AUTO_CANCEL;
      return notification;
    })
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .map(notification -> setExpandedView(context, title, body, notificationId, notification,
            appName, graphic, iconUrl));
  }

  private String createNotificationChannel(String channelId, String channelName) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
      return "";
    }
    NotificationChannel chan =
        new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
    chan.setLightColor(Color.BLUE);
    chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
    NotificationManager service =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    service.createNotificationChannel(chan);
    return channelId;
  }

  private android.app.Notification setExpandedView(Context context, String title, String body,
      int notificationId, Notification notification, String appName, String graphic,
      String iconUrl) {

    if (Build.VERSION.SDK_INT >= 16) {
      RemoteViews expandedView =
          new RemoteViews(context.getPackageName(), R.layout.pushnotificationlayout);
      //in this case, large icon is loaded already, so instead of reloading it, we just reuse it
      loadImage(context, notificationId, notification, iconUrl, expandedView, R.id.icon);
      expandedView.setTextViewText(R.id.title, title);
      expandedView.setTextViewText(R.id.app_name, appName);
      expandedView.setTextViewText(R.id.description, body);
      if (!TextUtils.isEmpty(graphic)) {
        loadImage(context, notificationId, notification, graphic, expandedView,
            R.id.push_notification_graphic);
      } else {
        expandedView.setViewVisibility(R.id.push_notification_graphic, View.GONE);
      }
      notification.bigContentView = expandedView;
    }
    return notification;
  }

  private void loadImage(Context context, int notificationId, Notification notification, String url,
      RemoteViews expandedView, @IdRes int viewId) {
    NotificationTarget notificationTarget =
        new NotificationTarget(context, viewId, expandedView, notification, notificationId);
    ImageLoader.with(context)
        .loadImageToNotification(notificationTarget, url);
  }

  public PendingIntent getOnDismissAction(int notificationId) {
    Intent resultIntent = new Intent(context, NotificationReceiver.class);
    resultIntent.setAction(NotificationReceiver.NOTIFICATION_DISMISSED_ACTION);
    resultIntent.putExtra(NotificationReceiver.NOTIFICATION_NOTIFICATION_ID, notificationId);

    return PendingIntent.getBroadcast(context, notificationId, resultIntent,
        PendingIntent.FLAG_UPDATE_CURRENT);
  }

  public void showNotification(Context context,
      RootInstallErrorNotification installErrorNotification) {
    android.app.Notification notification =
        mapToAndroidNotification(context, installErrorNotification);
    notificationManager.notify(installErrorNotification.getNotificationId(), notification);
  }

  private Notification mapToAndroidNotification(Context context,
      RootInstallErrorNotification installErrorNotification) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
        createNotificationChannel("system_notification", "System Notification"));
    Notification notification = builder.setContentTitle(
        installErrorNotification.getMessage())
        .setSmallIcon(R.drawable.ic_stat_aptoide_notification)
        .setLargeIcon(installErrorNotification.getIcon())
        .setAutoCancel(true)
        .addAction(installErrorNotification.getAction())
        .setDeleteIntent(installErrorNotification.getDeleteAction())
        .build();

    notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
    return notification;
  }

  public void dismissNotification(int notificationId) {
    notificationManager.cancel(notificationId);
  }

  private Completable dismissNotificationAfterAction(int notificationId) {
    return Completable.defer(() -> {
      try {
        return notificationCenter.notificationDismissed(
            notificationIdsMapper.getNotificationType(notificationId));
      } catch (RuntimeException e) {
        return Completable.error(e);
      }
    });
  }

  private void callDeepLink(Context context, NotificationInfo notificationInfo) {
    String targetUrl = notificationInfo.getNotificationUrl();
    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl));
    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    try {
      context.startActivity(i);
    } catch (ActivityNotFoundException e) {
      crashReport.log(e);
    }
  }

  private void setNotificationBootCompletedSubscribe() {
    view.getActionBootCompleted()
        .doOnNext(__ -> notificationCenter.setup())
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void setNotificationDismissSubscribe() {
    view.getNotificationDismissed()
        .filter(notificationInfo -> notificationInfo.getNotificationType() < 7)
        .doOnNext(notificationInfo -> dismissNotificationAfterAction(
            notificationInfo.getNotificationType()))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void setNotificationPressSubscribe() {
    view.getNotificationClick()
        .flatMapSingle(notificationInfo -> notificationProvider.getLastShowed(
            notificationIdsMapper.getNotificationType(notificationInfo.getNotificationType()))
            .doOnSuccess(notification -> {
              notificationAnalytics.sendPushNotificationPressedEvent(notification.getType(),
                  notification.getAbTestingGroup(), notification.getCampaignId(),
                  notification.getUrl());
              notificationAnalytics.sendNotificationTouchEvent(
                  notificationInfo.getNotificationTrackUrl(),
                  notificationInfo.getNotificationType(), notificationInfo.getNotificationUrl(),
                  notification.getCampaignId(), notification.getAbTestingGroup());
            })
            .doOnSuccess(notification -> navigationTracker.registerScreen(
                ScreenTagHistory.Builder.build("Notification")))
            .map(notification -> notificationInfo))
        .doOnNext(notificationInfo -> callDeepLink(context, notificationInfo))
        .flatMapCompletable(notificationInfo -> dismissNotificationAfterAction(
            notificationInfo.getNotificationType()))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }
}
