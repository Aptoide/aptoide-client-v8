package cm.aptoide.pt.notification.view;

import android.os.Bundle;
import cm.aptoide.pt.analytics.AptoideNavigationTracker;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.link.LinksHandlerFactory;
import cm.aptoide.pt.notification.NotificationCenter;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by pedroribeiro on 16/05/17.
 */

public class InboxPresenter implements Presenter {

  private final InboxView view;
  private final NotificationCenter notificationCenter;
  private final LinksHandlerFactory linkFactory;
  private CrashReport crashReport;
  private AptoideNavigationTracker aptoideNavigationTracker;
  private int NUMBER_OF_NOTIFICATIONS = 50;

  public InboxPresenter(InboxView view, NotificationCenter notificationCenter,
      LinksHandlerFactory linkFactory, CrashReport crashReport,
      AptoideNavigationTracker aptoideNavigationTracker) {
    this.view = view;
    this.notificationCenter = notificationCenter;
    this.linkFactory = linkFactory;
    this.crashReport = crashReport;
    this.aptoideNavigationTracker = aptoideNavigationTracker;
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
        .flatMap(__ -> view.notificationSelection())
        .map(notification -> linkFactory.get(LinksHandlerFactory.NOTIFICATION_LINK,
            notification.getUrl()))
        .doOnNext(link -> link.launch())
        .doOnNext(__ -> aptoideNavigationTracker.registerView("Notification"))
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

  @Override public void saveState(Bundle state) {
  }

  @Override public void restoreState(Bundle state) {
  }
}
