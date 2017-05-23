package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.link.LinksHandlerFactory;
import cm.aptoide.pt.v8engine.notification.NotificationCenter;
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

  public MyAccountPresenter(MyAccountView view, AptoideAccountManager accountManager,
      CrashReport crashReport, MyAccountNavigator navigator, NotificationCenter notificationCenter,
      LinksHandlerFactory linkFactory) {
    this.view = view;
    this.accountManager = accountManager;
    this.crashReport = crashReport;
    this.navigator = navigator;
    this.notificationCenter = notificationCenter;
    this.linkFactory = linkFactory;
  }

  @Override public void present() {
    //todo(pribeiro): error handling
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(resumed -> signOutClick())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(signOutClick -> {
        }, throwable -> crashReport.log(throwable));
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(resumed -> view.moreNotificationsClick()
            .doOnNext(clicked -> navigator.navigateToInboxView()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(moreClick -> {
        }, throwable -> crashReport.log(throwable));
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
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> notificationCenter.getInboxNotifications(NUMBER_OF_NOTIFICATIONS))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(notifications -> view.showNotifications(notifications))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notifications -> {
        }, throwable -> crashReport.log(throwable));
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> view.notificationSelection())
        .map(notification -> linkFactory.get(LinksHandlerFactory.NOTIFICATION_LINK,
            notification.getUrl()))
        .doOnNext(link -> link.launch())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, throwable -> crashReport.log(throwable));
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> view.editUserProfileClick()
            .flatMap(click -> accountManager.accountStatus())
            .doOnNext(account -> navigator.navigateToEditProfileView(account)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(account -> {
        }, throwable -> crashReport.log(throwable));
  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
  }

  private Observable<Void> signOutClick() {
    return view.signOutClick()
        .flatMap(click -> accountManager.logout()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnCompleted(() -> {
              ManagerPreferences.setAddressBookSyncValues(false);
              view.navigateToHome();
            })
            .doOnError(throwable -> crashReport.log(throwable)).<Void>toObservable())
        .retry();
  }
}
