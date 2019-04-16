package cm.aptoide.pt.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.BaseService;
import cm.aptoide.pt.DeepLinkIntentReceiver;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.updates.UpdateRepository;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by trinkes on 7/13/16.
 */
public class PullingContentService extends BaseService {

  public static final String PUSH_NOTIFICATIONS_ACTION = "PUSH_NOTIFICATIONS_ACTION";
  public static final String UPDATES_ACTION = "UPDATES_ACTION";
  public static final String BOOT_COMPLETED_ACTION = "BOOT_COMPLETED_ACTION";
  public static final long UPDATES_INTERVAL = AlarmManager.INTERVAL_DAY;
  public static final int UPDATE_NOTIFICATION_ID = 123;
  @Inject @Named("marketName") String marketName;
  @Inject DownloadFactory downloadFactory;
  private AptoideApplication application;
  private CompositeSubscription subscriptions;
  private InstallManager installManager;
  private UpdateRepository updateRepository;
  private SharedPreferences sharedPreferences;
  private NotificationAnalytics notificationAnalytics;

  public void setAlarm(AlarmManager am, Context context, String action, long time) {
    Intent intent = new Intent(context, PullingContentService.class);
    intent.setAction(action);
    PendingIntent pendingIntent =
        PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 5000, time, pendingIntent);
  }

  @Override public void onCreate() {
    super.onCreate();
    getApplicationComponent().inject(this);
    application = (AptoideApplication) getApplicationContext();
    sharedPreferences = application.getDefaultSharedPreferences();
    installManager = application.getInstallManager();
    updateRepository = RepositoryFactory.getUpdateRepository(this, sharedPreferences);
    notificationAnalytics = application.getNotificationAnalytics();
    subscriptions = new CompositeSubscription();
    AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

    if (!isAlarmUp(this, UPDATES_ACTION)) {
      setAlarm(alarm, this, UPDATES_ACTION, UPDATES_INTERVAL);
    }
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {

    String action = intent == null ? null : intent.getAction();
    if (action != null) {
      switch (action) {
        case UPDATES_ACTION:
        case BOOT_COMPLETED_ACTION:
          setUpdatesAction(startId);
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
    subscriptions.add(updateRepository.sync(true, false)
        .andThen(updateRepository.getAll(false))
        .flatMap(updates -> Observable.just(updates)
            .flatMapIterable(list -> list)
            .filter(update -> !update.isAppcUpgrade())
            .toList())
        .first()
        .observeOn(Schedulers.computation())
        .flatMap(updates -> autoUpdate(updates).flatMap(autoUpdateRunned -> {
          if (autoUpdateRunned) {
            return Observable.empty();
          } else {
            return Observable.just(updates);
          }
        }))
        .filter(__ -> ManagerPreferences.isUpdateNotificationEnable(sharedPreferences))
        .observeOn(Schedulers.io())
        .doOnNext(updates -> {
          notificationAnalytics.sendUpdatesNotificationReceivedEvent();
          setUpdatesNotification(updates, startId);
        })
        .subscribe(__ -> {
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
                    downloadList.add(downloadFactory.create(update, false));
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
          AptoideUtils.StringU.getFormattedString(R.string.has_updates, getResources(),
              getString(R.string.app_name));
      CharSequence contentTitle = getString(R.string.app_name);
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
      notificationAnalytics.sendUpdatesNotificationImpressionEvent();
      managerNotification.notify(UPDATE_NOTIFICATION_ID, notification);
      ManagerPreferences.setLastUpdates(numberUpdates, sharedPreferences);
    }
    stopSelf(startId);
  }
}
