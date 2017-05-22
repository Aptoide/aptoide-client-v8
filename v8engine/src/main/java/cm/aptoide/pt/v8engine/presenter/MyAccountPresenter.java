package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.link.LinksHandlerFactory;
import cm.aptoide.pt.v8engine.notification.NotificationCenter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyAccountPresenter implements Presenter {

  private final MyAccountView view;
  private final AptoideAccountManager accountManager;
  private final CrashReport crashReport;
  private final MyAccountNavigator navigator;
  private final NotificationCenter notificationCenter;
  private final LinksHandlerFactory linkFactory;
  private final int NUMBER_OF_NOTIFICATIONS = 3;
  private Store store;

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
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(resumed -> signOutClick())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(resumed -> view.moreNotificationsClick()
            .doOnNext(__ -> navigator.navigateToInboxView()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(click -> view.editStoreClick()
            .flatMap(response -> view.getStore())
            .observeOn(Schedulers.io())
            .map(getStore -> {
              store = getStore.getNodes()
                  .getMeta()
                  .getData();
              return store;
            })
            .doOnNext(__ -> navigator.navigateToEditStoreView(store)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
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
