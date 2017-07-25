package cm.aptoide.pt.v8engine.view.account;

import android.content.SharedPreferences;
import android.os.Bundle;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.link.LinksHandlerFactory;
import cm.aptoide.pt.v8engine.notification.NotificationCenter;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
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

  public MyAccountPresenter(MyAccountView view, AptoideAccountManager accountManager,
      CrashReport crashReport, MyAccountNavigator navigator, NotificationCenter notificationCenter,
      LinksHandlerFactory linkFactory, SharedPreferences sharedPreferences) {
    this.view = view;
    this.accountManager = accountManager;
    this.crashReport = crashReport;
    this.navigator = navigator;
    this.notificationCenter = notificationCenter;
    this.linkFactory = linkFactory;
    this.sharedPreferences = sharedPreferences;
  }

  @Override public void present() {
    showAndPopulateAccountViews();
    handleSignOutButtonClick();
    handleMoreNotificationsClick();
    handleEditStoreClick();
    handleHeaderVisibility();
    hangleGetNotifications();
    handleNotificationClick();
    handleUserEditClick();
    handleUserLayoutClick();
    handleStoreLayoutClick();
    checkIfStoreIsInvalidAndRefresh();
  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
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

  private void handleEditStoreClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(click -> view.editStoreClick()
            .flatMap(response -> view.getStore())
            .map(getStore -> getStore.getNodes()
                .getMeta()
                .getData()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(store -> navigator.navigateToEditStoreView(store),
            throwable -> crashReport.log(throwable));
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
        .map(notification -> linkFactory.get(LinksHandlerFactory.NOTIFICATION_LINK,
            notification.getUrl()))
        .doOnNext(link -> link.launch())
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
        .flatMap(__ -> view.userClick())
        .flatMap(click -> accountManager.accountStatus()
            .first())
        .doOnNext(account -> navigateToUser(account.getId(), account.getStore()
            .getTheme()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(account -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleStoreLayoutClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.storeClick())
        .flatMap(click -> accountManager.accountStatus()
            .first())
        .doOnNext(account -> navigateToStore(account.getStore()
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
        .filter(account -> account.getStore() //checking if a store exists in account manager
            .getId() == 0)
        .flatMap(account -> view.getStore()
            .observeOn(AndroidSchedulers.mainThread())
            .map(store -> store.getNodes()
                .getMeta()
                .getData())
            .doOnNext(store -> view.refreshUI(store)))
        .flatMap(__ -> accountManager.syncCurrentAccount()
            .toObservable())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(account -> {
        }, throwable -> CrashReport.getInstance()
            .log(throwable));
  }

  private void navigateToUser(String id, String storeTheme) {
    navigator.navigateToUserView(id, storeTheme);
  }

  private void navigateToStore(String storeName, String storeTheme) {
    navigator.navigateToStoreView(storeName, storeTheme);
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
