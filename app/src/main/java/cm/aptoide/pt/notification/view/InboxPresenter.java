package cm.aptoide.pt.notification.view;

import cm.aptoide.pt.PageViewsAnalytics;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.link.LinksHandlerFactory;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.notification.NotificationCenter;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class InboxPresenter implements Presenter {

  private final InboxView view;
  private final NotificationCenter notificationCenter;
  private final LinksHandlerFactory linkFactory;
  private final NotificationAnalytics analytics;
  private final PageViewsAnalytics pageViewsAnalytics;
  private final CrashReport crashReport;
  private final NavigationTracker navigationTracker;
  private final int NUMBER_OF_NOTIFICATIONS = 50;

  public InboxPresenter(InboxView view, NotificationCenter notificationCenter,
      LinksHandlerFactory linkFactory, CrashReport crashReport, NavigationTracker navigationTracker,
      NotificationAnalytics analytics, PageViewsAnalytics pageViewsAnalytics) {
    this.view = view;
    this.notificationCenter = notificationCenter;
    this.linkFactory = linkFactory;
    this.crashReport = crashReport;
    this.navigationTracker = navigationTracker;
    this.analytics = analytics;
    this.pageViewsAnalytics = pageViewsAnalytics;
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> notificationCenter.getInboxNotifications(NUMBER_OF_NOTIFICATIONS))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(notifications -> view.showNotifications(notifications))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notifications -> {
        }, throwable -> crashReport.log(throwable));
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.notificationSelection()
            .flatMap(notification -> Observable.just(
                linkFactory.get(LinksHandlerFactory.NOTIFICATION_LINK, notification.getUrl()))
                .doOnNext(link -> link.launch())
                .doOnNext(link -> analytics.notificationShown(
                    notification.getNotificationCenterUrlTrack()))
                .doOnNext(link -> navigationTracker.registerScreen(
                    ScreenTagHistory.Builder.build(this.getClass()
                        .getSimpleName())))
                .doOnNext(link -> pageViewsAnalytics.sendPageViewedEvent())))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, throwable -> crashReport.log(throwable));

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .first()
        .flatMapCompletable(create -> notificationCenter.setAllNotificationsRead())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, throwable -> crashReport.log(throwable));
  }
}
