package cm.aptoide.pt;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.notification.NotificationIdsMapper;
import cm.aptoide.pt.notification.NotificationInfo;
import cm.aptoide.pt.notification.NotificationReceiver;
import cm.aptoide.pt.notification.SystemNotificationShower;
import cm.aptoide.pt.presenter.NotificationView;
import cm.aptoide.pt.presenter.Presenter;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by pedroribeiro on 20/11/17.
 */

public abstract class NotificationApplicationView extends AptoideApplication
    implements NotificationView {

  private BehaviorSubject<LifecycleEvent> lifecycleEventBehaviorSubject;
  private SystemNotificationShower systemNotificationShower;

  @Override public void onCreate() {
    super.onCreate();
    lifecycleEventBehaviorSubject = BehaviorSubject.create();
    lifecycleEventBehaviorSubject.onNext(LifecycleEvent.CREATE);
    attachPresenter(getSystemNotificationShower());
  }

  @NonNull @Override protected SystemNotificationShower getSystemNotificationShower() {
    if (systemNotificationShower == null) {
      systemNotificationShower = new SystemNotificationShower(this,
          (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE),
          new NotificationIdsMapper(), getNotificationCenter(), getNotificationAnalytics(),
          CrashReport.getInstance(), getNotificationProvider(), this, new CompositeSubscription(),
          getNavigationTracker());
    }
    return systemNotificationShower;
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
}
