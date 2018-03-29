package cm.aptoide.pt.view.settings;

import android.content.SharedPreferences;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.preferences.managed.ManagedKeys;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Observable;
import rx.Scheduler;

/**
 * Created by franciscocalado on 13/03/18.
 */

public class NewAccountPresenter implements Presenter {

  private static final int EDIT_STORE_REQUEST_CODE = 1230;

  private final NewAccountView view;
  private final AptoideAccountManager accountManager;
  private final CrashReport crashReport;
  private final SharedPreferences sharedPreferences;
  private final Scheduler scheduler;
  private final NewAccountNavigator newAccountNavigator;
  private final AccountAnalytics accountAnalytics;

  public NewAccountPresenter(NewAccountView view, AptoideAccountManager accountManager,
      CrashReport crashReport, SharedPreferences sharedPreferences, Scheduler scheduler,
      NewAccountNavigator newAccountNavigator, AccountAnalytics accountAnalytics) {
    this.view = view;
    this.accountManager = accountManager;
    this.crashReport = crashReport;
    this.sharedPreferences = sharedPreferences;
    this.scheduler = scheduler;
    this.newAccountNavigator = newAccountNavigator;
    this.accountAnalytics = accountAnalytics;
  }

  @Override public void present() {
    populateAccountViews();
    checkIfStoreIsInvalidAndRefresh();
    handleLoginClick();
    handleLogOutClick();
    handleCreateStoreClick();
    handleFindFriendsClick();
    handleStoreEditClick();
    handleStoreEditResult();
    handleStoreDisplayableClick();
    handleProfileEditClick();
    handleProfileDisplayableClick();
    handleSettingsClicked();
    handleNotificationHistoryClicked();
  }

  private void handleLoginClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.loginClick())
        .doOnNext(loginClicked -> newAccountNavigator.navigateToLoginView(
            AccountAnalytics.AccountOrigins.MY_ACCOUNT))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void populateAccountViews() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(resumed -> accountManager.accountStatus()
            .first())
        .observeOn(scheduler)
        .doOnNext(account -> view.showAccount(account))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleProfileDisplayableClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.userClick())
        .flatMap(click -> accountManager.accountStatus()
            .first())
        .doOnNext(account -> newAccountNavigator.navigateToUserView(account.getId(),
            account.getStore()
                .getTheme()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(account -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleProfileEditClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> view.editUserProfileClick()
            .flatMap(click -> accountManager.accountStatus()
                .first())
            .doOnNext(account -> newAccountNavigator.navigateToEditProfileView()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(account -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleStoreDisplayableClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.storeClick())
        .flatMap(click -> accountManager.accountStatus()
            .first())
        .doOnNext(account -> newAccountNavigator.navigateToStoreView(account.getStore()
            .getName(), account.getStore()
            .getTheme()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(account -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleStoreEditClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(click -> view.editStoreClick()
            .flatMap(response -> view.getStore())
            .map(getStore -> getStore.getNodes()
                .getMeta()
                .getData()))
        .observeOn(scheduler)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(
            store -> newAccountNavigator.navigateToEditStoreView(store, EDIT_STORE_REQUEST_CODE),
            throwable -> crashReport.log(throwable));
  }

  private void handleStoreEditResult() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .flatMap(__ -> newAccountNavigator.editStoreResult(EDIT_STORE_REQUEST_CODE))
        .flatMap(__ -> accountManager.accountStatus()
            .first())
        .observeOn(scheduler)
        .doOnNext(account -> view.showAccount(account))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notification -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleFindFriendsClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.findFriendsClick())
        .doOnNext(__ -> {
          accountAnalytics.sendFollowFriendsClickEvent();
          newAccountNavigator.navigateToFindFriends();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notification -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleCreateStoreClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.createStoreClick())
        .doOnNext(__ -> newAccountNavigator.navigateToCreateStore())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notification -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void checkIfStoreIsInvalidAndRefresh() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(lifecycleEvent -> accountManager.accountStatus())
        .filter(account -> !storeExistsInAccount(account) && account.hasStore())
        .flatMap(account -> view.getStore()
            .map(store -> store.getNodes()
                .getMeta()
                .getData())
            .observeOn(scheduler)
            .doOnNext(store -> view.refreshUI(store)))
        .flatMap(__ -> accountManager.updateAccount()
            .toObservable())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(account -> {
        }, throwable -> CrashReport.getInstance()
            .log(throwable));
  }

  private void handleLogOutClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(resumed -> signOutClick())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(signOutClick -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleSettingsClicked() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.settingsClicked())
        .doOnNext(__ -> newAccountNavigator.navigateToSettings())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleNotificationHistoryClicked() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.notificationsClicked())
        .doOnNext(__ -> newAccountNavigator.navigateToNotificationHistory())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private Observable<Void> signOutClick() {
    return view.signOutClick()
        .flatMap(click -> accountManager.logout()
            .observeOn(scheduler)
            .doOnCompleted(() -> {
              resetAddressBookValues();
              view.showLoginAccountDisplayable();
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
