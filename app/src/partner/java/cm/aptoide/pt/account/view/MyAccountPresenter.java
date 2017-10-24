package cm.aptoide.pt.account.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.PageViewsAnalytics;
import cm.aptoide.pt.analytics.AptoideNavigationTracker;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.link.LinksHandlerFactory;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.notification.NotificationCenter;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class MyAccountPresenter implements Presenter {

  private final MyAccountView view;
  private final AptoideAccountManager accountManager;
  private final CrashReport crashReport;
  private final MyAccountNavigator navigator;
  private final NotificationCenter notificationCenter;
  private final LinksHandlerFactory linkFactory;
  private final int NUMBER_OF_NOTIFICATIONS = 3;
  private final SharedPreferences sharedPreferences;
  private final NotificationAnalytics analytics;
  private PageViewsAnalytics pageViewsAnalytics;
  private AptoideNavigationTracker aptoideNavigationTracker;

  public MyAccountPresenter(MyAccountView view, AptoideAccountManager accountManager,
      CrashReport crashReport, MyAccountNavigator navigator, NotificationCenter notificationCenter,
      LinksHandlerFactory linkFactory, SharedPreferences sharedPreferences,
      AptoideNavigationTracker aptoideNavigationTracker, NotificationAnalytics analytics,
      PageViewsAnalytics pageViewsAnalytics) {
    this.view = view;
    this.accountManager = accountManager;
    this.crashReport = crashReport;
    this.navigator = navigator;
    this.notificationCenter = notificationCenter;
    this.linkFactory = linkFactory;
    this.sharedPreferences = sharedPreferences;
    this.aptoideNavigationTracker = aptoideNavigationTracker;
    this.analytics = analytics;
    this.pageViewsAnalytics = pageViewsAnalytics;
  }

  @Override public void present() {
    showAndPopulateAccountViews();
    handleSignOutButtonClick();
    handleMoreNotificationsClick();
    handleHeaderVisibility();
    hangleGetNotifications();
    handleNotificationClick();
    handleUserEditClick();
    markNotificationsRead();
  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
  }

  private void markNotificationsRead() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .first()
        .flatMapCompletable(create -> notificationCenter.setAllNotificationsRead())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void showAndPopulateAccountViews() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(resumed -> accountManager.accountStatus()
            .first())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(account -> view.showAccount(account))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleSignOutButtonClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(resumed -> signOutClick())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(signOutClick -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleMoreNotificationsClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(resumed -> view.moreNotificationsClick()
            .doOnNext(clicked -> navigator.navigateToInboxView()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(moreClick -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleHeaderVisibility() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> notificationCenter.haveNotifications())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(hasNotifications -> {
          if (hasNotifications) {
            view.showHeader();
          } else {
            view.hideHeader();
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notification -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void hangleGetNotifications() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> notificationCenter.getInboxNotifications(NUMBER_OF_NOTIFICATIONS))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(notifications -> view.showNotifications(notifications))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notifications -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleNotificationClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> view.notificationSelection())
        .flatMap(notification -> Observable.just(
            linkFactory.get(LinksHandlerFactory.NOTIFICATION_LINK, notification.getUrl()))
            .doOnNext(link -> link.launch())
            .doOnNext(
                link -> analytics.notificationShown(notification.getNotificationCenterUrlTrack()))
            .doOnNext(__ -> aptoideNavigationTracker.registerView("Notification"))
            .doOnNext(__ -> pageViewsAnalytics.sendPageViewedEvent()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleUserEditClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> view.editUserProfileClick()
            .flatMap(click -> accountManager.accountStatus())
            .doOnNext(account -> navigator.navigateToEditProfileView()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(account -> {
        }, throwable -> crashReport.log(throwable));
  }

  private Observable<Void> signOutClick() {
    return view.signOutClick()
        .flatMap(click -> accountManager.logout()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnCompleted(() -> {
              ManagerPreferences.setAddressBookSyncValues(false, sharedPreferences);
              view.navigateToHome();
            })
            .doOnError(throwable -> crashReport.log(throwable)).<Void>toObservable())
        .retry();
  }
}
