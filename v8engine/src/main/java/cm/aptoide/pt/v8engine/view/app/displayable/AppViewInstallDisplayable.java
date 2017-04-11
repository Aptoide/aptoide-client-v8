/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 25/08/2016.
 */

package cm.aptoide.pt.v8engine.view.app.displayable;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Button;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.Progress;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.repository.InstalledRepository;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.repository.RollbackRepository;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import rx.Observable;

/**
 * Created by sithengineer on 06/05/16.
 */
public class AppViewInstallDisplayable extends AppViewDisplayable {

  /**
   * App not installed
   */
  public static final int ACTION_INSTALL = 0;
  /**
   * installed app has lower version than the current
   */
  public static final int ACTION_UPDATE = 1;
  /**
   * installed app has higher version than the current
   */
  public static final int ACTION_DOWNGRADE = 2;
  /**
   * installed app has the same version than the current
   */
  public static final int ACTION_OPEN = 3;
  /**
   * The current app is installing
   */
  public static final int ACTION_INSTALLING = 4;
  private static final String TAG = AppViewInstallDisplayable.class.getSimpleName();
  /**
   * This should only be used internally
   */
  private static final int ACTION_NO_STATE = -1;
  @Getter @Setter private boolean shouldInstall;
  @Getter private MinimalAd minimalAd;

  private RollbackRepository rollbackRepository;
  //private Installer installManager;

  private InstallManager installManager;
  private String md5;
  private String packageName;
  private InstalledRepository installedRepository;
  private Button installButton;
  private WidgetState widgetState;
  private GetAppMeta.App currentApp;

  public AppViewInstallDisplayable() {
    super();
  }

  public AppViewInstallDisplayable(InstallManager installManager, GetApp getApp,
      MinimalAd minimalAd, boolean shouldInstall, InstalledRepository installedRepository) {
    super(getApp);
    this.installManager = installManager;
    this.md5 = getApp.getNodes().getMeta().getData().getFile().getMd5sum();
    this.packageName = getApp.getNodes().getMeta().getData().getPackageName();
    currentApp = getApp.getNodes().getMeta().getData();
    this.minimalAd = minimalAd;
    this.shouldInstall = shouldInstall;
    this.rollbackRepository = RepositoryFactory.getRollbackRepository();
    this.installedRepository = installedRepository;
    widgetState = new WidgetState(ACTION_NO_STATE);
  }

  public static AppViewInstallDisplayable newInstance(GetApp getApp, InstallManager installManager,
      MinimalAd minimalAd, boolean shouldInstall, InstalledRepository installedRepository) {
    return new AppViewInstallDisplayable(installManager, getApp, minimalAd, shouldInstall,
        installedRepository);
  }

  public void startInstallationProcess() {
    if (installButton != null) {
      installButton.performClick();
    }
  }

  public void setInstallButton(Button installButton) {
    this.installButton = installButton;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_app_view_install;
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Logger.i(TAG, "onSaveInstanceState");
  }

  @Override public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    Logger.i(TAG, "onViewStateRestored");
  }

  @Override public void onResume() {
    super.onResume();
    Logger.i(TAG, "onResume");
  }

  @Override public void onPause() {
    super.onPause();
    Logger.i(TAG, "onPause");
  }

  public Observable<WidgetState> getState() {
    return getInstallationObservable(md5, installManager).flatMap(state -> {
      if (state.getButtonState() == ACTION_NO_STATE) {
        return getInstalledAppObservable(currentApp, installedRepository);
      } else {
        return Observable.just(state);
      }
    });
  }

  private Observable<WidgetState> getInstallationObservable(String md5,
      InstallManager installManager) {
    return installManager.getAsListInstallation(md5).map(progress -> {
      if (progress != null && progress.getState() != Progress.DONE && (progress.getState()
          == Progress.ACTIVE
          || progress.getRequest().getOverallDownloadStatus() == Download.PAUSED)) {

        widgetState.setButtonState(ACTION_INSTALLING);
      } else {
        widgetState.setButtonState(ACTION_NO_STATE);
      }
      widgetState.setProgress(progress);
      return widgetState;
    });
  }

  @NonNull private Observable<WidgetState> getInstalledAppObservable(GetAppMeta.App currentApp,
      InstalledRepository installedRepository) {
    return installedRepository.getAsList(currentApp.getPackageName()).map(installeds -> {
      if (installeds != null && installeds.size() > 0) {
        Installed installed = installeds.get(0);
        if (currentApp.getFile().getVercode() == installed.getVersionCode()) {
          widgetState.setButtonState(ACTION_OPEN);
        } else if (currentApp.getFile().getVercode() <= installed.getVersionCode()) {
          widgetState.setButtonState(ACTION_DOWNGRADE);
        } else if (currentApp.getFile().getVercode() >= installed.getVersionCode()) {
          widgetState.setButtonState(ACTION_UPDATE);
        }
      } else {
        widgetState.setButtonState(ACTION_INSTALL);
      }
      return widgetState;
    });
  }

  @IntDef({
      ACTION_INSTALL, ACTION_UPDATE, ACTION_DOWNGRADE, ACTION_OPEN, ACTION_INSTALLING,
      ACTION_NO_STATE
  })

  @Retention(RetentionPolicy.SOURCE)

  public @interface ButtonState {
  }

  public class WidgetState {
    private @Setter(AccessLevel.PRIVATE) @ButtonState int buttonState;
    private @Setter(AccessLevel.PRIVATE) @Getter @Nullable Progress<Download> progress;

    public WidgetState(int buttonState) {
      this.buttonState = buttonState;
    }

    public @ButtonState int getButtonState() {
      return buttonState;
    }
  }
}
