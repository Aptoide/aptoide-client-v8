package cm.aptoide.pt.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.DeepLinkIntentReceiver;
import cm.aptoide.pt.InstallManager;
import cm.aptoide.pt.PartnerApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dataprovider.ws.v3.GetPushNotificationsResponse;
import cm.aptoide.pt.dataprovider.ws.v3.PushNotificationsRequest;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.install.InstallerFactory;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.updates.UpdateRepository;
import cm.aptoide.pt.utils.AptoideUtils;
import com.bumptech.glide.request.target.NotificationTarget;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by trinkes on 7/13/16.
 */
public class PullingContentService extends Service {

  public static final String PUSH_NOTIFICATIONS_ACTION = "PUSH_NOTIFICATIONS_ACTION";
  public static final String UPDATES_ACTION = "UPDATES_ACTION";
  public static final String BOOT_COMPLETED_ACTION = "BOOT_COMPLETED_ACTION";
  public static final long UPDATES_INTERVAL = AlarmManager.INTERVAL_HALF_DAY;
  public static final int UPDATE_NOTIFICATION_ID = 123;
  public static final long PUSH_NOTIFICATION_INTERVAL = AlarmManager.INTERVAL_DAY;
  public static final int PUSH_NOTIFICATION_ID = 86456;
  private CompositeSubscription subscriptions;
  private InstallManager installManager;
  private UpdateRepository updateRepository;
  private SharedPreferences sharedPreferences;
  private String marketName;

  public void setAlarm(AlarmManager am, Context context, String action, long time) {
    Intent intent = new Intent(context, PullingContentService.class);
    intent.setAction(action);
    PendingIntent pendingIntent =
        PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 5000, time, pendingIntent);
  }

  @Override public void onCreate() {
    super.onCreate();
    marketName = ((AptoideApplication) getApplicationContext()).getMarketName();
    sharedPreferences =
        ((AptoideApplication) getApplicationContext()).getDefaultSharedPreferences();
    updateRepository = RepositoryFactory.getUpdateRepository(this, sharedPreferences);
    installManager =
        ((AptoideApplication) getApplicationContext()).getInstallManager(InstallerFactory.ROLLBACK);

    subscriptions = new CompositeSubscription();
    AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

    if (!isAlarmUp(this, UPDATES_ACTION)) {
      setAlarm(alarm, this, UPDATES_ACTION, UPDATES_INTERVAL);
    }
    if (!isAlarmUp(this, PUSH_NOTIFICATIONS_ACTION)) {
      setAlarm(alarm, this, PUSH_NOTIFICATIONS_ACTION, PUSH_NOTIFICATION_INTERVAL);
    }
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {

    String action = intent == null ? null : intent.getAction();
    if (action != null) {
      switch (action) {
        case UPDATES_ACTION:
          setUpdatesAction(startId);
          break;
        case PUSH_NOTIFICATIONS_ACTION:
          setPushNotificationsAction(this, startId);
          break;
        case BOOT_COMPLETED_ACTION:
          setUpdatesAction(startId);
          setPushNotificationsAction(this, startId);
          break;
      }
    }
    return START_NOT_STICKY;
  }

  private void setPushNotificationsAction(Context context, int startId) {
    PushNotificationsRequest.of(
        ((AptoideApplication) getApplicationContext()).getDefaultSharedPreferences(),
        getApplicationContext().getResources(), getApplicationContext(),
        ((PartnerApplication) getApplicationContext()).getPartnerId(),
        ((AptoideApplication) getApplicationContext()).getBodyInterceptorV3(),
        ((AptoideApplication) getApplicationContext()).getTokenInvalidator())
        .execute(response -> setPushNotification(context, response, startId));
  }

  private void setPushNotification(Context context, GetPushNotificationsResponse response,
      int startId) {
    for (final GetPushNotificationsResponse.Notification pushNotification : response.getResults()) {
      Intent resultIntent = new Intent(getApplicationContext(), PullingContentReceiver.class);
      resultIntent.setAction(PullingContentReceiver.NOTIFICATION_PRESSED_ACTION);
      resultIntent.putExtra(PullingContentReceiver.PUSH_NOTIFICATION_TRACK_URL,
          pushNotification.getTrackUrl());
      resultIntent.putExtra(PullingContentReceiver.PUSH_NOTIFICATION_TARGET_URL,
          pushNotification.getTargetUrl());

      PendingIntent resultPendingIntent =
          PendingIntent.getBroadcast(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
      Notification notification =
          new NotificationCompat.Builder(getApplicationContext()).setContentIntent(
              resultPendingIntent)
              .setOngoing(false)
              .setSmallIcon(R.drawable.ic_stat_aptoide_notification)
              .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                  R.mipmap.ic_launcher))
              .setContentTitle(pushNotification.getTitle())
              .setContentText(pushNotification.getMessage())
              .build();

      notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
      final NotificationManager managerNotification =
          (NotificationManager) getApplicationContext().getSystemService(
              Context.NOTIFICATION_SERVICE);

      if (Build.VERSION.SDK_INT >= 16
          && Build.VERSION.SDK_INT < 24
          && pushNotification.getImages() != null
          && TextUtils.isEmpty(pushNotification.getImages()
          .getIconUrl())) {

        String imageUrl = pushNotification.getImages()
            .getBannerUrl();
        RemoteViews expandedView = new RemoteViews(getApplicationContext().getPackageName(),
            R.layout.push_notification_layout);
        expandedView.setImageViewBitmap(R.id.icon,
            BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.mipmap.ic_launcher));
        expandedView.setTextViewText(R.id.text1, pushNotification.getTitle());
        expandedView.setTextViewText(R.id.description, pushNotification.getMessage());
        notification.bigContentView = expandedView;
        NotificationTarget notificationTarget =
            new NotificationTarget(getApplicationContext(), expandedView,
                R.id.PushNotificationImageView, notification, PUSH_NOTIFICATION_ID);
        ImageLoader.with(context)
            .loadImageToNotification(notificationTarget, imageUrl);
      }

      if (!response.getResults()
          .isEmpty()) {
        ManagerPreferences.setLastPushNotificationId(response.getResults()
            .get(0)
            .getId()
            .intValue(), sharedPreferences);
      }
      managerNotification.notify(PUSH_NOTIFICATION_ID, notification);
    }
    stopSelf(startId);
  }

  @Override public void onDestroy() {
    subscriptions.clear();
    super.onDestroy();
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  private boolean isAlarmUp(Context context, String action) {
    Intent intent = new Intent(context, PullingContentService.class);
    intent.setAction(action);
    return (PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE) != null);
  }

  /**
   * Setup on updates action received
   *
   * @param startId service startid
   */
  private void setUpdatesAction(int startId) {
    subscriptions.add(updateRepository.sync(true)
        .andThen(updateRepository.getAll(false))
        .first()
        .observeOn(Schedulers.computation())
        .flatMap(updates -> autoUpdate(updates).flatMap(autoUpdateRunned -> {
          if (autoUpdateRunned) {
            return Observable.empty();
          } else {
            return Observable.just(updates);
          }
        }))
        .subscribe(updates -> {
          setUpdatesNotification(updates, startId);
        }, throwable -> {
          throwable.printStackTrace();
          CrashReport.getInstance()
              .log(throwable);
        }));
  }

  /**
   * @return true if updateList were installed with success, false otherwise
   */
  private Observable<Boolean> autoUpdate(List<Update> updateList) {
    return Observable.just(ManagerPreferences.isAutoUpdateEnable(sharedPreferences)
        && ManagerPreferences.allowRootInstallation(sharedPreferences))
        .flatMap(shouldAutoUpdateRun -> {
          if (shouldAutoUpdateRun) {
            return Observable.just(updateList)
                .observeOn(Schedulers.io())
                .map(updates -> {
                  ArrayList<Download> downloadList = new ArrayList<>(updates.size());
                  for (Update update : updates) {
                    downloadList.add(new DownloadFactory(marketName).create(update));
                  }
                  return downloadList;
                })
                .flatMap(downloads -> installManager.startInstalls(downloads));
          } else {
            return Observable.just(false);
          }
        });
  }

  private void setUpdatesNotification(List<Update> updates, int startId) {
    Intent resultIntent = new Intent(getApplicationContext(),
        AptoideApplication.getActivityProvider()
            .getMainActivityFragmentClass());
    resultIntent.putExtra(DeepLinkIntentReceiver.DeepLinksTargets.NEW_UPDATES, true);
    PendingIntent resultPendingIntent =
        PendingIntent.getActivity(getApplicationContext(), 0, resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT);

    int numberUpdates = updates.size();
    if (numberUpdates > 0
        && numberUpdates != ManagerPreferences.getLastUpdates(sharedPreferences)
        && ManagerPreferences.isUpdateNotificationEnable(sharedPreferences)) {
      CharSequence tickerText =
          AptoideUtils.StringU.getFormattedString(R.string.has_updates, getResources(), marketName);
      CharSequence contentTitle = marketName;
      CharSequence contentText =
          AptoideUtils.StringU.getFormattedString(R.string.new_updates, getResources(),
              numberUpdates);
      if (numberUpdates == 1) {
        contentText =
            AptoideUtils.StringU.getFormattedString(R.string.one_new_update, getResources(),
                numberUpdates);
      }

      Notification notification =
          new NotificationCompat.Builder(getApplicationContext()).setContentIntent(
              resultPendingIntent)
              .setOngoing(false)
              .setSmallIcon(R.drawable.ic_stat_aptoide_notification)
              .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                  R.mipmap.ic_launcher))
              .setContentTitle(contentTitle)
              .setContentText(contentText)
              .setTicker(tickerText)
              .build();

      notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
      final NotificationManager managerNotification =
          (NotificationManager) getApplicationContext().getSystemService(
              Context.NOTIFICATION_SERVICE);
      managerNotification.notify(UPDATE_NOTIFICATION_ID, notification);
      ManagerPreferences.setLastUpdates(numberUpdates, sharedPreferences);
    }
    stopSelf(startId);
  }
}
