package cm.aptoide.pt.v8engine.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.PushNotificationsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v3.GetPushNotificationsResponse;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.receivers.DeepLinkIntentReceiver;
import cm.aptoide.pt.v8engine.receivers.PullingContentReceiver;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.repository.UpdateRepository;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
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
  public static final long PUSH_NOTIFICATION_INTERVAL = AlarmManager.INTERVAL_DAY;
  public static final int PUSH_NOTIFICATION_ID = 86456;
  public static final int UPDATE_NOTIFICATION_ID = 123;
  private static final String TAG = PullingContentService.class.getSimpleName();
  private CompositeSubscription subscriptions;
  private InstallManager installManager;
  private BodyInterceptor<BaseBody> baseBodyInterceptorV3;

  public static void setAlarm(AlarmManager am, Context context, String action, long time) {
    Intent intent = new Intent(context, PullingContentService.class);
    intent.setAction(action);
    PendingIntent pendingIntent =
        PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 5000, time, pendingIntent);
  }

  @Override public void onCreate() {
    super.onCreate();
    baseBodyInterceptorV3 = ((V8Engine) this.getApplicationContext()).getBaseBodyInterceptorV3();
    installManager = new InstallManager(AptoideDownloadManager.getInstance(),
        new InstallerFactory().create(this, InstallerFactory.ROLLBACK));

    subscriptions = new CompositeSubscription();
    AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
    if (!isAlarmUp(this, PUSH_NOTIFICATIONS_ACTION)) {
      setAlarm(alarm, this, PUSH_NOTIFICATIONS_ACTION, PUSH_NOTIFICATION_INTERVAL);
    }
    if (!isAlarmUp(this, UPDATES_ACTION)) {
      setAlarm(alarm, this, UPDATES_ACTION, UPDATES_INTERVAL);
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
    UpdateRepository repository = RepositoryFactory.getUpdateRepository(this);
    subscriptions.add(repository.sync(true)
        .andThen(repository.getAll(false))
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
          CrashReport.getInstance().log(throwable);
        }));
  }

  /**
   * setup on push notifications action received
   *
   * @param startId service startid
   */
  private void setPushNotificationsAction(Context context, int startId) {
    PushNotificationsRequest.of(baseBodyInterceptorV3)
        .execute(response -> setPushNotification(context, response, startId));
  }

  /**
   * @return true if updateList were installed with success, false otherwise
   */
  private Observable<Boolean> autoUpdate(List<Update> updateList) {
    return Observable.just(
        ManagerPreferences.isAutoUpdateEnable() && ManagerPreferences.allowRootInstallation())
        .flatMap(shouldAutoUpdateRun -> {
          if (shouldAutoUpdateRun) {
            return Observable.just(updateList).observeOn(Schedulers.io()).map(updates -> {
              ArrayList<Download> downloadList = new ArrayList<>(updates.size());
              for (Update update : updates) {
                downloadList.add(new DownloadFactory().create(update));
              }
              return downloadList;
            }).flatMap(downloads -> installManager.startInstalls(downloads, this));
          } else {
            return Observable.just(false);
          }
        });
  }

  private void setUpdatesNotification(List<Update> updates, int startId) {
    Intent resultIntent = new Intent(Application.getContext(),
        V8Engine.getActivityProvider().getMainActivityFragmentClass());
    resultIntent.putExtra(DeepLinkIntentReceiver.DeepLinksTargets.NEW_UPDATES, true);
    PendingIntent resultPendingIntent =
        PendingIntent.getActivity(Application.getContext(), 0, resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT);

    int numberUpdates = updates.size();
    if (numberUpdates > 0
        && numberUpdates != ManagerPreferences.getLastUpdates()
        && ManagerPreferences.isUpdateNotificationEnable()) {
      CharSequence tickerText = AptoideUtils.StringU.getFormattedString(R.string.has_updates,
          Application.getConfiguration().getMarketName());
      CharSequence contentTitle = Application.getConfiguration().getMarketName();
      CharSequence contentText =
          AptoideUtils.StringU.getFormattedString(R.string.new_updates, numberUpdates);
      if (numberUpdates == 1) {
        contentText =
            AptoideUtils.StringU.getFormattedString(R.string.one_new_update, numberUpdates);
      }

      Notification notification =
          new NotificationCompat.Builder(Application.getContext()).setContentIntent(
              resultPendingIntent)
              .setOngoing(false)
              .setSmallIcon(R.drawable.ic_stat_aptoide_notification)
              .setLargeIcon(BitmapFactory.decodeResource(Application.getContext().getResources(),
                  Application.getConfiguration().getIcon()))
              .setContentTitle(contentTitle)
              .setContentText(contentText)
              .setTicker(tickerText)
              .build();

      notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
      final NotificationManager managerNotification = (NotificationManager) Application.getContext()
          .getSystemService(Context.NOTIFICATION_SERVICE);
      managerNotification.notify(UPDATE_NOTIFICATION_ID, notification);
      ManagerPreferences.setLastUpdates(numberUpdates);
    }
    stopSelf(startId);
  }

  private void setPushNotification(Context context, GetPushNotificationsResponse response,
      int startId) {
    for (final GetPushNotificationsResponse.Notification pushNotification : response.getResults()) {
      Intent resultIntent = new Intent(Application.getContext(), PullingContentReceiver.class);
      resultIntent.setAction(PullingContentReceiver.NOTIFICATION_PRESSED_ACTION);
      resultIntent.putExtra(PullingContentReceiver.PUSH_NOTIFICATION_TRACK_URL,
          pushNotification.getTrackUrl());
      resultIntent.putExtra(PullingContentReceiver.PUSH_NOTIFICATION_TARGET_URL,
          pushNotification.getTargetUrl());

      PendingIntent resultPendingIntent =
          PendingIntent.getBroadcast(Application.getContext(), 0, resultIntent,
              PendingIntent.FLAG_UPDATE_CURRENT);
      Notification notification =
          new NotificationCompat.Builder(Application.getContext()).setContentIntent(
              resultPendingIntent)
              .setOngoing(false)
              .setSmallIcon(R.drawable.ic_stat_aptoide_notification)
              .setLargeIcon(BitmapFactory.decodeResource(Application.getContext().getResources(),
                  Application.getConfiguration().getIcon()))
              .setContentTitle(pushNotification.getTitle())
              .setContentText(pushNotification.getMessage())
              .build();

      notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
      final NotificationManager managerNotification = (NotificationManager) Application.getContext()
          .getSystemService(Context.NOTIFICATION_SERVICE);

      if (Build.VERSION.SDK_INT >= 16
          && Build.VERSION.SDK_INT < 24
          && pushNotification.getImages() != null
          && TextUtils.isEmpty(pushNotification.getImages().getIconUrl())) {

        String imageUrl = pushNotification.getImages().getBannerUrl();
        RemoteViews expandedView = new RemoteViews(Application.getContext().getPackageName(),
            R.layout.pushnotificationlayout);
        expandedView.setImageViewBitmap(R.id.icon,
            BitmapFactory.decodeResource(Application.getContext().getResources(),
                Application.getConfiguration().getIcon()));
        expandedView.setTextViewText(R.id.text1, pushNotification.getTitle());
        expandedView.setTextViewText(R.id.description, pushNotification.getMessage());
        notification.bigContentView = expandedView;
        NotificationTarget notificationTarget =
            new NotificationTarget(Application.getContext(), expandedView,
                R.id.PushNotificationImageView, notification, PUSH_NOTIFICATION_ID);
        ImageLoader.with(context).loadImageToNotification(notificationTarget, imageUrl);
      }

      if (!response.getResults().isEmpty()) {
        ManagerPreferences.setLastPushNotificationId(
            response.getResults().get(0).getId().intValue());
      }
      managerNotification.notify(PUSH_NOTIFICATION_ID, notification);
    }
    stopSelf(startId);
  }
}
