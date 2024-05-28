package cm.aptoide.pt.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.NotificationApplicationView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.install.installer.RootInstallErrorNotification;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.themes.NewFeatureManager;
import cm.aptoide.pt.themes.ThemeAnalytics;
import com.bumptech.glide.Glide;
import java.util.Arrays;
import rx.Completable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by trinkes on 09/05/2017.
 */

public class SystemNotificationShower implements Presenter {

  public static final String LOCAL_NOTIFICATION_CHANNEL_ID = "LocalNotification";
  public static final String ANDROID_NOTIFICATION_CHANNEL_ID = "AndroidNotification";
  public static final String NEW_FEATURE_NOTIFICATION_CHANNEL_ID = "NewFeatureNotification";
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
  private NewFeatureManager newFeatureManager;
  private ThemeAnalytics themeAnalytics;
  private ReadyToInstallNotificationManager readyToInstallNotificationManager;

  public SystemNotificationShower(Context context, NotificationManager notificationManager,
      NotificationIdsMapper notificationIdsMapper, NotificationCenter notificationCenter,
      NotificationAnalytics notificationAnalytics, CrashReport crashReport,
      NotificationProvider notificationProvider,
      NotificationApplicationView notificationApplicationView, CompositeSubscription subscriptions,
      NavigationTracker navigationTracker, NewFeatureManager newFeatureManager,
      ThemeAnalytics themeAnalytics,
      ReadyToInstallNotificationManager readyToInstallNotificationManager) {
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
    this.newFeatureManager = newFeatureManager;
    this.themeAnalytics = themeAnalytics;
    this.readyToInstallNotificationManager = readyToInstallNotificationManager;
  }

  @Override public void present() {
    setupChannels();
    setNotificationPressSubscribe();
    setNotificationDismissSubscribe();
    setNotificationBootCompletedSubscribe();
    showNewNotification();
  }

  private void setupChannels() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      notificationManager.createNotificationChannels(
          Arrays.asList(getLocalNotificationChannel(), getAndroidNotificationChannel(),
              getNewFeatureNotificationChannel(),
              readyToInstallNotificationManager.getNotificationChannel()));
    }
  }

  private void showNewNotification() {
    subscriptions.add(notificationCenter.getNewNotifications()
        .flatMapCompletable(aptoideNotification -> {
          int notificationId =
              notificationIdsMapper.getNotificationId(aptoideNotification.getType());
          if (aptoideNotification.getType() != AptoideNotification.APPC_PROMOTION
              && aptoideNotification.getType() != AptoideNotification.NEW_FEATURE
              && aptoideNotification.getType() != AptoideNotification.APPS_READY_TO_INSTALL) {
            notificationAnalytics.sendPushNotficationImpressionEvent(aptoideNotification.getType(),
                aptoideNotification.getAbTestingGroup(), aptoideNotification.getCampaignId(),
                aptoideNotification.getUrl());
            return mapToAndroidNotification(aptoideNotification, notificationId).doOnSuccess(
                    notification -> notificationManager.notify(notificationId, notification))
                .toCompletable();
          } else {
            return mapLocalToAndroidNotification(aptoideNotification, notificationId).doOnSuccess(
                    notification -> notificationManager.notify(notificationId, notification))
                .toCompletable();
          }
        })
        .retry()
        .subscribe(notification -> {
        }, throwable -> crashReport.log(throwable)));
  }

  private Single<Notification> mapToAndroidNotification(AptoideNotification aptoideNotification,
      int notificationId) {
    return getPressIntentAction(aptoideNotification.getUrlTrack(), aptoideNotification.getUrl(),
        notificationId, context).flatMap(
        pressIntentAction -> buildNotification(context, aptoideNotification.getTitle(),
            aptoideNotification.getBody(), aptoideNotification.getImg(), pressIntentAction,
            getOnDismissAction(notificationId)));
  }

  private Single<Notification> mapLocalToAndroidNotification(
      AptoideNotification aptoideNotification, int notificationId) {
    return getPressIntentAction(aptoideNotification.getUrlTrack(), aptoideNotification.getUrl(),
        notificationId, context).flatMap(pressIntentAction -> {
      if (aptoideNotification.getType() == AptoideNotification.NEW_FEATURE) {
        newFeatureManager.setFeatureAsShown();
        return buildNewFeatureNotification(context, aptoideNotification.getTitle(),
            aptoideNotification.getBody(), aptoideNotification.getActionStringRes(),
            pressIntentAction, getOnDismissAction(notificationId));
      } else if (aptoideNotification.getType() == AptoideNotification.APPS_READY_TO_INSTALL) {
        return readyToInstallNotificationManager.buildNotification(aptoideNotification, context)
            .doOnSuccess(__ -> readyToInstallNotificationManager.setIsNotificationDisplayed(true));
      } else {
        return buildLocalNotification(context, aptoideNotification.getTitle(),
            aptoideNotification.getBody(), aptoideNotification.getImg(), pressIntentAction,
            getOnDismissAction(notificationId));
      }
    });
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

  @NonNull private Single<Notification> buildNewFeatureNotification(Context context, String title,
      String body, @StringRes int actionButtonString, PendingIntent pressIntentAction,
      PendingIntent onDismissAction) {

    return Single.fromCallable(
            () -> new NotificationCompat.Builder(context, NEW_FEATURE_NOTIFICATION_CHANNEL_ID)
                .setContentIntent(pressIntentAction)
                .setSmallIcon(R.drawable.ic_stat_aptoide_notification)
                .setColor(ContextCompat.getColor(context, R.color.default_orange_gradient_end))
                .setContentTitle(title)
                .setContentText(body)
                .addAction(0, context.getResources()
                    .getString(R.string.updates_notification_dismiss_button), onDismissAction)
                .addAction(0, context.getResources()
                    .getString(actionButtonString), pressIntentAction)
                .build())
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread());
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private NotificationChannel getLocalNotificationChannel() {
    String name = "Local Aptoide System notifications";
    String descriptionText = "Aptoide notifications";
    int importance = NotificationManager.IMPORTANCE_DEFAULT;
    NotificationChannel notificationChannel =
        new NotificationChannel(LOCAL_NOTIFICATION_CHANNEL_ID, name, importance);
    notificationChannel.setDescription(descriptionText);
    return notificationChannel;
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private NotificationChannel getAndroidNotificationChannel() {
    String name = "Aptoide Android System notifications";
    String descriptionText = "Aptoide android notifications";
    int importance = NotificationManager.IMPORTANCE_DEFAULT;
    NotificationChannel notificationChannel =
        new NotificationChannel(ANDROID_NOTIFICATION_CHANNEL_ID, name, importance);
    notificationChannel.setDescription(descriptionText);
    return notificationChannel;
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private NotificationChannel getNewFeatureNotificationChannel() {
    String name = "Aptoide New Feature System notifications";
    String descriptionText = "Aptoide new feature notifications";
    int importance = NotificationManager.IMPORTANCE_DEFAULT;
    NotificationChannel notificationChannel =
        new NotificationChannel(NEW_FEATURE_NOTIFICATION_CHANNEL_ID, name, importance);
    notificationChannel.setDescription(descriptionText);
    return notificationChannel;
  }

  private Single<Notification> buildLocalNotification(Context context, String title, String body,
      String iconUrl, PendingIntent pressIntentAction, PendingIntent onDismissAction) { //new one
    return Single.fromCallable(
            () -> new NotificationCompat.Builder(context, LOCAL_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_aptoide_notification)
                .setColor(ContextCompat.getColor(context, R.color.default_orange_gradient_end))
                .setContentTitle(title)
                .setContentText(body)
                .addAction(0, context.getResources()
                        .getString(R.string.promo_update2appc_notification_dismiss_button),
                    onDismissAction)
                .addAction(0, context.getResources()
                        .getString(R.string.promo_update2appc_notification_claim_button),
                    pressIntentAction)
                .setLargeIcon(Glide.with(context)
                    .asBitmap()
                    .load(iconUrl)
                    .submit()
                    .get())
                .build())
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread());
  }

  @NonNull private Single<android.app.Notification> buildNotification(Context context, String title,
      String body, String iconUrl, PendingIntent pressIntentAction, PendingIntent onDismissAction) {
    return Single.fromCallable(
            () -> new NotificationCompat.Builder(context, ANDROID_NOTIFICATION_CHANNEL_ID)
                .setContentIntent(pressIntentAction)
                .setOngoing(false)
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.drawable.ic_stat_aptoide_notification)
                .setLargeIcon(ImageLoader.with(context)
                    .loadBitmap(iconUrl))
                .setContentTitle(title)
                .setContentText(body)
                .setDeleteIntent(onDismissAction)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .build())
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread());
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
    Notification notification = new NotificationCompat.Builder(context).setContentTitle(
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
        .filter(notificationInfo -> notificationInfo.getNotificationType() < 9)
        .doOnNext(notificationInfo -> {
          if (notificationIdsMapper.getNotificationType(
              notificationInfo.getNotificationType())[0].equals(AptoideNotification.NEW_FEATURE)) {
            themeAnalytics.sendDarkThemeDismissClickEvent("Notification");
          }
          if (notificationIdsMapper.getNotificationType(
              notificationInfo.getNotificationType())[0].equals(
              AptoideNotification.APPS_READY_TO_INSTALL)) {
            readyToInstallNotificationManager.setIsNotificationDisplayed(false);
          }
        })
        .flatMapCompletable(notificationInfo -> dismissNotificationAfterAction(
            notificationInfo.getNotificationType()))
        .filter(notificationInfo -> notificationIdsMapper.getNotificationType(
            notificationInfo.getNotificationType())[0].equals(AptoideNotification.APPC_PROMOTION))
        .flatMapCompletable(notificationInfo -> notificationProvider.deleteAllForType(
            AptoideNotification.APPC_PROMOTION))
        .toCompletable()
        .subscribe(() -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void setNotificationPressSubscribe() {
    view.getNotificationClick()
        .flatMapSingle(notificationInfo -> notificationProvider.getLastShowed(
                notificationIdsMapper.getNotificationType(notificationInfo.getNotificationType()))
            .doOnSuccess(notification -> {
              if (notification.getType() != AptoideNotification.APPC_PROMOTION
                  && notification.getType() != AptoideNotification.NEW_FEATURE
                  && notification.getType() != AptoideNotification.APPS_READY_TO_INSTALL) {
                notificationAnalytics.sendPushNotificationPressedEvent(notification.getType(),
                    notification.getAbTestingGroup(), notification.getCampaignId(),
                    notification.getUrl());
                notificationAnalytics.sendNotificationTouchEvent(
                    notificationInfo.getNotificationTrackUrl(),
                    notificationInfo.getNotificationType(), notificationInfo.getNotificationUrl(),
                    notification.getCampaignId(), notification.getAbTestingGroup());
              }
              if (notification.getType() == AptoideNotification.NEW_FEATURE) {
                themeAnalytics.sendDarkThemeDialogTurnItOnClickEvent("Notification");
              }
            })
            .doOnSuccess(notification -> navigationTracker.registerScreen(
                ScreenTagHistory.Builder.build("Notification")))
            .map(notification -> notificationInfo))
        .flatMapCompletable(notificationInfo -> {
          callDeepLink(context, notificationInfo);
          return dismissNotificationAfterAction(notificationInfo.getNotificationType());
        })
        .filter(notificationInfo -> notificationIdsMapper.getNotificationType(
            notificationInfo.getNotificationType())[0].equals(AptoideNotification.APPC_PROMOTION))
        .flatMapCompletable(notificationInfo -> notificationProvider.deleteAllForType(
            AptoideNotification.APPC_PROMOTION))
        .toCompletable()
        .subscribe(() -> {
        }, throwable -> crashReport.log(throwable));
  }
}
