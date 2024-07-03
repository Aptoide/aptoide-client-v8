package cm.aptoide.pt.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.BaseService;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.room.RoomDownload;
import cm.aptoide.pt.database.room.RoomUpdate;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.updates.UpdateRepository;
import java.util.ArrayList;
import java.util.Collections;
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
  @Inject @Named("marketName") String marketName;
  @Inject DownloadFactory downloadFactory;
  @Inject UpdateRepository updateRepository;
  private AptoideApplication application;
  private CompositeSubscription subscriptions;
  private InstallManager installManager;
  private SharedPreferences sharedPreferences;

  public void setAlarm(AlarmManager am, Context context, String action, long time) {
    Intent intent = new Intent(context, PullingContentService.class);
    intent.setAction(action);
    PendingIntent pendingIntent =
        PendingIntent.getService(context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE);
    am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, getElapsedRealtimeTrigger(time),
        getElapsedRealtimeTrigger(time), pendingIntent);
  }

  @Override public void onCreate() {
    super.onCreate();
    getApplicationComponent().inject(this);
    application = (AptoideApplication) getApplicationContext();
    sharedPreferences = application.getDefaultSharedPreferences();
    installManager = application.getInstallManager();
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
    return (PendingIntent.getService(context, 0, intent,
        PendingIntent.FLAG_IMMUTABLE) != null);
  }

  private long getElapsedRealtimeTrigger(long trigger) {
    return SystemClock.elapsedRealtime() + trigger;
  }

  /**
   * Setup on updates action received
   *
   * @param startId service startid
   */
  private void setUpdatesAction(int startId) {
    subscriptions.add(updateRepository.getAll(false)
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
        .doOnNext(updates -> stopSelf(startId))
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
  private Observable<Boolean> autoUpdate(List<RoomUpdate> updateList) {
    return Observable.just(ManagerPreferences.isAutoUpdateEnable(sharedPreferences)
            && ManagerPreferences.allowRootInstallation(sharedPreferences))
        .flatMap(shouldAutoUpdateRun -> {
          if (shouldAutoUpdateRun) {
            return Observable.just(updateList)
                .observeOn(Schedulers.io())
                .map(updates -> {
                  ArrayList<RoomDownload> downloadList = new ArrayList<>(updates.size());
                  for (RoomUpdate update : updates) {
                    downloadList.add(
                        downloadFactory.create(update, false, Collections.emptyList()));
                  }
                  return downloadList;
                })
                .flatMap(downloads -> installManager.startInstalls(downloads));
          } else {
            return Observable.just(false);
          }
        });
  }
}
