/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.IntentFilter;
import android.support.annotation.MainThread;
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
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.exceptions.DownloadNotFoundException;
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
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecureKeys;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
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
import cm.aptoide.pt.v8engine.install.Installer;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.interfaces.AppMenuOptions;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.receivers.AppBoughtReceiver;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewInstallDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static cm.aptoide.pt.utils.GenericDialogs.EResponse.YES;

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
  private CheckBox shareInTimeline; // FIXME: 27/07/16 sithengineer what does this flag do ??
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
  private CompositeSubscription subscriptions;
  private boolean isUpdate;

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
    //displayable.setOnResumeAction(() -> onViewAttached());
    //displayable.setOnPauseAction(() -> onViewDetached());
    displayable.setInstallButton(actionButton);
    if (subscriptions == null || subscriptions.isUnsubscribed()) {
      subscriptions = new CompositeSubscription();
    }

    AptoideDownloadManager downloadManager = AptoideDownloadManager.getInstance();
    downloadManager.initDownloadService(getContext());
    Installer installer = new InstallerFactory().create(getContext(), InstallerFactory.ROLLBACK);
    installManager = new InstallManager(downloadManager, installer,
        AccessorFactory.getAccessorFor(Download.class),
        AccessorFactory.getAccessorFor(Installed.class));

    minimalAd = displayable.getMinimalAd();
    GetApp getApp = displayable.getPojo();
    //appID = getApp.getNodes().getMeta().getData().getId();
    GetAppMeta.App currentApp = getApp.getNodes().getMeta().getData();
    final FragmentShower fragmentShower = ((FragmentShower) getContext());

    versionName.setText(currentApp.getFile().getVername());
    otherVersions.setOnClickListener(v -> {
      Fragment fragment = V8Engine.getFragmentProvider()
          .newOtherVersionsFragment(currentApp.getName(), currentApp.getIcon(),
              currentApp.getPackageName());
      fragmentShower.pushFragmentV4(fragment);
    });

    String packageName = currentApp.getPackageName();

    //@Cleanup Realm realm = DeprecatedDatabase.get();
    //Installed installed = DeprecatedDatabase.InstalledQ.get(packageName, realm);
    //Update update = DeprecatedDatabase.UpdatesQ.get(packageName, realm);

    //check if the app is installed or has an update
    //if (update != null) {
    //	// app installed and has a pending update. setup update buttons
    //	((AppMenuOptions) fragmentShower.getLastV4()).setUnInstallMenuOptionVisible(null);
    //	actionButton.setText(R.string.update);
    //	actionButton.setOnClickListener(installOrUpgradeListener(true, currentApp, getApp.getNodes().getVersions(), displayable));
    //
    //	// setup un-install button as visible in fragment menu
    //	((AppMenuOptions) fragmentShower.getLastV4()).setUnInstallMenuOptionVisible(() -> {
    //		displayable.uninstall(getContext(), currentApp).subscribe();
    //	});
    //} else if (update == null && installed != null) {
    //
    //	// app installed and does not have a pending update. we can show open or downgrade buttons here.
    //	// it is a downgrade if the appview version is inferior to the installed version
    //	// it is a open if the appview version is equal to the installed version
    //
    //	if (currentApp.getFile().getVercode() < installed.getVersionCode()) {
    //		actionButton.setText(R.string.downgrade);
    //		actionButton.setOnClickListener(downgradeListener(currentApp, displayable));
    //	} else {
    //		actionButton.setText(R.string.open);
    //		actionButton.setOnClickListener(v -> AptoideUtils.SystemU.openApp(currentApp.getPackageName()));
    //	}
    //
    //	// setup un-install button as visible in fragment menu
    //	((AppMenuOptions) fragmentShower.getLastV4()).setUnInstallMenuOptionVisible(() -> {
    //		displayable.uninstall(getContext(), currentApp).subscribe();
    //	});
    //} else {
    //	// app not installed
    //	setupInstallOrBuyButton(displayable, getApp);
    //
    //	// setup un-install button as invisible in fragment menu
    //	((AppMenuOptions) fragmentShower.getLastV4()).setUnInstallMenuOptionVisible(null);
    //}

    InstalledAccessor installedAccessor = displayable.getInstalledAccessor();
    installedAccessor.get(packageName)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(installed -> {
          if (installed != null) {
            ((AppMenuOptions) fragmentShower.getLastV4()).setUnInstallMenuOptionVisible(() -> {
              subscriptions.add(
                  new PermissionManager().requestDownloadAccess((PermissionRequest) getContext())
                      .flatMap(success -> installManager.uninstall(getContext(), packageName))
                      .subscribe(aVoid -> {
                      }, throwable -> throwable.printStackTrace()));
            });
            if (currentApp.getFile().getVercode() == installed.getVersionCode()) {
              //current installed version
              setupActionButton(R.string.open,
                  v -> AptoideUtils.SystemU.openApp(currentApp.getPackageName()));
            } else if (currentApp.getFile().getVercode() > installed.getVersionCode()) {
              //update
              isUpdate = true;
              setupActionButton(R.string.update,
                  installOrUpgradeListener(currentApp, getApp.getNodes().getVersions(),
                      displayable));
            } else {
              //downgrade
              setupActionButton(R.string.downgrade, downgradeListener(currentApp, displayable));
            }
          } else {
            //app not installed
            setupInstallOrBuyButton(displayable, getApp);
            ((AppMenuOptions) fragmentShower.getLastV4()).setUnInstallMenuOptionVisible(null);
          }
        }, throwable -> throwable.printStackTrace());
    checkOnGoingDownload(getApp, displayable);

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

    ContextWrapper ctx = (ContextWrapper) versionName.getContext();
    permissionRequest = ((PermissionRequest) ctx.getBaseContext());
  }

  private void setupActionButton(@StringRes int text, View.OnClickListener onClickListener) {
    actionButton.setText(text);
    actionButton.setOnClickListener(onClickListener);
  }

  @Override public void onViewAttached() {
    /*subscribe = AptoideDownloadManager.getInstance().getDownloads()
        .map(downloads -> {
					for (int i = 0; i < downloads.size(); i++) {
						if (downloads.get(i).getAppId() == appID && (downloads.get(i).getOverallDownloadStatus()
								== Download.PROGRESS
								|| downloads.get(i).getOverallDownloadStatus() == Download.PAUSED)) {

							return true;
						}
					}
					return false;
				})
				.distinctUntilChanged()
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(showControllers -> {
				}, throwable -> throwable.printStackTrace());*/
  }

  @Override public void onViewDetached() {
    actionButton.setOnClickListener(null);
    actionPause.setOnClickListener(null);
    actionCancel.setOnClickListener(null);
    subscriptions.clear();
  }

  public void checkOnGoingDownload(GetApp getApp, AppViewInstallDisplayable displayable) {
    GetAppMeta.App app = getApp.getNodes().getMeta().getData();
    installManager.getInstallation(app.getMd5())
        .firstOrDefault(null)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(progress -> {
          if ((progress.getState() == Progress.ACTIVE)) {
            setDownloadBarVisible(true);
            setupDownloadControls(app, progress, displayable);
            installManager.getInstallation(app.getMd5())
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(installationProgress -> shouldContinueListenDownload(
                    installationProgress.getState()))
                .subscribe(onGoingDownload -> {
                  manageDownload(progress, app);
                }, err -> {
                  Logger.e(TAG, err);
                });
          }
        }, err -> {
          if (!(err instanceof DownloadNotFoundException)) {
            Logger.e(TAG, err);
          }
          // ignore because download does not exist
        });

    // FIXME: 22/08/16 sithengineer clean the following commented out code
    //		downloadServiceHelper.getAllDownloads().firstOrDefault(Collections.emptyList()).subscribe(downloads -> {
    //			for (Download download : downloads) {
    //				int downloadStatus = download.getOverallDownloadStatus();
    //				if ((downloadStatus == Download.PROGRESS || downloadStatus == Download.IN_QUEUE || downloadStatus == Download.PENDING ||
    // downloadStatus ==
    //						Download.PAUSED) && download
    //						.getAppId() == app.getId()) {
    //					setDownloadBarVisible(true);
    //					setupDownloadControls(app, download, displayable);
    //					downloadServiceHelper.getDownload(app.getId()).subscribe(onGoingDownload -> {
    //						manageDownload(onGoingDownload, displayable, app);
    //					}, err -> {
    //						Logger.e(TAG, err);
    //					});
    //					return;
    //				}
    //			}
    //		}, err -> {
    //			Logger.e(TAG, err);
    //		});
  }

  private boolean shouldContinueListenDownload(int downloadStatus) {
    return downloadStatus != Progress.INACTIVE;
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
          if (displayable.isVisible()) {
            actionButton.performClick();
          }
        }, 1000);
      }
    }
  }

  private View.OnClickListener downgradeListener(final GetAppMeta.App app,
      AppViewInstallDisplayable displayable) {
    return view -> {
      final Context context = view.getContext();
      ContextWrapper contextWrapper = (ContextWrapper) context;
      final PermissionRequest permissionRequest =
          ((PermissionRequest) contextWrapper.getBaseContext());

      permissionRequest.requestAccessToExternalFileSystem(() -> {

        showMessageOKCancel(
            getContext().getResources().getString(R.string.downgrade_warning_dialog),
            new SimpleSubscriber<GenericDialogs.EResponse>() {

              @Override public void onNext(GenericDialogs.EResponse eResponse) {
                super.onNext(eResponse);
                if (eResponse == GenericDialogs.EResponse.YES) {

                  ShowMessage.asSnack(view, R.string.downgrading_msg);

                  //installManager.suWarningAndPermission();

                  DownloadFactory factory = new DownloadFactory();
                  Download appDownload = factory.create(app, Download.ACTION_DOWNGRADE);

                  if (installManager.showWarning() ) {
                    GenericDialogs.createGenericYesNoCancelMessage(context, null
                        , AptoideUtils.StringU.getFormattedString(R.string.root_access_dialog) )
                        .subscribe(eResponses -> {
                          switch (eResponses) {
                            case YES:
                              installManager.rootInstallAllowed(true);
                              break;
                            case NO:
                              installManager.rootInstallAllowed(false);
                              break;
                          }
                        });
                  }

                  subscriptions.add(new PermissionManager().requestDownloadAccess(permissionRequest)
                      .flatMap(success -> installManager.install(getContext(), appDownload))
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(progress -> {
                        manageDownload(progress, app);
                /*if (!setupDownloadControlsRunned) {
                  // TODO: 09/09/16 refactor this
                  setupDownloadControls(app, appDownload, displayable);
                }*/

                        //if (progress.isDone()) {
                        //  //final String packageName = app.getPackageName();
                        //  //final FileToDownload downloadedFile = progress.getFilesToDownload().get(0);
                        //
                        //  displayable.downgrade(getContext()).subscribe(aVoid -> {
                        //  }, throwable -> throwable.printStackTrace());
                        //}
                      }, Throwable::printStackTrace));
                  setupDownloadControls(app, new Progress<>(appDownload, true, 100, 0, 0, 0),
                      displayable);
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

  private void showMessageOKCancel(String message,
      SimpleSubscriber<GenericDialogs.EResponse> subscriber) {
    GenericDialogs.createGenericContinueCancelMessage(getContext(), "", message)
        .subscribe(subscriber);
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

      //installManager.suWarningAndPermission();

      DownloadFactory factory = new DownloadFactory();
      Download appDownload = factory.create(app, downloadAction);

      if (installManager.showWarning() ) {
        GenericDialogs.createGenericYesNoCancelMessage(context, null
            , AptoideUtils.StringU.getFormattedString(R.string.root_access_dialog) )
            .subscribe(eResponse -> {
              switch (eResponse) {
                case YES:
                  installManager.rootInstallAllowed(true);
                  break;
                case NO:
                  installManager.rootInstallAllowed(false);
                  break;
              }
            });
      }

      subscriptions.add(permissionManager.requestDownloadAccess(permissionRequest)
          .flatMap(success -> permissionManager.requestExternalStoragePermission(permissionRequest))
          .flatMap(success -> installManager.install(getContext(),
              new DownloadFactory().create(displayable.getPojo().getNodes().getMeta().getData(),
                  downloadAction)))
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(progress -> {
            manageDownload(progress, app);
          }, err -> {
            if (err instanceof SecurityException) {
              ShowMessage.asSnack(v, R.string.needs_permission_to_fs);
            }

            Logger.e(TAG, err);
          }));

      //downloadServiceHelper.startDownload(permissionRequest, appDownload)
      //    .observeOn(AndroidSchedulers.mainThread())
      //    .takeUntil(onGoingDownload -> shouldContinueListenDownload(
      //        onGoingDownload.getOverallDownloadStatus()))
      //    .subscribe(download -> {
      //      manageDownload(download, displayable, app);
      //    }, err -> {
      //      if (err instanceof SecurityException) {
      //        ShowMessage.asSnack(v, R.string.needs_permission_to_fs);
      //      }
      //
      //      Logger.e(TAG, err);
      //    });
      ShowMessage.asSnack(v, installOrUpgradeMsg);
      setupDownloadControls(app, new Progress<>(appDownload, true, 100, 0, 0, 0), displayable);
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

  @MainThread private void manageDownload(Progress<Download> progress, GetAppMeta.App app) {

    switch (progress.getRequest().getOverallDownloadStatus()) {

      case Download.PAUSED: {
        actionResume.setVisibility(View.VISIBLE);
        actionPause.setVisibility(View.GONE);
        break;
      }

      case Download.IN_QUEUE:
      case Download.PROGRESS: {
        downloadProgress.setProgress(progress.getCurrent());
        //textProgress.setText(download.getOverallProgress() + "% - " + AptoideUtils.StringU.formatBits((long) download.getSpeed()) +
        // "/s");
        textProgress.setText(progress.getCurrent() + "%");
        break;
      }

      case Download.ERROR: {
        setDownloadBarVisible(false);
        break;
      }

      case Download.COMPLETED: {
        Analytics.DownloadComplete.downloadComplete(app);
        Analytics.SourceDownloadComplete.downloadComplete(app.getId(), app.getPackageName());
        setDownloadBarVisible(false);

        if (!isUpdate) {
          if (minimalAd != null && minimalAd.getCpdUrl() != null) {
            DataproviderUtils.AdNetworksUtils.knockCpd(minimalAd);
          }
        }

        //install.observeOn(AndroidSchedulers.mainThread()).doOnNext(success -> {
        //  if (minimalAd != null && minimalAd.getCpdUrl() != null) {
        //    DataproviderUtils.AdNetworksUtils.knockCpd(minimalAd);
        //  }
        //}).subscribe(success -> {
        //  if (actionButton.getVisibility() == View.VISIBLE) {
        //    setupActionButton(R.string.open,
        //        v -> AptoideUtils.SystemU.openApp(app.getPackageName()));
        //
        //    if (displayable.isVisible()) {
        //      ((AppMenuOptions) ((FragmentShower) ctx).getLastV4()).setUnInstallMenuOptionVisible(
        //          () -> {
        //            displayable.uninstall(ctx).subscribe(aVoid -> {
        //            }, throwable -> throwable.printStackTrace());
        //          });
        //    }
        //  }
        //}, throwable -> throwable.printStackTrace());
        break;
      }
    }
  }

  private void setupDownloadControls(GetAppMeta.App app, Progress<Download> progress,
      AppViewInstallDisplayable displayable) {
    String md5 = app.getMd5();

    actionCancel.setOnClickListener(view -> {
      installManager.removeInstallationFile(getContext(), md5);
      setDownloadBarVisible(false);
    });

    actionPause.setOnClickListener(view -> {
      installManager.stopInstallation(getContext(), md5);
      actionResume.setVisibility(View.VISIBLE);
      actionPause.setVisibility(View.GONE);
    });

    actionResume.setOnClickListener(view -> {
      PermissionManager permissionManager = new PermissionManager();
      subscriptions.add(permissionManager.requestDownloadAccess(permissionRequest)
          .flatMap(permissionGranted -> permissionManager.requestExternalStoragePermission(
              (PermissionRequest) getContext()))
          .flatMap(success -> installManager.install(getContext(),
              new DownloadFactory().create(displayable.getPojo().getNodes().getMeta().getData(),
                  progress.getRequest().getAction())))
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(downloadProgress -> {
            if (actionPause.getVisibility() != View.VISIBLE) {
              actionResume.setVisibility(View.GONE);
              actionPause.setVisibility(View.VISIBLE);
            }
            manageDownload(downloadProgress, app);
          }, err -> {
            Logger.e(TAG, err);
          }));
    });

    setDownloadBarVisible(true);
    switch (progress.getState()) {
      case Progress.INACTIVE:
        downloadProgress.setProgress(progress.getCurrent());
        textProgress.setText(progress.getCurrent() + "%");
        actionResume.setVisibility(View.VISIBLE);
        actionPause.setVisibility(View.GONE);
      default:
        actionResume.setVisibility(View.GONE);
        actionPause.setVisibility(View.VISIBLE);
    }
  }

  private void setDownloadBarVisible(boolean visible) {
    // TODO: 22/09/16 diogo.loureiro crashes on downgrade
    installAndLatestVersionLayout.setVisibility(visible ? View.GONE : View.VISIBLE);
    downloadProgressLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
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
