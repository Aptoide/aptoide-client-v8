package cm.aptoide.pt.account.view;

import android.content.SharedPreferences;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.notification.NotificationCenter;
import cm.aptoide.pt.preferences.managed.ManagedKeys;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Observable;
import rx.Scheduler;

public class MyAccountPresenter implements Presenter {

  public static final int EDIT_STORE_REQUEST_CODE = 1230;
  private final MyAccountView view;
  private final AptoideAccountManager accountManager;
  private final CrashReport crashReport;
  private final MyAccountNavigator navigator;
  private final NotificationCenter notificationCenter;
  private final int NUMBER_OF_NOTIFICATIONS = 3;
  private final SharedPreferences sharedPreferences;
  private final NotificationAnalytics analytics;
  private final NavigationTracker navigationTracker;
  private Scheduler viewScheduler;

  public MyAccountPresenter(MyAccountView view, AptoideAccountManager accountManager,
      CrashReport crashReport, MyAccountNavigator navigator, NotificationCenter notificationCenter,
      SharedPreferences sharedPreferences, NavigationTracker navigationTracker,
      NotificationAnalytics analytics, Scheduler viewScheduler) {
    this.view = view;
    this.accountManager = accountManager;
    this.crashReport = crashReport;
    this.navigator = navigator;
    this.notificationCenter = notificationCenter;
    this.sharedPreferences = sharedPreferences;
    this.navigationTracker = navigationTracker;
    this.analytics = analytics;
    this.viewScheduler = viewScheduler;
  }

  @Override public void present() {
    showAndPopulateAccountViews();
    handleSignOutButtonClick();
    handleEditStoreBackNavigation();
    handleMoreNotificationsClick();
    handleEditStoreClick();
    handleHeaderVisibility();
    handleGetNotifications();
    handleNotificationClick();
    handleUserEditClick();
    handleUserLayoutClick();
    handleStoreLayoutClick();
    checkIfStoreIsInvalidAndRefresh();
    markNotificationsRead();
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
        .observeOn(viewScheduler)
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

  private void handleEditStoreClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(click -> view.editStoreClick()
            .flatMap(response -> view.getStore())
            .map(getStore -> getStore.getNodes()
                .getMeta()
                .getData()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(store -> navigator.navigateToEditStoreView(store, EDIT_STORE_REQUEST_CODE),
            throwable -> crashReport.log(throwable));
  }

  private void handleEditStoreBackNavigation() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .flatMap(__ -> navigator.editStoreResult(EDIT_STORE_REQUEST_CODE))
        .flatMap(__ -> accountManager.accountStatus()
            .first())
        .observeOn(viewScheduler)
        .doOnNext(account -> view.showAccount(account))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notification -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleHeaderVisibility() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> notificationCenter.haveNotifications())
        .observeOn(viewScheduler)
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

  private void handleGetNotifications() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> notificationCenter.haveNotifications())
        .filter(haveNotifications -> haveNotifications)
        .flatMap(viewCreated -> notificationCenter.getInboxNotifications(NUMBER_OF_NOTIFICATIONS))
        .observeOn(viewScheduler)
        .doOnNext(notifications -> view.updateAdapter(notifications))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notifications -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleNotificationClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> view.notificationSelection())
        .doOnNext(notification -> {
          navigator.navigateToNotification(notification);
          analytics.sendNotificationTouchEvent(notification.getNotificationCenterUrlTrack());
          navigationTracker.registerScreen(ScreenTagHistory.Builder.build("Notification"));
        })
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

  private void handleUserLayoutClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.userLayoutClick())
        .flatMap(click -> accountManager.accountStatus()
            .first())
        .doOnNext(account -> navigator.navigateToUserView(account.getId(), account.getStore()
            .getTheme()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(account -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleStoreLayoutClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.storeLayoutClick())
        .flatMap(click -> accountManager.accountStatus()
            .first())
        .doOnNext(account -> navigator.navigateToStoreView(account.getStore()
            .getName(), account.getStore()
            .getTheme()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(account -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void checkIfStoreIsInvalidAndRefresh() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(lifecycleEvent -> accountManager.accountStatus())
        .filter(account -> !storeExistsInAccount(account))
        .flatMap(account -> view.getStore()
            .observeOn(viewScheduler)
            .map(store -> store.getNodes()
                .getMeta()
                .getData())
            .doOnNext(store -> view.refreshUI(store)))
        .flatMap(__ -> accountManager.updateAccount()
            .toObservable())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(account -> {
        }, throwable -> CrashReport.getInstance()
            .log(throwable));
  }

  private Observable<Void> signOutClick() {
    return view.signOutClick()
        .flatMap(click -> accountManager.logout()
            .observeOn(viewScheduler)
            .doOnCompleted(() -> {
              resetAddressBookValues();
              navigator.navigateToHome();
            })
            .doOnError(throwable -> crashReport.log(throwable)).<Void>toObservable())
        .retry();
  }

  private void resetAddressBookValues() {
    sharedPreferences.edit()
        .putBoolean(ManagedKeys.ADDRESS_BOOK_SYNC, false)
        .putBoolean(ManagedKeys.TWITTER_SYNC, false)
        .putBoolean(ManagedKeys.FACEBOOK_SYNC, false)
        .apply();
  }

  private boolean storeExistsInAccount(Account account) {
    return account.getStore()
        .getId() != 0;
  }
}
