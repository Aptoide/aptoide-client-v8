package cm.aptoide.pt.notification.view;

import cm.aptoide.pt.PageViewsAnalytics;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.notification.NotificationCenter;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Scheduler;

public class InboxPresenter implements Presenter {

  private final InboxView view;
  private final InboxNavigator inboxNavigator;
  private final NotificationCenter notificationCenter;
  private final NotificationAnalytics analytics;
  private final PageViewsAnalytics pageViewsAnalytics;
  private final CrashReport crashReport;
  private final NavigationTracker navigationTracker;
  private final int NUMBER_OF_NOTIFICATIONS = 50;
  private final Scheduler viewScheduler;

  public InboxPresenter(InboxView view, InboxNavigator inboxNavigator,
      NotificationCenter notificationCenter, CrashReport crashReport,
      NavigationTracker navigationTracker, NotificationAnalytics analytics,
      PageViewsAnalytics pageViewsAnalytics, Scheduler viewScheduler) {
    this.view = view;
    this.inboxNavigator = inboxNavigator;
    this.notificationCenter = notificationCenter;
    this.crashReport = crashReport;
    this.navigationTracker = navigationTracker;
    this.analytics = analytics;
    this.pageViewsAnalytics = pageViewsAnalytics;
    this.viewScheduler = viewScheduler;
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> notificationCenter.getInboxNotifications(NUMBER_OF_NOTIFICATIONS))
        .observeOn(viewScheduler)
        .doOnNext(notifications -> view.showNotifications(notifications))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notifications -> {
        }, throwable -> crashReport.log(throwable));

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.notificationSelection())
        .doOnNext(notification -> {
          inboxNavigator.navigateToNotification(notification);
          analytics.sendNotificationTouchEvent(notification.getNotificationCenterUrlTrack());
          navigationTracker.registerScreen(ScreenTagHistory.Builder.build(this.getClass()
              .getSimpleName()));
          pageViewsAnalytics.sendPageViewedEvent();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, throwable -> crashReport.log(throwable));

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMapCompletable(create -> notificationCenter.setAllNotificationsRead())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, throwable -> crashReport.log(throwable));
  }
}
