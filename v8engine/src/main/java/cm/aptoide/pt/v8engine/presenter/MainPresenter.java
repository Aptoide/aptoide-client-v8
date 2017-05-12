/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 18/01/2017.
 */

package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.v8engine.AutoUpdate;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.pull.ContentPuller;
import cm.aptoide.pt.v8engine.util.ApkFy;

/**
 * Created by marcelobenites on 18/01/17.
 */
public class MainPresenter implements Presenter {

  private final MainView view;
  private final ContentPuller contentPuller;
  private ApkFy apkFy;
  private AutoUpdate autoUpdate;
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
          if (ManagerPreferences.isCheckAutoUpdateEnable() && !V8Engine.isAutoUpdateWasCalled()) {
            // only call auto update when the app was not on the background
            autoUpdate.execute();
          }
          if (view.showDeepLink()) {
            SecurePreferences.setWizardAvailable(false);
          } else {
            if (SecurePreferences.isWizardAvailable()) {
              view.changeOrientationToPortrait();
              view.showWizard();
              SecurePreferences.setWizardAvailable(false);
            }
          }
        }, throwable -> CrashReport.getInstance()
            .log(throwable));
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {
    firstCreated = false;
  }
}
