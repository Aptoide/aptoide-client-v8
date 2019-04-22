package cm.aptoide.pt.view.settings;

import android.content.SharedPreferences;
import android.support.annotation.VisibleForTesting;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Observable;
import rx.Scheduler;

/**
 * Created by franciscocalado on 13/03/18.
 */

public class MyAccountPresenter implements Presenter {

  private static final int EDIT_STORE_REQUEST_CODE = 1230;

  private final MyAccountView view;
  private final AptoideAccountManager accountManager;
  private final CrashReport crashReport;
  private final SharedPreferences sharedPreferences;
  private final Scheduler scheduler;
  private final MyAccountNavigator myAccountNavigator;
  private final AccountAnalytics accountAnalytics;

  public MyAccountPresenter(MyAccountView view, AptoideAccountManager accountManager,
      CrashReport crashReport, SharedPreferences sharedPreferences, Scheduler scheduler,
      MyAccountNavigator myAccountNavigator, AccountAnalytics accountAnalytics) {
    this.view = view;
    this.accountManager = accountManager;
    this.crashReport = crashReport;
    this.sharedPreferences = sharedPreferences;
    this.scheduler = scheduler;
    this.myAccountNavigator = myAccountNavigator;
    this.accountAnalytics = accountAnalytics;
  }

  @Override public void present() {
    populateAccountViews();
    checkIfStoreIsInvalidAndRefresh();
    handleLoginClick();
    handleLogOutClick();
    handleCreateStoreClick();
    handleStoreEditClick();
    handleStoreEditResult();
    handleStoreDisplayableClick();
    handleProfileEditClick();
    handleProfileDisplayableClick();
    handleSettingsClicked();
    handleNotificationHistoryClicked();
    handleAptoideTvCardViewClick();
    handleAptoideUploaderCardViewClick();
    handleAptoideBackupCardViewClick();
  }

  @VisibleForTesting public void handleLoginClick() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.loginClick())
        .doOnNext(loginClicked -> myAccountNavigator.navigateToLoginView(
            AccountAnalytics.AccountOrigins.MY_ACCOUNT))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  @VisibleForTesting public void populateAccountViews() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(resumed -> accountManager.accountStatus()
            .first())
        .observeOn(scheduler)
        .doOnNext(account -> view.showAccount(account))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  @VisibleForTesting public void handleProfileDisplayableClick() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.userClick())
        .flatMap(click -> accountManager.accountStatus()
            .first())
        .doOnNext(account -> myAccountNavigator.navigateToUserView(account.getId(),
            account.getStore()
                .getTheme()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(account -> {
        }, throwable -> crashReport.log(throwable));
  }

  @VisibleForTesting public void handleProfileEditClick() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> view.editUserProfileClick()
            .flatMap(click -> accountManager.accountStatus()
                .first())
            .doOnNext(account -> myAccountNavigator.navigateToEditProfileView()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(account -> {
        }, throwable -> crashReport.log(throwable));
  }

  @VisibleForTesting public void handleStoreDisplayableClick() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.storeClick())
        .flatMap(click -> accountManager.accountStatus()
            .first())
        .doOnNext(account -> myAccountNavigator.navigateToStoreView(account.getStore()
            .getName(), account.getStore()
            .getTheme()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(account -> {
        }, throwable -> crashReport.log(throwable));
  }

  @VisibleForTesting public void handleStoreEditClick() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(click -> view.editStoreClick()
            .flatMap(response -> view.getStore())
            .map(getStore -> getStore.getNodes()
                .getMeta()
                .getData()))
        .observeOn(scheduler)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(
            store -> myAccountNavigator.navigateToEditStoreView(store, EDIT_STORE_REQUEST_CODE),
            throwable -> crashReport.log(throwable));
  }

  @VisibleForTesting public void handleStoreEditResult() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .flatMap(__ -> myAccountNavigator.editStoreResult(EDIT_STORE_REQUEST_CODE))
        .flatMap(__ -> accountManager.accountStatus()
            .first())
        .observeOn(scheduler)
        .doOnNext(account -> view.showAccount(account))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notification -> {
        }, throwable -> crashReport.log(throwable));
  }

  @VisibleForTesting public void handleCreateStoreClick() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.createStoreClick())
        .doOnNext(__ -> myAccountNavigator.navigateToCreateStore())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notification -> {
        }, throwable -> crashReport.log(throwable));
  }

  @VisibleForTesting public void checkIfStoreIsInvalidAndRefresh() {
    view.getLifecycleEvent()
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

  @VisibleForTesting public void handleLogOutClick() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(resumed -> signOutClick())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(signOutClick -> {
        }, throwable -> crashReport.log(throwable));
  }

  @VisibleForTesting public void handleSettingsClicked() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.settingsClicked())
        .doOnNext(__ -> myAccountNavigator.navigateToSettings())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  @VisibleForTesting public void handleNotificationHistoryClicked() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.notificationsClicked())
        .doOnNext(__ -> myAccountNavigator.navigateToNotificationHistory())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private Observable<Void> signOutClick() {
    return view.signOutClick()
        .flatMap(click -> accountManager.logout()
            .observeOn(scheduler)
            .doOnCompleted(() -> view.showLoginAccountDisplayable())
            .doOnError(throwable -> crashReport.log(throwable)).<Void>toObservable())
        .retry();
  }

  private void handleAptoideTvCardViewClick() {

    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.aptoideTvCardViewClick())
        .doOnNext(__ -> {
          view.startAptoideTvWebView();
          accountAnalytics.sendPromoteAptoideTVEvent();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleAptoideUploaderCardViewClick() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.aptoideUploaderCardViewClick())
        .doOnNext(__ -> {
          myAccountNavigator.navigateToUploader();
          accountAnalytics.sendPromoteAptoideUploaderEvent();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleAptoideBackupCardViewClick() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.aptoideBackupCardViewClick())
        .doOnNext(__ -> {
          myAccountNavigator.navigateToBackupApps();
          accountAnalytics.sendPromoteAptoideBackupAppsEvent();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private boolean storeExistsInAccount(Account account) {
    return account.getStore()
        .getId() != 0;
  }
}
