package cm.aptoide.pt.view.settings;

import android.content.SharedPreferences;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.MyAccountNavigator;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.preferences.managed.ManagedKeys;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Observable;
import rx.Scheduler;

/**
 * Created by franciscocalado on 13/03/18.
 */

public class NewSettingsPresenter implements Presenter {

  public static final int EDIT_STORE_REQUEST_CODE = 1230;

  private final NewSettingsView view;
  private final AptoideAccountManager accountManager;
  private final CrashReport crashReport;
  private final MyAccountNavigator navigator;
  private final SharedPreferences sharedPreferences;
  private final NavigationTracker navigationTracker;
  private final Scheduler scheduler;
  private final NewSettingsNavigator newSettingsNavigator;

  public NewSettingsPresenter(NewSettingsView view, AptoideAccountManager accountManager,
      CrashReport crashReport, MyAccountNavigator accountNavigator,
      SharedPreferences sharedPreferences, NavigationTracker navigationTracker, Scheduler scheduler,
      NewSettingsNavigator newSettingsNavigator) {
    this.view = view;
    this.accountManager = accountManager;
    this.crashReport = crashReport;
    navigator = accountNavigator;
    this.sharedPreferences = sharedPreferences;
    this.navigationTracker = navigationTracker;
    this.scheduler = scheduler;
    this.newSettingsNavigator = newSettingsNavigator;
  }

  @Override public void present() {
    //Account handlers
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
  }

  private void handleLoginClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.loginClick())
        .doOnNext(loginClicked -> newSettingsNavigator.navigateToLoginView(
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
        .doOnNext(account -> navigator.navigateToUserView(account.getId(), account.getStore()
            .getTheme()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(account -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleProfileEditClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> view.editUserProfileClick()
            .flatMap(click -> accountManager.accountStatus())
            .doOnNext(account -> navigator.navigateToEditProfileView()))
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
        .doOnNext(account -> navigator.navigateToStoreView(account.getStore()
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
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(store -> navigator.navigateToEditStoreView(store, EDIT_STORE_REQUEST_CODE),
            throwable -> crashReport.log(throwable));
  }

  private void handleStoreEditResult() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .flatMap(__ -> navigator.editStoreResult(EDIT_STORE_REQUEST_CODE))
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
        .doOnNext(__ -> newSettingsNavigator.navigateToFindFriends())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notification -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleCreateStoreClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.createStoreClick())
        .doOnNext(__ -> newSettingsNavigator.navigateToCreateStore())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notification -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void checkIfStoreIsInvalidAndRefresh() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(lifecycleEvent -> accountManager.accountStatus())
        .filter(account -> !storeExistsInAccount(account))
        .flatMap(account -> view.getStore()
            .observeOn(scheduler)
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

  private void handleLogOutClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(resumed -> signOutClick())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(signOutClick -> {
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
