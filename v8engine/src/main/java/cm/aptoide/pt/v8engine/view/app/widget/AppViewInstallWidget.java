/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.view.app.widget;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.GetApp;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.dataprovider.model.v7.Malware;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.listapp.ListAppVersions;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.SimpleSubscriber;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.Install;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.app.AppBoughtReceiver;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.download.DownloadEvent;
import cm.aptoide.pt.v8engine.download.DownloadEventConverter;
import cm.aptoide.pt.v8engine.download.DownloadFactory;
import cm.aptoide.pt.v8engine.download.DownloadInstallBaseEvent;
import cm.aptoide.pt.v8engine.download.InstallEvent;
import cm.aptoide.pt.v8engine.download.InstallEventConverter;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.timeline.SocialRepository;
import cm.aptoide.pt.v8engine.timeline.TimelineAnalytics;
import cm.aptoide.pt.v8engine.view.app.AppViewFragment;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewInstallDisplayable;
import cm.aptoide.pt.v8engine.view.dialog.SharePreviewDialog;
import cm.aptoide.pt.v8engine.view.install.InstallWarningDialog;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.facebook.appevents.AppEventsLogger;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created on 06/05/16.
 */
@Displayables({ AppViewInstallDisplayable.class }) public class AppViewInstallWidget
    extends Widget<AppViewInstallDisplayable> {

  private static final String TAG = AppViewInstallWidget.class.getSimpleName();

  private RelativeLayout downloadProgressLayout;
  private RelativeLayout installAndLatestVersionLayout;

  private ProgressBar downloadProgress;
  private TextView textProgress;
  private ImageView actionResume;
  private ImageView actionPause;
  private ImageView actionCancel;
  private Button actionButton;

  private TextView versionName;
  private View latestAvailableLayout;
  private View latestAvailableTrustedSeal;
  private View notLatestAvailableText;
  private TextView otherVersions;

  private App trustedVersion;
  private InstallManager installManager;
  private boolean isUpdate;
  private DownloadEventConverter downloadInstallEventConverter;
  private Analytics analytics;
  private InstallEventConverter installConverter;
  private AptoideAccountManager accountManager;
  private AppViewInstallDisplayable displayable;
  private SocialRepository socialRepository;
  private DownloadFactory downloadFactory;
  private PermissionService permissionService;
  private PermissionManager permissionManager;

  public AppViewInstallWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    downloadProgressLayout = (RelativeLayout) itemView.findViewById(R.id.download_progress_layout);
    installAndLatestVersionLayout =
        (RelativeLayout) itemView.findViewById(R.id.install_and_latest_version_layout);

    downloadProgress = (ProgressBar) itemView.findViewById(R.id.download_progress);
    textProgress = (TextView) itemView.findViewById(R.id.text_progress);
    actionPause = (ImageView) itemView.findViewById(R.id.ic_action_pause);
    actionResume = (ImageView) itemView.findViewById(R.id.ic_action_resume);
    actionCancel = (ImageView) itemView.findViewById(R.id.ic_action_cancel);
    actionButton = (Button) itemView.findViewById(R.id.action_btn);
    versionName = (TextView) itemView.findViewById(R.id.store_version_name);
    otherVersions = (TextView) itemView.findViewById(R.id.other_versions);
    latestAvailableLayout = itemView.findViewById(R.id.latest_available_layout);
    latestAvailableTrustedSeal = itemView.findViewById(R.id.latest_available_icon);
    notLatestAvailableText = itemView.findViewById(R.id.not_latest_available_text);
  }

  @Override public void unbindView() {
    super.unbindView();
    displayable.setInstallButton(null);
    displayable = null;
  }

  @Override public void bindView(AppViewInstallDisplayable displayable) {
    this.displayable = displayable;
    this.displayable.setInstallButton(actionButton);

    final OkHttpClient httpClient =
        ((V8Engine) getContext().getApplicationContext()).getDefaultClient();
    final Converter.Factory converterFactory = WebService.getDefaultConverter();
    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    installManager = ((V8Engine) getContext().getApplicationContext()).getInstallManager(
        InstallerFactory.ROLLBACK);
    BodyInterceptor<BaseBody> bodyInterceptor =
        ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
    final TokenInvalidator tokenInvalidator =
        ((V8Engine) getContext().getApplicationContext()).getTokenInvalidator();
    downloadInstallEventConverter =
        new DownloadEventConverter(bodyInterceptor, httpClient, converterFactory, tokenInvalidator,
            V8Engine.getConfiguration()
                .getAppId(),
            ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences(),
            (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE),
            (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE));
    installConverter =
        new InstallEventConverter(bodyInterceptor, httpClient, converterFactory, tokenInvalidator,
            V8Engine.getConfiguration()
                .getAppId(),
            ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences(),
            (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE),
            (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE));
    analytics = Analytics.getInstance();
    downloadFactory = displayable.getDownloadFactory();
    socialRepository =
        new SocialRepository(accountManager, bodyInterceptor, converterFactory, httpClient,
            new TimelineAnalytics(analytics,
                AppEventsLogger.newLogger(getContext().getApplicationContext()), bodyInterceptor,
                httpClient, WebService.getDefaultConverter(), tokenInvalidator,
                V8Engine.getConfiguration()
                    .getAppId(),
                ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences()),
            tokenInvalidator,
            ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences());

    GetApp getApp = this.displayable.getPojo();
    GetAppMeta.App currentApp = getApp.getNodes()
        .getMeta()
        .getData();
    versionName.setText(currentApp.getFile()
        .getVername());
    otherVersions.setOnClickListener(v -> {
      displayable.getAppViewAnalytics()
          .sendOtherVersionsEvent();
      Fragment fragment = V8Engine.getFragmentProvider()
          .newOtherVersionsFragment(currentApp.getName(), currentApp.getIcon(),
              currentApp.getPackageName());
      getFragmentNavigator().navigateTo(fragment);
    });

    //setup the ui
    compositeSubscription.add(displayable.getInstallState()
        .first()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(installationProgress -> updateUi(displayable, installationProgress, true, getApp))
        .subscribe(viewUpdated -> {
        }, throwable -> CrashReport.getInstance()
            .log(throwable)));

    //listen ui events
    compositeSubscription.add(displayable.getInstallState()
        .skip(1)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(
            installationProgress -> updateUi(displayable, installationProgress, false, getApp))
        .subscribe(viewUpdated -> {
        }, throwable -> CrashReport.getInstance()
            .log(throwable)));

    if (isThisTheLatestVersionAvailable(currentApp, getApp.getNodes()
        .getVersions())) {
      notLatestAvailableText.setVisibility(View.GONE);
      latestAvailableLayout.setVisibility(View.VISIBLE);
      if (isThisTheLatestTrustedVersionAvailable(currentApp, getApp.getNodes()
          .getVersions())) {
        latestAvailableTrustedSeal.setVisibility(View.VISIBLE);
      }
    } else {
      notLatestAvailableText.setVisibility(View.VISIBLE);
      latestAvailableLayout.setVisibility(View.GONE);
    }

    permissionService = ((PermissionService) getContext());
    permissionManager = new PermissionManager();
  }

  private void updateUi(AppViewInstallDisplayable displayable, Install install, boolean isSetup,
      GetApp getApp) {
    Install.InstallationStatus state = install.getState();
    switch (state) {
      case IN_QUEUE:
        updateInstallingUi(install, getApp.getNodes()
            .getMeta()
            .getData(), isSetup, true);

        break;
      case INSTALLING:
        updateInstallingUi(install, getApp.getNodes()
            .getMeta()
            .getData(), isSetup, !install.isIndeterminate());
        break;
      case INSTALLATION_TIMEOUT:
        if (isSetup) {
          updateUninstalledUi(displayable, getApp, isSetup, install.getType());
        } else {
          updateInstallingUi(install, getApp.getNodes()
              .getMeta()
              .getData(), isSetup, false);
        }
        break;
      case PAUSED:
        updatePausedUi(install, getApp, isSetup);
        break;
      case INSTALLED:
        //current installed version
        updateInstalledUi(install);
        break;
      case UNINSTALLED:
        //App not installed
        updateUninstalledUi(displayable, getApp, isSetup, install.getType());
        break;
      case GENERIC_ERROR:
        updateFailedUi(isSetup, displayable, install, getApp, "",
            getContext().getString(R.string.error_occured));
        break;
      case NOT_ENOUGH_SPACE_ERROR:
        updateFailedUi(isSetup, displayable, install, getApp,
            getContext().getString(R.string.out_of_space_dialog_title),
            getContext().getString(R.string.out_of_space_dialog_message));
        break;
    }
  }

  private void updateFailedUi(boolean isSetup, AppViewInstallDisplayable displayable,
      Install install, GetApp getApp, String errorTitle, String errorMessage) {
    if (isSetup) {
      updateUninstalledUi(displayable, getApp, isSetup, install.getType());
    } else {
      updatePausedUi(install, getApp, isSetup);
      showDialogError(errorTitle, errorMessage);
    }
  }

  @NonNull private void updateUninstalledUi(AppViewInstallDisplayable displayable, GetApp getApp,
      boolean isSetup, Install.InstallationType installationType) {

    GetAppMeta.App app = getApp.getNodes()
        .getMeta()
        .getData();
    setDownloadBarInvisible();
    switch (installationType) {
      case INSTALL:
        setupInstallOrBuyButton(displayable, getApp);
        compositeSubscription.add(displayable.getInstallAppRelay()
            .doOnNext(__ -> actionButton.performClick())
            .subscribe());
        break;
      case UPDATE:
        //update
        isUpdate = true;
        setupActionButton(R.string.appview_button_update, installOrUpgradeListener(app,
            getApp.getNodes()
                .getVersions(), displayable));
        break;
      case DOWNGRADE:
        //downgrade
        setupActionButton(R.string.appview_button_downgrade, downgradeListener(app));
        break;
    }
    setupDownloadControls(app, isSetup, installationType);
  }

  private void updateInstalledUi(Install install) {
    setDownloadBarInvisible();
    setupActionButton(R.string.appview_button_open,
        v -> AptoideUtils.SystemU.openApp(install.getPackageName(),
            getContext().getPackageManager(), getContext()));
  }

  private void updatePausedUi(Install install, GetApp app, boolean isSetup) {

    showProgress(install.getProgress(), install.isIndeterminate());
    actionResume.setVisibility(View.VISIBLE);
    actionPause.setVisibility(View.GONE);
    actionCancel.setVisibility(View.VISIBLE);
    setupDownloadControls(app.getNodes()
        .getMeta()
        .getData(), isSetup, install.getType());
  }

  private void updateInstallingUi(Install install, GetAppMeta.App app, boolean isSetup,
      boolean showControlButtons) {
    showProgress(install.getProgress(), install.isIndeterminate());
    if (showControlButtons) {
      actionResume.setVisibility(View.GONE);
      actionPause.setVisibility(View.VISIBLE);
      actionCancel.setVisibility(View.VISIBLE);
    } else {
      actionResume.setVisibility(View.GONE);
      actionPause.setVisibility(View.GONE);
      actionCancel.setVisibility(View.GONE);
    }
    setupDownloadControls(app, isSetup, install.getType());
  }

  private void showProgress(int progress, boolean isIndeterminate) {
    if (!isDownloadBarVisible()) {
      setDownloadBarVisible();
    }
    downloadProgress.setProgress(progress);
    downloadProgress.setIndeterminate(isIndeterminate);
    textProgress.setText(progress + "%");
  }

  private void setupActionButton(@StringRes int text, View.OnClickListener onClickListener) {
    actionButton.setText(text);
    actionButton.setOnClickListener(onClickListener);
  }

  private void setupInstallOrBuyButton(AppViewInstallDisplayable displayable, GetApp getApp) {
    GetAppMeta.App app = getApp.getNodes()
        .getMeta()
        .getData();

    //check if the app is paid
    if (app.isPaid() && !app.getPay()
        .isPaid()) {
      actionButton.setText(getContext().getString(R.string.appview_button_buy) + " (" + app.getPay()
          .getSymbol() + " " + app.getPay()
          .getPrice() + ")");
      actionButton.setOnClickListener(v -> buyApp(app));
      AppBoughtReceiver receiver = new AppBoughtReceiver() {
        @Override public void appBought(long appId, String path) {
          if (app.getId() == appId) {
            isUpdate = false;
            app.getFile()
                .setPath(path);
            app.getPay()
                .setPaid();
            setupActionButton(R.string.appview_button_install, installOrUpgradeListener(app,
                getApp.getNodes()
                    .getVersions(), displayable));
            actionButton.performClick();
          }
        }
      };
      getContext().registerReceiver(receiver, new IntentFilter(AppBoughtReceiver.APP_BOUGHT));
    } else {
      isUpdate = false;
      setupActionButton(R.string.appview_button_install, installOrUpgradeListener(app,
          getApp.getNodes()
              .getVersions(), displayable));
      if (displayable.isShouldInstall()) {
        actionButton.postDelayed(() -> {
          if (displayable.isVisible() && displayable.isShouldInstall()) {
            actionButton.performClick();
            displayable.setShouldInstall(false);
          }
        }, 1000);
      }
    }
  }

  private void buyApp(GetAppMeta.App app) {
    Fragment fragment = getFragmentNavigator().peekLast();
    if (fragment != null && AppViewFragment.class.isAssignableFrom(fragment.getClass())) {
      ((AppViewFragment) fragment).buyApp(app);
    }
  }

  private View.OnClickListener downgradeListener(final GetAppMeta.App app) {
    return view -> {
      final Context context = view.getContext();
      final PermissionService permissionRequest = (PermissionService) getContext();

      permissionRequest.requestAccessToExternalFileSystem(() -> {

        showMessageOKCancel(getContext().getResources()
                .getString(R.string.downgrade_warning_dialog),
            new SimpleSubscriber<GenericDialogs.EResponse>() {

              @Override public void onNext(GenericDialogs.EResponse eResponse) {
                super.onNext(eResponse);
                if (eResponse == GenericDialogs.EResponse.YES) {

                  ShowMessage.asSnack(view, R.string.downgrading_msg);

                  DownloadFactory factory = new DownloadFactory();
                  Download appDownload = factory.create(app, Download.ACTION_DOWNGRADE);
                  showRootInstallWarningPopup(context);
                  compositeSubscription.add(
                      new PermissionManager().requestDownloadAccess(permissionRequest)
                          .flatMap(success -> installManager.install(appDownload)
                              .toObservable()
                              .doOnSubscribe(() -> setupEvents(appDownload)))
                          .observeOn(AndroidSchedulers.mainThread())
                          .subscribe(progress -> {
                            // TODO: 12/07/2017 this code doesnt run
                            Logger.d(TAG, "Installing");
                          }, throwable -> CrashReport.getInstance()
                              .log(throwable)));
                  Analytics.Rollback.downgradeDialogContinue();
                } else {
                  Analytics.Rollback.downgradeDialogCancel();
                }
              }
            });
      }, () -> {
        ShowMessage.asSnack(view, R.string.needs_permission_to_fs);
      });
    };
  }

  private void setupEvents(Download download) {
    DownloadEvent report =
        downloadInstallEventConverter.create(download, DownloadEvent.Action.CLICK,
            DownloadEvent.AppContext.APPVIEW);

    analytics.save(report.getPackageName() + report.getVersionCode(), report);

    InstallEvent installEvent =
        installConverter.create(download, DownloadInstallBaseEvent.Action.CLICK,
            DownloadInstallBaseEvent.AppContext.APPVIEW);
    analytics.save(download.getPackageName() + download.getVersionCode(), installEvent);
  }

  private void showRootInstallWarningPopup(Context context) {
    if (installManager.showWarning()) {
      compositeSubscription.add(GenericDialogs.createGenericYesNoCancelMessage(context, null,
          AptoideUtils.StringU.getFormattedString(R.string.root_access_dialog,
              getContext().getResources()))
          .subscribe(eResponses -> {
            switch (eResponses) {
              case YES:
                installManager.rootInstallAllowed(true);
                break;
              case NO:
                installManager.rootInstallAllowed(false);
                break;
            }
          }));
    }
  }

  private void showMessageOKCancel(String message,
      SimpleSubscriber<GenericDialogs.EResponse> subscriber) {
    compositeSubscription.add(
        GenericDialogs.createGenericContinueCancelMessage(getContext(), "", message)
            .subscribe(subscriber));
  }

  public View.OnClickListener installOrUpgradeListener(GetAppMeta.App app,
      ListAppVersions appVersions, AppViewInstallDisplayable displayable) {

    final Context context = getContext();

    @StringRes final int installOrUpgradeMsg =
        this.isUpdate ? R.string.updating_msg : R.string.installing_msg;
    int downloadAction = isUpdate ? Download.ACTION_UPDATE : Download.ACTION_INSTALL;
    final View.OnClickListener installHandler = v -> {
      if (installOrUpgradeMsg == R.string.installing_msg) {
        Analytics.ClickedOnInstallButton.clicked(app);
        Analytics.DownloadComplete.installClicked(app.getId());
      }

      showRootInstallWarningPopup(context);
      compositeSubscription.add(permissionManager.requestDownloadAccess(permissionService)
          .flatMap(success -> permissionManager.requestExternalStoragePermission(permissionService))
          .map(success -> new DownloadFactory().create(displayable.getPojo()
              .getNodes()
              .getMeta()
              .getData(), downloadAction))
          .flatMapCompletable(download -> installManager.install(download)
              .doOnSubscribe(subcription -> setupEvents(download))
              .observeOn(AndroidSchedulers.mainThread())
              .doOnCompleted(() -> {
                if (accountManager.isLoggedIn()
                    && ManagerPreferences.isShowPreviewDialog(
                    ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences())
                    && Application.getConfiguration()
                    .isCreateStoreAndSetUserPrivacyAvailable()) {
                  SharePreviewDialog sharePreviewDialog =
                      new SharePreviewDialog(displayable, accountManager, true,
                          SharePreviewDialog.SharePreviewOpenMode.SHARE,
                          displayable.getTimelineAnalytics(),
                          ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences());
                  AlertDialog.Builder alertDialog =
                      sharePreviewDialog.getPreviewDialogBuilder(getContext());

                  sharePreviewDialog.showShareCardPreviewDialog(displayable.getPojo()
                          .getNodes()
                          .getMeta()
                          .getData()
                          .getPackageName(), displayable.getPojo()
                          .getNodes()
                          .getMeta()
                          .getData()
                          .getStore()
                          .getId(), "install", context, sharePreviewDialog, alertDialog,
                      socialRepository);
                }
                ShowMessage.asSnack(v, installOrUpgradeMsg);
              }))
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(progress -> {
          }, err -> {
            if (err instanceof SecurityException) {
              ShowMessage.asSnack(v, R.string.needs_permission_to_fs);
            }
            CrashReport.getInstance()
                .log(err);
          }));
    };

    findTrustedVersion(app, appVersions);
    final boolean hasTrustedVersion = trustedVersion != null;

    final View.OnClickListener onSearchHandler = v -> {
      Fragment fragment;
      if (hasTrustedVersion) {
        // go to app view of the trusted version
        fragment = V8Engine.getFragmentProvider()
            .newAppViewFragment(trustedVersion.getId(), trustedVersion.getPackageName());
      } else {
        // search for a trusted version
        fragment = V8Engine.getFragmentProvider()
            .newSearchFragment(app.getName(), true);
      }
      getFragmentNavigator().navigateTo(fragment);
    };

    return v -> {
      final Malware.Rank rank = app.getFile()
          .getMalware()
          .getRank();
      if (!Malware.Rank.TRUSTED.equals(rank)) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View alertView = LayoutInflater.from(context)
            .inflate(R.layout.dialog_install_warning, null);
        builder.setView(alertView);
        new InstallWarningDialog(rank, hasTrustedVersion, context, installHandler,
            onSearchHandler).getDialog()
            .show();
      } else {
        installHandler.onClick(v);
      }
    };
  }

  private void showDialogError(String title, String message) {
    GenericDialogs.createGenericOkMessage(getContext(), title, message)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(eResponse -> {
        }, throwable -> CrashReport.getInstance()
            .log(throwable));
  }

  private void setupDownloadControls(GetAppMeta.App app, boolean isSetup,
      Install.InstallationType installationType) {
    if (isSetup) {
      int actionInstall;
      switch (installationType) {
        case INSTALLED:
          //in case of app is uninstalled inside the appview, the setup won't run again. The unique
          // possible action will be install
        case INSTALL:
          actionInstall = Download.ACTION_INSTALL;
          break;
        case UPDATE:
          actionInstall = Download.ACTION_UPDATE;
          break;
        case DOWNGRADE:
          actionInstall = Download.ACTION_DOWNGRADE;
          break;
        default:
          actionInstall = Download.ACTION_INSTALL;
      }
      String md5 = app.getMd5();
      Download download = downloadFactory.create(app, actionInstall);
      actionCancel.setOnClickListener(
          view -> installManager.removeInstallationFile(md5, download.getPackageName(),
              download.getVersionCode()));

      actionPause.setOnClickListener(view -> {
        installManager.stopInstallation(md5);
      });

      actionResume.setOnClickListener(view -> {
        PermissionManager permissionManager = new PermissionManager();
        compositeSubscription.add(permissionManager.requestDownloadAccess(permissionService)
            .flatMap(permissionGranted -> permissionManager.requestExternalStoragePermission(
                (PermissionService) getContext()))
            .flatMap(success -> installManager.install(download)
                .toObservable()
                .doOnSubscribe(() -> setupEvents(download)))
            .subscribe(downloadProgress -> Logger.d(TAG, "Installing"),
                err -> CrashReport.getInstance()
                    .log(err)));
      });
    }
  }

  private void setDownloadBarInvisible() {
    installAndLatestVersionLayout.setVisibility(View.VISIBLE);
    downloadProgressLayout.setVisibility(View.GONE);
  }

  private void setDownloadBarVisible() {
    installAndLatestVersionLayout.setVisibility(View.GONE);
    downloadProgressLayout.setVisibility(View.VISIBLE);
  }

  private boolean isDownloadBarVisible() {
    return installAndLatestVersionLayout.getVisibility() == View.GONE
        && downloadProgressLayout.getVisibility() == View.VISIBLE;
  }

  /**
   * Similar to {@link #isThisTheLatestVersionAvailable(GetAppMeta.App, ListAppVersions)
   * isThisTheLatestVersionAvailable} altough this returns true only if
   * the latest version is the same app that we are viewing and the current app is trusted.
   */
  private boolean isThisTheLatestTrustedVersionAvailable(GetAppMeta.App app,
      @Nullable ListAppVersions appVersions) {
    boolean canCompare = appVersions != null
        && appVersions.getList() != null
        && appVersions.getList() != null
        && !appVersions.getList()
        .isEmpty();
    if (canCompare) {
      boolean isLatestVersion = app.getFile()
          .getMd5sum()
          .equals(appVersions.getList()
              .get(0)
              .getFile()
              .getMd5sum());
      if (isLatestVersion) {
        return app.getFile()
            .getMalware()
            .getRank() == Malware.Rank.TRUSTED;
      }
    }
    return false;
  }

  /**
   * Checks if the current app that we are viewing is the latest version available.
   * <p>
   * This is done by comparing the current app md5sum with the first app md5sum in the list of
   * other
   * versions, since the other versions list is sorted using
   * several criterea (vercode, cpu, malware ranking, etc.).
   *
   * @return true if this is the latested version of this app, trusted or not.
   */
  private boolean isThisTheLatestVersionAvailable(GetAppMeta.App app,
      @Nullable ListAppVersions appVersions) {
    boolean canCompare = appVersions != null
        && appVersions.getList() != null
        && appVersions.getList() != null
        && !appVersions.getList()
        .isEmpty();
    if (canCompare) {
      return app.getFile()
          .getMd5sum()
          .equals(appVersions.getList()
              .get(0)
              .getFile()
              .getMd5sum());
    }
    return false;
  }

  private void findTrustedVersion(GetAppMeta.App app, ListAppVersions appVersions) {

    if (app.getFile() != null
        && app.getFile()
        .getMalware() != null
        && !Malware.Rank.TRUSTED.equals(app.getFile()
        .getMalware()
        .getRank())) {

      for (App version : appVersions.getList()) {
        if (app.getId() != version.getId()
            && version.getFile() != null
            && version.getFile()
            .getMalware() != null
            && Malware.Rank.TRUSTED.equals(version

            .getFile()
            .getMalware()
            .getRank())) {
          trustedVersion = version;
        }
      }
    }
  }
}
