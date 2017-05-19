package cm.aptoide.pt.v8engine.notification.view;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.link.LinksHandlerFactory;
import cm.aptoide.pt.v8engine.notification.NotificationCenter;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by pedroribeiro on 16/05/17.
 */

public class InboxPresenter implements Presenter {

  private final InboxView view;
  private final NotificationCenter notificationCenter;
  private final LinksHandlerFactory linkFactory;
  private int NUMBER_OF_NOTIFICATIONS = 50;

  public InboxPresenter(InboxView view, NotificationCenter notificationCenter,
      LinksHandlerFactory linkFactory) {
    this.view = view;
    this.notificationCenter = notificationCenter;
    this.linkFactory = linkFactory;
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> notificationCenter.getInboxNotifications(NUMBER_OF_NOTIFICATIONS))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(notifications -> view.showNotifications(notifications))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.notificationSelection())
        .map(notification -> linkFactory.get(LinksHandlerFactory.NOTIFICATION_LINK,
            notification.getUrl()))
        .doOnNext(link -> link.launch())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  @Override public void saveState(Bundle state) {
  }

  @Override public void restoreState(Bundle state) {
  }
}
