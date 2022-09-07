/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 18/01/2017.
 */

package cm.aptoide.pt.presenter;

import android.content.Intent;
import android.content.SharedPreferences;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.AptoideCredentials;
import cm.aptoide.pt.account.AgentPersistence;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.autoupdate.AutoUpdateDialogFragment;
import cm.aptoide.pt.autoupdate.AutoUpdateManager;
import cm.aptoide.pt.bottomNavigation.BottomNavigationMapper;
import cm.aptoide.pt.bottomNavigation.BottomNavigationNavigator;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.home.AptoideBottomNavigator;
import cm.aptoide.pt.home.apps.UpdatesManager;
import cm.aptoide.pt.install.Install;
import cm.aptoide.pt.install.InstallCompletedNotifier;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.install.installer.RootInstallationRetryHandler;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.notification.ContentPuller;
import cm.aptoide.pt.notification.NotificationSyncScheduler;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.root.RootAvailabilityManager;
import cm.aptoide.pt.util.ApkFyManager;
import cm.aptoide.pt.view.DeepLinkManager;
import cm.aptoide.pt.view.wizard.WizardFragment;
import com.aptoide.authentication.AuthenticationException;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorNotImplementedException;

public class MainPresenter implements Presenter {

  private final MainView view;
  private final ContentPuller contentPuller;
  private final InstallManager installManager;
  private final RootInstallationRetryHandler rootInstallationRetryHandler;
  private final CrashReport crashReport;
  private final SharedPreferences sharedPreferences;
  private final SharedPreferences securePreferences;
  private final FragmentNavigator fragmentNavigator;
  private final DeepLinkManager deepLinkManager;
  private final NotificationSyncScheduler notificationSyncScheduler;
  private final InstallCompletedNotifier installCompletedNotifier;
  private final ApkFyManager apkFyManager;
  private final boolean firstCreated;
  private final AptoideBottomNavigator aptoideBottomNavigator;
  private final Scheduler viewScheduler;
  private final Scheduler ioScheduler;
  private final BottomNavigationNavigator bottomNavigationNavigator;
  private final UpdatesManager updatesManager;
  private final AutoUpdateManager autoUpdateManager;
  private final PermissionService permissionService;
  private final RootAvailabilityManager rootAvailabilityManager;
  private final AptoideAccountManager accountManager;
  private final AccountNavigator accountNavigator;
  private final AgentPersistence agentPersistence;
  private final BottomNavigationMapper bottomNavigationMapper;

  public MainPresenter(MainView view, InstallManager installManager,
      RootInstallationRetryHandler rootInstallationRetryHandler, CrashReport crashReport,
      ApkFyManager apkFyManager, ContentPuller contentPuller,
      NotificationSyncScheduler notificationSyncScheduler,
      InstallCompletedNotifier installCompletedNotifier, SharedPreferences sharedPreferences,
      SharedPreferences securePreferences, FragmentNavigator fragmentNavigator,
      DeepLinkManager deepLinkManager, boolean firstCreated,
      AptoideBottomNavigator aptoideBottomNavigator, Scheduler viewScheduler, Scheduler ioScheduler,
      BottomNavigationNavigator bottomNavigationNavigator, UpdatesManager updatesManager,
      AutoUpdateManager autoUpdateManager, PermissionService permissionService,
      RootAvailabilityManager rootAvailabilityManager,
      BottomNavigationMapper bottomNavigationMapper, AptoideAccountManager accountManager,
      AccountNavigator accountNavigator, AgentPersistence agentPersistence) {
    this.view = view;
    this.installManager = installManager;
    this.rootInstallationRetryHandler = rootInstallationRetryHandler;
    this.crashReport = crashReport;
    this.apkFyManager = apkFyManager;
    this.contentPuller = contentPuller;
    this.notificationSyncScheduler = notificationSyncScheduler;
    this.installCompletedNotifier = installCompletedNotifier;
    this.fragmentNavigator = fragmentNavigator;
    this.deepLinkManager = deepLinkManager;
    this.firstCreated = firstCreated;
    this.sharedPreferences = sharedPreferences;
    this.securePreferences = securePreferences;
    this.aptoideBottomNavigator = aptoideBottomNavigator;
    this.viewScheduler = viewScheduler;
    this.ioScheduler = ioScheduler;
    this.bottomNavigationNavigator = bottomNavigationNavigator;
    this.updatesManager = updatesManager;
    this.autoUpdateManager = autoUpdateManager;
    this.permissionService = permissionService;
    this.rootAvailabilityManager = rootAvailabilityManager;
    this.bottomNavigationMapper = bottomNavigationMapper;
    this.accountManager = accountManager;
    this.accountNavigator = accountNavigator;
    this.agentPersistence = agentPersistence;
  }

  @Override public void present() {

    view.getLifecycleEvent()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .doOnNext(created -> apkFyManager.run())
        .filter(created -> firstCreated)
        .doOnNext(created -> notificationSyncScheduler.forceSync())
        .doOnNext(__ -> contentPuller.start())
        .doOnNext(__ -> downloadAutoUpdate())
        .observeOn(viewScheduler)
        .doOnNext(__ -> navigate())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));

    view.getLifecycleEvent()
        .filter(lifecycleEvent -> View.LifecycleEvent.CREATE.equals(lifecycleEvent))
        .flatMap(created -> aptoideBottomNavigator.navigationEvent()
            .observeOn(viewScheduler)
            .doOnNext(fragmentid -> aptoideBottomNavigator.showFragment(fragmentid))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });

    setupInstallErrorsDisplay();
    shortcutManagement();
    setupUpdatesNumber();

    handleAuthentication();
  }

  private void handleAuthentication() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> View.LifecycleEvent.CREATE.equals(lifecycleEvent))
        .flatMap(__ -> view.onAuthenticationIntent()
            .doOnNext(__1 -> accountNavigator.clearBackStackUntilLogin())
            .flatMapCompletable(token -> authenticate(token))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, Throwable::printStackTrace);
  }

  private Completable authenticate(String authToken) {
    return accountManager.login(new AptoideCredentials(agentPersistence.getEmail(), authToken, true,
            agentPersistence.getAgent(), agentPersistence.getState()))
        .observeOn(viewScheduler)
        .doOnSubscribe(() -> view.showLoadingView())
        .doOnSuccess(__ -> view.hideLoadingView())
        .doOnSuccess(isSignup -> handleFirstSignup(isSignup))
        .doOnError(throwable -> {
          view.hideLoadingView();
          if (throwable instanceof AuthenticationException
              && (((AuthenticationException) throwable).getCode() >= 400
              && ((AuthenticationException) throwable).getCode() < 500)) {
            accountNavigator.navigateToLoginError();
          } else {
            view.showGenericErrorMessage();
          }
        })
        .toCompletable();
  }

  private void handleFirstSignup(Boolean isSignup) {
    if (isSignup) {
      accountNavigator.navigateToCreateProfileView();
    }
  }

  private void setupUpdatesNumber() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(__ -> updatesManager.getUpdatesNumber())
        .observeOn(viewScheduler)
        .doOnNext(updates -> view.showUpdatesBadge(updates))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void setupInstallErrorsDisplay() {
    view.getLifecycleEvent()
        .filter(event -> View.LifecycleEvent.RESUME.equals(event))
        .flatMapSingle(__ -> rootAvailabilityManager.isRootAvailable())
        .filter(rootAvailable -> rootAvailable)
        .flatMap(lifecycleEvent -> rootInstallationRetryHandler.retries()
            .compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE)))
        .distinctUntilChanged(installationProgresses -> installationProgresses.size())
        .filter(installationProgresses -> !installationProgresses.isEmpty())
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(installationProgresses -> {
          watchInstalls(installationProgresses);
          view.showInstallationError(installationProgresses.size());
        }, throwable -> crashReport.log(throwable));

    view.getLifecycleEvent()
        .filter(lifecycleEvent -> View.LifecycleEvent.RESUME.equals(lifecycleEvent))
        .flatMapSingle(__ -> rootAvailabilityManager.isRootAvailable())
        .filter(rootAvailable -> rootAvailable)
        .flatMap(lifecycleEvent -> installManager.getTimedOutInstallations())
        .filter(installationProgresses -> !installationProgresses.isEmpty())
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(noInstallErrors -> view.dismissInstallationError(),
            throwable -> crashReport.log(throwable));

    view.getLifecycleEvent()
        .filter(lifecycleEvent -> View.LifecycleEvent.RESUME.equals(lifecycleEvent))
        .flatMapSingle(__ -> rootAvailabilityManager.isRootAvailable())
        .filter(rootAvailable -> rootAvailable)
        .flatMap(event -> installCompletedNotifier.getWatcher())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(allInstallsCompleted -> view.showInstallationSuccessMessage());

    view.getLifecycleEvent()
        .filter(lifecycleEvent -> View.LifecycleEvent.RESUME.equals(lifecycleEvent))
        .flatMapSingle(__ -> rootAvailabilityManager.isRootAvailable())
        .filter(rootAvailable -> rootAvailable)
        .flatMap(lifecycleEvent -> view.getInstallErrorsDismiss())
        .flatMapCompletable(click -> installManager.cleanTimedOutInstalls())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(timeoutErrorsCleaned -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void shortcutManagement() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.DESTROY))
        .first()
        .subscribe(__ -> deepLinkManager.freeSubscriptions(),
            throwable -> crashReport.log(throwable));
  }

  // FIXME we are showing home by default when we should decide were to go here and provide

  // proper up/back navigation to home if needed
  private void navigate() {
    Intent intent = view.getIntentAfterCreate();
    showHome();
    if (deepLinkManager.showDeepLink(intent)) {
      SecurePreferences.setWizardAvailable(false, securePreferences);
    } else {
      if (SecurePreferences.isWizardAvailable(securePreferences)) {
        showWizard();
        SecurePreferences.setWizardAvailable(false, securePreferences);
      }
    }
  }

  private Single<Boolean> isAutoUpdateDownloaded() {
    return autoUpdateManager.isDownloadComplete();
  }

  private void downloadAutoUpdate() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> View.LifecycleEvent.CREATE.equals(lifecycleEvent))
        .filter(__ -> ManagerPreferences.isCheckAutoUpdateEnable(sharedPreferences))
        .observeOn(ioScheduler)
        .flatMap(__ -> autoUpdateManager.shouldUpdate())
        .observeOn(viewScheduler)
        .filter(shouldUpdate -> shouldUpdate)
        .flatMapSingle(__ -> isAutoUpdateDownloaded())
        .flatMap(isDownloaded -> {
          if (isDownloaded) {
            return handleAutoUpdateDialog();
          } else {
            return handleAutoUpdateDownload();
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(timeoutErrorsCleaned -> {
        }, throwable -> {
          handleErrorResult(throwable);
          crashReport.log(throwable);
        });
  }

  private Observable<Install> handleAutoUpdateDownload() {
    return autoUpdateManager.hasDownloadPermissions(permissionService)
        .filter(hasDownloadPermissions -> hasDownloadPermissions)
        .doOnNext(__ -> autoUpdateManager.clearAutoUpdateShow())
        .observeOn(ioScheduler)
        .flatMap(success -> autoUpdateManager.startUpdate(false));
  }

  private Observable<Boolean> handleAutoUpdateDialog() {
    autoUpdateManager.incrementAutoUpdateShow();
    return autoUpdateManager.shouldShowAutoUpdateDialog()
        .filter(show -> show)
        .doOnNext(__ -> showAutoUpdate());
  }

  private void showWizard() {
    fragmentNavigator.navigateTo(WizardFragment.newInstance(), true);
  }

  private void showHome() {
    bottomNavigationNavigator.navigateToHome();
  }

  private void showAutoUpdate() {
    fragmentNavigator.navigateToDialogFragment(new AutoUpdateDialogFragment());
  }

  private void watchInstalls(List<Install> installs) {
    for (Install install : installs) {
      installCompletedNotifier.add(install.getPackageName(), install.getVersionCode(),
          install.getMd5());
    }
  }

  private void handleErrorResult(Throwable throwable) {
    view.dismissAutoUpdateDialog();
    if (!(throwable instanceof SecurityException)) {
      view.showUnknownErrorMessage();
    }
  }
}
