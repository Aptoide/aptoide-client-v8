package cm.aptoide.pt.v8engine.pull;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.NotificationAccessor;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.BuildConfig;
import cm.aptoide.pt.v8engine.DeepLinkIntentReceiver;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.download.DownloadFactory;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.networking.IdsRepository;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.updates.UpdateRepository;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
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
  public static final int UPDATE_NOTIFICATION_ID = 123;
  private CompositeSubscription subscriptions;
  private InstallManager installManager;
  private ScheduleNotificationSync notificationSync;
  private NotificationShower notificationShower;

  public void setAlarm(AlarmManager am, Context context, String action, long time) {
    Intent intent = new Intent(context, PullingContentService.class);
    intent.setAction(action);
    PendingIntent pendingIntent =
        PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 5000, time, pendingIntent);
  }

  @Override public void onCreate() {
    super.onCreate();

    long pushNotificationInterval;

    if (ManagerPreferences.isDebug()
        && ManagerPreferences.getPushNotificationPullingInterval() > 0) {
      pushNotificationInterval = ManagerPreferences.getPushNotificationPullingInterval();
    } else {
      pushNotificationInterval = PUSH_NOTIFICATION_INTERVAL;
    }

    PackageInfo pInfo = null;
    try {
      pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    String versionName = pInfo == null ? "" : pInfo.versionName;

    installManager =
        ((V8Engine) getApplicationContext()).getInstallManager(InstallerFactory.ROLLBACK);
    final OkHttpClient httpClient = ((V8Engine) getApplicationContext()).getDefaultClient();
    final Converter.Factory converterFactory = WebService.getDefaultConverter();
    final IdsRepository idsRepository = ((V8Engine) getApplicationContext()).getIdsRepository();
    subscriptions = new CompositeSubscription();
    NotificationAccessor notificationAccessor =
        AccessorFactory.getAccessorFor(cm.aptoide.pt.database.realm.Notification.class);
    notificationShower = new NotificationShower(notificationAccessor);
    notificationSync =
        new ScheduleNotificationSync(idsRepository, this, httpClient, converterFactory, versionName,
            BuildConfig.APPLICATION_ID, notificationAccessor, notificationShower);

    AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
    if (!isAlarmUp(this, PUSH_NOTIFICATIONS_ACTION)) {
      setAlarm(alarm, this, PUSH_NOTIFICATIONS_ACTION, pushNotificationInterval);
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
          setPushNotificationsAction(startId);
          break;
        case BOOT_COMPLETED_ACTION:
          setUpdatesAction(startId);
          setPushNotificationsAction(startId);
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
   * @param startId service startId
   */
  private void setPushNotificationsAction(int startId) {
    subscriptions.add(notificationSync.sync(PullingContentService.this)
        .subscribe(() -> stopSelf(startId), throwable -> {
          stopSelf(startId);
          CrashReport.getInstance().log(throwable);
        }));
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
}
