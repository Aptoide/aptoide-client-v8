/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 18/01/2017.
 */

package cm.aptoide.pt.v8engine.presenter;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.v8engine.AutoUpdate;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.fragment.BaseWizardViewerFragment;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.services.ContentPuller;
import cm.aptoide.pt.v8engine.services.PullingContentService;
import cm.aptoide.pt.v8engine.util.ApkFy;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import cm.aptoide.pt.v8engine.view.MainView;
import cm.aptoide.pt.v8engine.view.View;

/**
 * Created by marcelobenites on 18/01/17.
 */
public class MainPresenter implements Presenter {

  private final MainView view;
  private ApkFy apkFy;
  private AutoUpdate autoUpdate;
  private final ContentPuller contentPuller;
  private boolean firstCreated;

  public MainPresenter(MainView view, ApkFy apkFy, AutoUpdate autoUpdate,
      ContentPuller contentPuller) {
    this.view = view;
    this.apkFy = apkFy;
    this.autoUpdate = autoUpdate;
    this.contentPuller = contentPuller;
    this.firstCreated = true;
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .doOnNext(created -> apkFy.run())
        .filter(created -> firstCreated)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
          view.showHome();
          contentPuller.start();
          if (ManagerPreferences.isAutoUpdateEnable() && !V8Engine.isAutoUpdateWasCalled()) {
            // only call auto update when the app was not on the background
            autoUpdate.execute();
          }
          if (SecurePreferences.isWizardAvailable()) {
            view.changeOrientationToPortrait();
            view.showWizard();
            SecurePreferences.setWizardAvailable(false);
          }
          view.showDeepLink();
        }, throwable -> CrashReport.getInstance().log(throwable));
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {
    firstCreated = false;
  }
}
