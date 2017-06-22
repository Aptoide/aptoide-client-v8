/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 18/01/2017.
 */

package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.v8engine.AutoUpdate;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.install.installer.RootInstallationRetryHandler;
import cm.aptoide.pt.v8engine.notification.ContentPuller;
import cm.aptoide.pt.v8engine.notification.NotificationSyncScheduler;
import cm.aptoide.pt.v8engine.util.ApkFy;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by marcelobenites on 18/01/17.
 */
public class MainPresenter implements Presenter {

  private final MainView view;
  private final ContentPuller contentPuller;
  private final InstallManager installManager;
  private final RootInstallationRetryHandler rootInstallationRetryHandler;
  private final CrashReport crashReport;
  private NotificationSyncScheduler notificationSyncScheduler;
  private ApkFy apkFy;
  private AutoUpdate autoUpdate;
  private boolean firstCreated;
  private boolean notificationShowed;

  public MainPresenter(MainView view, InstallManager installManager,
      RootInstallationRetryHandler rootInstallationRetryHandler, CrashReport crashReport,
      ApkFy apkFy, AutoUpdate autoUpdate, ContentPuller contentPuller,
      NotificationSyncScheduler notificationSyncScheduler) {
    this.view = view;
    this.installManager = installManager;
    this.rootInstallationRetryHandler = rootInstallationRetryHandler;
    this.crashReport = crashReport;
    this.apkFy = apkFy;
    this.autoUpdate = autoUpdate;
    this.contentPuller = contentPuller;
    this.notificationSyncScheduler = notificationSyncScheduler;
    this.firstCreated = true;
    this.notificationShowed = false;
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .doOnNext(created -> apkFy.run())
        .filter(created -> firstCreated)
        .doOnNext(created -> notificationSyncScheduler.forceSync())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
          view.showHome();
          contentPuller.start();
          if (ManagerPreferences.isCheckAutoUpdateEnable() && !V8Engine.isAutoUpdateWasCalled()) {
            // only call auto update when the app was not on the background
            autoUpdate.execute();
          }
          if (view.showDeepLink()) {
            SecurePreferences.setWizardAvailable(false);
          } else {
            if (SecurePreferences.isWizardAvailable()) {
              view.showWizard();
              SecurePreferences.setWizardAvailable(false);
            }
          }
        }, throwable -> crashReport.log(throwable));

    setupInstallErrorsDisplay();
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {
    firstCreated = false;
  }

  private void setupInstallErrorsDisplay() {
    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.RESUME.equals(event))
        .flatMap(lifecycleEvent -> rootInstallationRetryHandler.retries()
            .compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE)))
        .distinctUntilChanged(installationProgresses -> installationProgresses.size())
        .filter(installationProgresses -> installationProgresses.size() > 0)
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(installationProgresses -> {
          view.showInstallationError(installationProgresses);
          notificationShowed = true;
        }, throwable -> crashReport.log(throwable));

    view.getLifecycle()
        .filter(lifecycleEvent -> View.LifecycleEvent.RESUME.equals(lifecycleEvent))
        .flatMap(lifecycleEvent -> installManager.getTimedOutInstallations())
        .filter(installationProgresses -> installationProgresses.size() == 0)
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(noInstallErrors -> {
              if (notificationShowed) {
                view.dismissInstallationError();
                view.showInstallationSuccessMessage();
                notificationShowed = false;
              }
            },
            throwable -> crashReport.log(throwable));
  }
}
