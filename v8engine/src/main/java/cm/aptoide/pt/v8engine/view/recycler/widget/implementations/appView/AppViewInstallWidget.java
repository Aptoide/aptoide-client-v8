/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.content.Context;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.model.v7.Malware;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.listapp.ListAppVersions;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.SimpleSubscriber;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.Progress;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.dialog.InstallWarningDialog;
import cm.aptoide.pt.v8engine.dialog.SharePreviewDialog;
import cm.aptoide.pt.v8engine.install.Installer;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.interfaces.AppMenuOptions;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.receivers.AppBoughtReceiver;
import cm.aptoide.pt.v8engine.repository.SocialRepository;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewInstallDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by sithengineer on 06/05/16.
 */
@Displayables({ AppViewInstallDisplayable.class }) public class AppViewInstallWidget
    extends Widget<AppViewInstallDisplayable> {

  private static final String TAG = AppViewInstallWidget.class.getSimpleName();

  private RelativeLayout downloadProgressLayout;
  private RelativeLayout installAndLatestVersionLayout;

  //
  // downloading views
  //
  private CheckBox shareInTimeline;
  private ProgressBar downloadProgress;
  private TextView textProgress;
  private ImageView actionResume;
  private ImageView actionPause;
  private ImageView actionCancel;

  // get app, upgrade and downgrade button
  private Button actionButton;

  // app info
  private TextView versionName;
  private View latestAvailableLayout;
  private View latestAvailableTrustedSeal;
  private View notLatestAvailableText;
  private TextView otherVersions;
  private MinimalAd minimalAd;

  private App trustedVersion;
  //private DownloadServiceHelper downloadServiceHelper;
  private PermissionRequest permissionRequest;
  private InstallManager installManager;
  private boolean isUpdate;
  private boolean triedInstall;

  //private Subscription subscribe;
  //private long appID;

  public AppViewInstallWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    downloadProgressLayout = (RelativeLayout) itemView.findViewById(R.id.download_progress_layout);
    installAndLatestVersionLayout =
        (RelativeLayout) itemView.findViewById(R.id.install_and_latest_version_layout);
    shareInTimeline = (CheckBox) itemView.findViewById(R.id.share_in_timeline);
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

  @Override public void bindView(AppViewInstallDisplayable displayable) {
    displayable.setInstallButton(actionButton);

    AptoideDownloadManager downloadManager = AptoideDownloadManager.getInstance();
    downloadManager.initDownloadService(getContext());
    Installer installer = new InstallerFactory().create(getContext(), InstallerFactory.ROLLBACK);
    installManager = new InstallManager(downloadManager, installer,
        AccessorFactory.getAccessorFor(Download.class),
        AccessorFactory.getAccessorFor(Installed.class));

    minimalAd = displayable.getMinimalAd();
    GetApp getApp = displayable.getPojo();
    GetAppMeta.App currentApp = getApp.getNodes().getMeta().getData();
    final FragmentShower fragmentShower = ((FragmentShower) getContext());

    versionName.setText(currentApp.getFile().getVername());
    otherVersions.setOnClickListener(v -> {
      Fragment fragment = V8Engine.getFragmentProvider()
          .newOtherVersionsFragment(currentApp.getName(), currentApp.getIcon(),
              currentApp.getPackageName());
      fragmentShower.pushFragmentV4(fragment);
    });

    compositeSubscription.add(
        displayable.getState().observeOn(AndroidSchedulers.mainThread()).subscribe(widgetState -> {
          switch (widgetState.getButtonState()) {
            case AppViewInstallDisplayable.ACTION_INSTALLING:
              if (widgetState.getProgress() != null) {
                downloadProgress.setIndeterminate(widgetState.getProgress().isIndeterminate());
                downloadStatusUpdate(widgetState.getProgress(), currentApp);
                setDownloadBarVisible(true, displayable, widgetState.getProgress(), currentApp);
                break;
              }
            case AppViewInstallDisplayable.ACTION_INSTALL:
              //App not installed
              setDownloadBarVisible(false, displayable, widgetState.getProgress(), currentApp);
              setupInstallOrBuyButton(displayable, getApp);
              if (widgetState.getProgress() != null) {
                downloadStatusUpdate(widgetState.getProgress(), currentApp);
              }
              ((AppMenuOptions) fragmentShower.getLastV4()).setUnInstallMenuOptionVisible(null);
              break;
            case AppViewInstallDisplayable.ACTION_DOWNGRADE:
              //downgrade
              setDownloadBarVisible(false, displayable, widgetState.getProgress(), currentApp);
              setupActionButton(R.string.downgrade, downgradeListener(currentApp));
              break;
            case AppViewInstallDisplayable.ACTION_OPEN:
              //current installed version
              setDownloadBarVisible(false, displayable, widgetState.getProgress(), currentApp);
              setupActionButton(R.string.open,
                  v -> AptoideUtils.SystemU.openApp(currentApp.getPackageName()));
              break;
            case AppViewInstallDisplayable.ACTION_UPDATE:
              //update
              isUpdate = true;
              setDownloadBarVisible(false, displayable, widgetState.getProgress(), currentApp);
              setupActionButton(R.string.update,
                  installOrUpgradeListener(currentApp, getApp.getNodes().getVersions(),
                      displayable));
              break;
          }
        }));

    if (isThisTheLatestVersionAvailable(currentApp, getApp.getNodes().getVersions())) {
      notLatestAvailableText.setVisibility(View.GONE);
      latestAvailableLayout.setVisibility(View.VISIBLE);
      if (isThisTheLatestTrustedVersionAvailable(currentApp, getApp.getNodes().getVersions())) {
        latestAvailableTrustedSeal.setVisibility(View.VISIBLE);
      }
    } else {
      notLatestAvailableText.setVisibility(View.VISIBLE);
      latestAvailableLayout.setVisibility(View.GONE);
    }

    permissionRequest = ((PermissionRequest) getContext());
  }

  @Override public void unbindView() {
    super.unbindView();
    triedInstall = false;
  }

  private void setupActionButton(@StringRes int text, View.OnClickListener onClickListener) {
    actionButton.setText(text);
    actionButton.setOnClickListener(onClickListener);
  }

  private void setupInstallOrBuyButton(AppViewInstallDisplayable displayable, GetApp getApp) {
    GetAppMeta.App app = getApp.getNodes().getMeta().getData();

    //check if the app is paid
    if (app.isPaid() && !app.getPay().isPaid()) {
      actionButton.setText(
          getContext().getString(R.string.buy) + " (" + app.getPay().getPriceDescription() + ")");
      actionButton.setOnClickListener(v -> displayable.buyApp(getContext(), app));
      AppBoughtReceiver receiver = new AppBoughtReceiver() {
        @Override public void appBought(long appId, String path) {
          if (app.getId() == appId) {
            isUpdate = false;
            app.getFile().setPath(path);
            app.getPay().setPaid();
            setupActionButton(R.string.install,
                installOrUpgradeListener(app, getApp.getNodes().getVersions(), displayable));
            actionButton.performClick();
          }
        }
      };
      getContext().registerReceiver(receiver, new IntentFilter(AppBoughtReceiver.APP_BOUGHT));
    } else {
      isUpdate = false;
      setupActionButton(R.string.install,
          installOrUpgradeListener(app, getApp.getNodes().getVersions(), displayable));
      if (displayable.isShouldInstall()) {
        actionButton.postDelayed(() -> {
          if (displayable.isVisible() && !triedInstall) {
            actionButton.performClick();
            triedInstall = true;
          }
        }, 1000);
      }
    }
  }

  private View.OnClickListener downgradeListener(final GetAppMeta.App app) {
    return view -> {
      final Context context = view.getContext();
      final PermissionRequest permissionRequest = (PermissionRequest) getContext();

      permissionRequest.requestAccessToExternalFileSystem(() -> {

        showMessageOKCancel(
            getContext().getResources().getString(R.string.downgrade_warning_dialog),
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
                          .flatMap(success -> installManager.install(getContext(), appDownload))
                          .observeOn(AndroidSchedulers.mainThread())
                          .subscribe(progress -> {
                            Logger.d(TAG, "Installing");
                          }, throwable -> Logger.e(TAG, throwable)));
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

  private void showRootInstallWarningPopup(Context context) {
    if (installManager.showWarning()) {
      compositeSubscription.add(GenericDialogs.createGenericYesNoCancelMessage(context, null,
          AptoideUtils.StringU.getFormattedString(R.string.root_access_dialog))
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
    PermissionManager permissionManager = new PermissionManager();
    final View.OnClickListener installHandler = v -> {

      if (installOrUpgradeMsg == R.string.installing_msg) {
        Analytics.ClickedOnInstallButton.clicked(app);
        Analytics.SourceDownloadComplete.installClicked(app.getId());
        Analytics.DownloadComplete.installClicked(app.getId());
      }

      showRootInstallWarningPopup(context);

      compositeSubscription.add(permissionManager.requestDownloadAccess(permissionRequest)
          .flatMap(success -> permissionManager.requestExternalStoragePermission(permissionRequest))
          .flatMap(success -> installManager.install(getContext(),
              new DownloadFactory().create(displayable.getPojo().getNodes().getMeta().getData(),
                  downloadAction)))
          .first()
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(progress -> {
            Toast.makeText(context, "INSTALL CLICKED", Toast.LENGTH_SHORT).show();
            if (AptoideAccountManager.isLoggedIn()) {

              SharePreviewDialog sharePreviewDialog = new SharePreviewDialog(displayable);
              AlertDialog.Builder alertDialog = sharePreviewDialog.showPreviewDialog(context)
                  .setPositiveButton(R.string.share, (dialogInterface, i) -> {
                    Toast.makeText(getContext(), "SHARING...", Toast.LENGTH_SHORT).show();
                    SocialRepository socialRepository = new SocialRepository();
                    socialRepository.share(displayable);
                  })
                  .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
                  });
              alertDialog.show();
            }
            ShowMessage.asSnack(v, installOrUpgradeMsg);
          }, err -> {
            if (err instanceof SecurityException) {
              ShowMessage.asSnack(v, R.string.needs_permission_to_fs);
            }
            Logger.e(TAG, err);
          }));
    };

    findTrustedVersion(app, appVersions);
    final boolean hasTrustedVersion = trustedVersion != null;

    final View.OnClickListener onSearchHandler = v -> {
      Fragment fragment;
      if (hasTrustedVersion) {
        // go to app view of the trusted version
        fragment = V8Engine.getFragmentProvider().newAppViewFragment(trustedVersion.getId());
      } else {
        // search for a trusted version
        fragment = V8Engine.getFragmentProvider().newSearchFragment(app.getName(), true);
      }
      ((FragmentShower) context).pushFragmentV4(fragment);
    };

    return v -> {
      final Malware.Rank rank = app.getFile().getMalware().getRank();
      if (!Malware.Rank.TRUSTED.equals(rank)) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View alertView =
            LayoutInflater.from(context).inflate(R.layout.dialog_install_warning, null);
        builder.setView(alertView);
        new InstallWarningDialog(rank, hasTrustedVersion, context, installHandler,
            onSearchHandler).getDialog().show();
      } else {
        installHandler.onClick(v);
      }
    };
  }

  private void downloadStatusUpdate(@NonNull Progress<Download> progress, GetAppMeta.App app) {
    switch (progress.getRequest().getOverallDownloadStatus()) {
      case Download.PAUSED: {
        actionResume.setVisibility(View.VISIBLE);
        actionPause.setVisibility(View.GONE);
        break;
      }
      case Download.IN_QUEUE:
      case Download.PROGRESS: {
        actionResume.setVisibility(View.GONE);
        actionPause.setVisibility(View.VISIBLE);
        downloadProgress.setProgress(progress.getCurrent());
        textProgress.setText(progress.getCurrent() + "%");
        break;
      }
      case Download.ERROR: {
        break;
      }

      case Download.COMPLETED: {
        Analytics.DownloadComplete.downloadComplete(app);
        Analytics.SourceDownloadComplete.downloadComplete(app.getId(), app.getPackageName());
        if (!isUpdate) {
          if (minimalAd != null && minimalAd.getCpdUrl() != null) {
            DataproviderUtils.AdNetworksUtils.knockCpd(minimalAd);
          }
        }
        break;
      }
    }
  }

  private void setupDownloadControls(GetAppMeta.App app, Progress<Download> progress,
      AppViewInstallDisplayable displayable) {
    String md5 = app.getMd5();

    actionCancel.setOnClickListener(view -> {
      installManager.removeInstallationFile(md5, getContext());
    });

    actionPause.setOnClickListener(view -> {
      installManager.stopInstallation(getContext(), md5);
    });

    actionResume.setOnClickListener(view -> {
      PermissionManager permissionManager = new PermissionManager();
      compositeSubscription.add(permissionManager.requestDownloadAccess(permissionRequest)
          .flatMap(permissionGranted -> permissionManager.requestExternalStoragePermission(
              (PermissionRequest) getContext()))
          .flatMap(success -> installManager.install(getContext(),
              new DownloadFactory().create(displayable.getPojo().getNodes().getMeta().getData(),
                  progress.getRequest().getAction())))
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(downloadProgress -> {
            Logger.d(TAG, "Installing");
          }, err -> {
            Logger.e(TAG, err);
          }));
    });
  }

  private void setDownloadBarVisible(boolean visible, AppViewInstallDisplayable displayable,
      Progress<Download> progress, GetAppMeta.App app) {
    installAndLatestVersionLayout.setVisibility(visible ? View.GONE : View.VISIBLE);
    downloadProgressLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    if (visible) {
      setupDownloadControls(app, progress, displayable);
    }
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
        && !appVersions.getList().isEmpty();
    if (canCompare) {
      boolean isLatestVersion =
          app.getFile().getMd5sum().equals(appVersions.getList().get(0).getFile().getMd5sum());
      if (isLatestVersion) {
        return app.getFile().getMalware().getRank() == Malware.Rank.TRUSTED;
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
        && !appVersions.getList().isEmpty();
    if (canCompare) {
      return app.getFile().getMd5sum().equals(appVersions.getList().get(0).getFile().getMd5sum());
    }
    return false;
  }

  private void findTrustedVersion(GetAppMeta.App app, ListAppVersions appVersions) {

    if (app.getFile() != null && app.getFile().getMalware() != null && !Malware.Rank.TRUSTED.equals(
        app.getFile().getMalware().getRank())) {

      for (App version : appVersions.getList()) {
        if (app.getId() != version.getId()
            && version.getFile() != null
            && version.getFile().getMalware() != null
            && Malware.Rank.TRUSTED.equals(version

            .getFile().getMalware().getRank())) {
          trustedVersion = version;
        }
      }
    }
  }
}
