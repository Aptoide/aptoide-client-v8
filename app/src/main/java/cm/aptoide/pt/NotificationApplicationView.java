package cm.aptoide.pt;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.install.DownloadsNotification;
import cm.aptoide.pt.install.DownloadsNotificationsPresenter;
import cm.aptoide.pt.notification.NotificationIdsMapper;
import cm.aptoide.pt.notification.NotificationInfo;
import cm.aptoide.pt.notification.NotificationReceiver;
import cm.aptoide.pt.notification.SystemNotificationShower;
import cm.aptoide.pt.presenter.NotificationView;
import cm.aptoide.pt.presenter.Presenter;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import java.util.Locale;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by pedroribeiro on 20/11/17.
 */

public class NotificationApplicationView extends AptoideApplication
    implements NotificationView, DownloadsNotification {

  public static final String FILE_MD5_EXTRA = "APTOIDE_APPID_EXTRA";
  static public final int PROGRESS_MAX_VALUE = 100;
  private static final int NOTIFICATION_ID = 1;
  private final int PAUSE_DOWNLOAD_REQUEST_CODE = 111;
  private final int OPEN_DOWNLOAD_MANAGER_REQUEST_CODE = 222;
  private final int OPEN_APPVIEW_REQUEST_CODE = 333;
  private BehaviorSubject<LifecycleEvent> lifecycleEventBehaviorSubject;
  private SystemNotificationShower systemNotificationShower;
  private DownloadsNotificationsPresenter downloadsNotificationsPresenter;
  private Notification notification;
  private NotificationManager notificationManager;

  @Override public void onCreate() {
    super.onCreate();
    this.notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    lifecycleEventBehaviorSubject = BehaviorSubject.create();
    lifecycleEventBehaviorSubject.onNext(LifecycleEvent.CREATE);
    attachPresenter(getSystemNotificationShower());
    attachPresenter(getDownloadsNotificationsPresenter());
  }

  @NonNull @Override protected SystemNotificationShower getSystemNotificationShower() {
    if (systemNotificationShower == null) {
      systemNotificationShower =
          new SystemNotificationShower(this, notificationManager, new NotificationIdsMapper(),
              getNotificationCenter(), getNotificationAnalytics(), CrashReport.getInstance(),
              getNotificationProvider(), this, new CompositeSubscription(), getNavigationTracker());
    }
    return systemNotificationShower;
  }

  private DownloadsNotificationsPresenter getDownloadsNotificationsPresenter() {
    if (downloadsNotificationsPresenter == null) {
      downloadsNotificationsPresenter = new DownloadsNotificationsPresenter(this, installManager);
    }
    return downloadsNotificationsPresenter;
  }

  @Override public Observable<NotificationInfo> getNotificationClick() {
    return getNotificationsPublishRelay().filter(notificationInfo -> notificationInfo.getAction()
        .equals(NotificationReceiver.NOTIFICATION_PRESSED_ACTION));
  }

  @Override public Observable<NotificationInfo> getNotificationDismissed() {
    return getNotificationsPublishRelay().filter(notificationInfo -> notificationInfo.getAction()
        .equals(NotificationReceiver.NOTIFICATION_DISMISSED_ACTION));
  }

  @Override public Observable<NotificationInfo> getActionBootCompleted() {
    return getNotificationsPublishRelay().filter(notificationInfo -> notificationInfo.getAction()
        .equals(Intent.ACTION_BOOT_COMPLETED));
  }

  @NonNull @Override
  public <T> LifecycleTransformer<T> bindUntilEvent(@NonNull LifecycleEvent lifecycleEvent) {
    return RxLifecycle.bindUntilEvent(getLifecycleEvent(), lifecycleEvent);
  }

  @Override public Observable<LifecycleEvent> getLifecycleEvent() {
    return lifecycleEventBehaviorSubject;
  }

  @Override public void attachPresenter(Presenter presenter) {
    presenter.present();
  }

  @Override
  public void setupNotification(String md5, String appName, int progress, boolean isIndeterminate) {

    NotificationCompat.Action downloadManagerAction = getDownloadManagerAction(md5);
    PendingIntent appViewPendingIntent = getAppViewOpeningPendingIntent(md5);
    NotificationCompat.Action pauseAction = getPauseAction(md5);

    if (notification == null) {
      notification =
          buildNotification(appName, progress, isIndeterminate, pauseAction, downloadManagerAction,
              appViewPendingIntent);
    } else {
      long oldWhen = notification.when;
      notification =
          buildNotification(appName, progress, isIndeterminate, pauseAction, downloadManagerAction,
              appViewPendingIntent);
      notification.when = oldWhen;
    }

    notificationManager.notify(NOTIFICATION_ID, notification);
  }

  @Override public void removeNotificationAndStop() {
    notificationManager.cancel(NOTIFICATION_ID);
  }

  private PendingIntent getAppViewOpeningPendingIntent(String md5) {
    Intent intent = createDeeplinkingIntent();

    final Bundle bundle = new Bundle();

    bundle.putBoolean(DeepLinkIntentReceiver.DeepLinksTargets.APP_VIEW_FRAGMENT, true);
    bundle.putString(DeepLinkIntentReceiver.DeepLinksKeys.APP_MD5_KEY, md5);
    intent.putExtras(bundle);

    return PendingIntent.getActivity(this, OPEN_APPVIEW_REQUEST_CODE, intent,
        PendingIntent.FLAG_ONE_SHOT);
  }

  @NonNull private Intent createDeeplinkingIntent() {
    Intent intent = new Intent();
    intent.setClass(getApplicationContext(), AptoideApplication.getActivityProvider()
        .getMainActivityFragmentClass());
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    return intent;
  }

  private NotificationCompat.Action getAction(int icon, String title, PendingIntent pendingIntent) {
    return new NotificationCompat.Action(icon, title, pendingIntent);
  }

  private Notification buildNotification(String appName, int progress, boolean isIndeterminate,
      NotificationCompat.Action pauseAction, NotificationCompat.Action openDownloadManager,
      PendingIntent contentIntent) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    builder.setSmallIcon(android.R.drawable.stat_sys_download)
        .setContentTitle(String.format(Locale.ENGLISH,
            getResources().getString(cm.aptoide.pt.downloadmanager.R.string.aptoide_downloading),
            getString(R.string.app_name)))
        .setContentText(new StringBuilder().append(appName)
            .append(" - ")
            .append(getString(cm.aptoide.pt.database.R.string.download_progress)))
        .setContentIntent(contentIntent)
        .setProgress(PROGRESS_MAX_VALUE, progress, isIndeterminate)
        .addAction(pauseAction)
        .addAction(openDownloadManager);
    return builder.build();
  }

  @NonNull private NotificationCompat.Action getPauseAction(String md5) {
    Bundle appIdExtras = new Bundle();
    appIdExtras.putString(FILE_MD5_EXTRA, md5);
    return getAction(cm.aptoide.pt.downloadmanager.R.drawable.media_pause,
        getString(cm.aptoide.pt.downloadmanager.R.string.pause_download),
        getPausePendingIntent(md5));
  }

  private PendingIntent getPausePendingIntent(String md5) {
    Intent intent = createDeeplinkingIntent();
    intent.putExtra(DeepLinkIntentReceiver.DeepLinksTargets.PAUSE_FROM_DOWNLOAD_NOTIFICATION, true);
    intent.putExtra(DeepLinkIntentReceiver.DeepLinksKeys.APP_MD5_KEY, md5);
    return PendingIntent.getActivity(this, PAUSE_DOWNLOAD_REQUEST_CODE, intent,
        PendingIntent.FLAG_ONE_SHOT);
  }

  private PendingIntent getOpenDownloadManagerPendingIntent(int requestCode) {
    Intent intent = createDeeplinkingIntent();
    intent.putExtra(DeepLinkIntentReceiver.DeepLinksTargets.FROM_DOWNLOAD_NOTIFICATION, true);
    return PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
  }

  @NonNull private NotificationCompat.Action getDownloadManagerAction(String md5) {
    Bundle appIdExtras = new Bundle();
    appIdExtras.putString(FILE_MD5_EXTRA, md5);
    return getAction(R.drawable.ic_manager, getString(R.string.open_apps_manager),
        getOpenDownloadManagerPendingIntent(OPEN_DOWNLOAD_MANAGER_REQUEST_CODE));
  }
}
