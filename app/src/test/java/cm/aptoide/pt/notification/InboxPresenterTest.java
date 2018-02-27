package cm.aptoide.pt.notification;

import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.notification.view.InboxFragment;
import cm.aptoide.pt.notification.view.InboxNavigator;
import cm.aptoide.pt.notification.view.InboxPresenter;
import cm.aptoide.pt.presenter.View;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by franciscocalado on 26/02/18.
 */

public class InboxPresenterTest {

  private static int NUMBER_OF_NOTIFICATIONS_TEST = 50;

  @Mock private InboxFragment view;
  @Mock private InboxNavigator navigator;
  @Mock private NotificationCenter notificationCenter;
  @Mock private NotificationAnalytics analytics;
  @Mock private CrashReport crashReport;
  @Mock private NavigationTracker tracker;

  private InboxPresenter presenter;
  private PublishSubject<View.LifecycleEvent> lifecycleEvent;
  private List<AptoideNotification> notifications;
  private AptoideNotification notification;

  @Before public void setupInboxPresenter() {
    MockitoAnnotations.initMocks(this);

    notifications = new ArrayList<>();
    lifecycleEvent = PublishSubject.create();
    presenter =
        new InboxPresenter(view, navigator, notificationCenter, crashReport, tracker, analytics,
            Schedulers.immediate());

    notification = new AptoideNotification("Image", "Title", "URL", "URLTrack", "Graphic",
        AptoideNotification.LIKE, 0, 100, "my_id");
    AptoideNotification notification2 =
        new AptoideNotification("Image2", "Title2", "URL2", "URLTrack2", "Graphic2",
            AptoideNotification.CAMPAIGN, 4, 150, "my_id2");

    notifications.add(notification);
    notifications.add(notification2);

    when(view.getLifecycle()).thenReturn(lifecycleEvent);
  }

  @Test public void showInboxNotifications() {
    //Given an initialized InboxPresenter, once the OnCreate() method is called on the view
    //get a list of notifications and display them
    when(notificationCenter.getInboxNotifications(NUMBER_OF_NOTIFICATIONS_TEST)).thenReturn(
        Observable.just(notifications));
    presenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(view).showNotifications(notifications);
  }

  @Test public void navigateToNotification() {
    //Given an initialized InboxPresenter, once the user clicks a notification
    //they navigate to that notification view
    when(view.notificationSelection()).thenReturn(Observable.just(notification));
    presenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    verify(navigator).navigateToNotification(notification);
    verify(analytics).sendNotificationTouchEvent(notification.getNotificationCenterUrlTrack());
    verify(tracker).registerScreen(any(ScreenTagHistory.class));
  }

  @Test public void markNotificationsAsRead() {
    //Given an initialized InboxPresenter, once the onCreate() method is called on the view
    //mark all notifications as read
    presenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    verify(notificationCenter).setAllNotificationsRead();
  }
}
