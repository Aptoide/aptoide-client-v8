/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt.app.view.widget;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.app.AppBoughtReceiver;
import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.app.view.AppViewNavigator;
import cm.aptoide.pt.app.view.displayable.AppViewInstallDisplayable;
import cm.aptoide.pt.crashreports.CrashReport;
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
import cm.aptoide.pt.download.AppContext;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.download.InstallType;
import cm.aptoide.pt.download.Origin;
import cm.aptoide.pt.install.Install;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.install.view.InstallWarningDialog;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.timeline.SocialRepository;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.SimpleSubscriber;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.view.dialog.SharePreviewDialog;
import cm.aptoide.pt.view.recycler.widget.Widget;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created on 06/05/16.
 */
public class AppViewInstallWidget extends Widget<AppViewInstallDisplayable> {

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
    private AptoideAccountManager accountManager;
    private AppViewInstallDisplayable displayable;
    private SocialRepository socialRepository;
    private DownloadFactory downloadFactory;
    private PermissionService permissionService;
    private PermissionManager permissionManager;
    private SharedPreferences sharedPreferences;
    private AccountNavigator accountNavigator;
    private AppViewNavigator appViewNavigator;
    private CrashReport crashReport;
    private String marketName;
    private boolean isCreateStoreUserPrivacyEnabled;
    private boolean isMultiStoreSearch;
    private String defaultStoreName;
    private int campaignId;
    private String abTestGroup;
    private AppViewAnalytics appViewAnalytics;
    private InstallAnalytics installAnalytics;

    public AppViewInstallWidget(View itemView) {
        super(itemView);
    }

    @Override
    protected void assignViews(View itemView) {
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

    @Override
    public void unbindView() {
        super.unbindView();
        displayable.setInstallButton(null);
        displayable = null;
    }

    @Override
    public void bindView(AppViewInstallDisplayable displayable) {
        this.displayable = displayable;
        this.displayable.setInstallButton(actionButton);
        crashReport = CrashReport.getInstance();
        campaignId = displayable.getCampaignId();
        abTestGroup = displayable.getAbTestingGroup();
        accountNavigator = ((ActivityResultNavigator) getContext()).getAccountNavigator();
        final AptoideApplication application =
                (AptoideApplication) getContext().getApplicationContext();
        installAnalytics = displayable.getInstallAnalytics();
        isCreateStoreUserPrivacyEnabled = application.isCreateStoreUserPrivacyEnabled();
        marketName = application.getMarketName();
        sharedPreferences = application.getDefaultSharedPreferences();
        isMultiStoreSearch = application.hasMultiStoreSearch();
        defaultStoreName = application.getDefaultStoreName();
        final OkHttpClient httpClient = application.getDefaultClient();
        final Converter.Factory converterFactory = WebService.getDefaultConverter();
        accountManager = application.getAccountManager();
        installManager = application.getInstallManager();
        BodyInterceptor<BaseBody> bodyInterceptor =
                application.getAccountSettingsBodyInterceptorPoolV7();
        final TokenInvalidator tokenInvalidator = application.getTokenInvalidator();
        appViewAnalytics = displayable.getAppViewAnalytics();
        downloadFactory = displayable.getDownloadFactory();
        socialRepository =
                new SocialRepository(accountManager, bodyInterceptor, converterFactory, httpClient,
                        application.getTimelineAnalytics(), tokenInvalidator, sharedPreferences);

        appViewNavigator = getAppViewNavigator();

        GetApp getApp = this.displayable.getPojo();
        GetAppMeta.App currentApp = getApp.getNodes()
                .getMeta()
                .getData();
        versionName.setText(currentApp.getFile()
                .getVername());

        compositeSubscription.add(RxView.clicks(otherVersions)
                .subscribe(__ -> {
                    displayable.getAppViewAnalytics()
                            .sendOtherVersionsEvent();
                    appViewNavigator.navigateToOtherVersions(currentApp.getName(), currentApp.getIcon(),
                            currentApp.getPackageName());
                }, err -> crashReport.log(err)));

        //setup the ui
        compositeSubscription.add(displayable.getInstallState()
                .first()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(installationProgress -> updateUi(displayable, installationProgress, true, getApp))
                .subscribe(viewUpdated -> {
                }, throwable -> crashReport.log(throwable)));

        //listen ui events
        compositeSubscription.add(displayable.getInstallState()
                .skip(1)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(
                        installationProgress -> updateUi(displayable, installationProgress, false, getApp))
                .subscribe(viewUpdated -> {
                }, throwable -> crashReport.log(throwable)));

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

    @NonNull
    private void updateUninstalledUi(AppViewInstallDisplayable displayable, GetApp getApp,
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
                @Override
                public void appBought(long appId, String path) {
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
        appViewNavigator.buyApp(app);
    }

    private View.OnClickListener downgradeListener(final GetAppMeta.App app) {
        return view -> {
            final Context context = view.getContext();
            final PermissionService permissionRequest = (PermissionService) getContext();
            displayable.installAppClicked(InstallType.DOWNGRADE, Origin.DOWNGRADE);
            permissionRequest.requestAccessToExternalFileSystem(() -> {

                showMessageOKCancel(getContext().getResources()
                                .getString(R.string.downgrade_warning_dialog),
                        new SimpleSubscriber<GenericDialogs.EResponse>() {

                            @Override
                            public void onNext(GenericDialogs.EResponse eResponse) {
                                super.onNext(eResponse);
                                if (eResponse == GenericDialogs.EResponse.YES) {

                                    ShowMessage.asSnack(view, R.string.downgrading_msg);

                                    DownloadFactory factory = new DownloadFactory(marketName);
                                    Download appDownload = factory.create(app, Download.ACTION_DOWNGRADE);
                                    showRootInstallWarningPopup(context);
                                    compositeSubscription.add(
                                            new PermissionManager().requestDownloadAccess(permissionRequest)
                                                    .flatMap(success -> installManager.install(appDownload)
                                                            .toObservable()
                                                            .doOnSubscribe(() -> setupEvents(appDownload, InstallType.DOWNGRADE,
                                                                    Origin.DOWNGRADE)))
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(progress -> {
                                                        // TODO: 12/07/2017 this code doesnt run
                                                        Logger.d(TAG, "Installing");
                                                    }, throwable -> crashReport.log(throwable)));
                                    appViewAnalytics.downgradeDialogContinue();
                                } else {
                                    appViewAnalytics.downgradeDialogCancel();
                                }
                            }
                        });
            }, () -> {
                ShowMessage.asSnack(view, R.string.needs_permission_to_fs);
            });
        };
    }

    private void setupEvents(Download download, InstallType installType, Origin origin) {
        appViewAnalytics.setupDownloadEvents(download, campaignId, abTestGroup,
                AnalyticsManager.Action.CLICK);
        installAnalytics.installStarted(download.getPackageName(), download.getVersionCode(),
                installType, AnalyticsManager.Action.INSTALL, AppContext.APPVIEW, origin, campaignId,
                abTestGroup);
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
            ManagerPreferences.setNotLoggedInInstallClicks(
                    ManagerPreferences.getNotLoggedInInstallClicks(sharedPreferences) + 1, sharedPreferences);
            if (installOrUpgradeMsg == R.string.installing_msg) {
                appViewAnalytics.clickOnInstallButton(app);
            }
            displayable.installAppClicked(isUpdate ? InstallType.UPDATE : InstallType.INSTALL,
                    isUpdate ? Origin.UPDATE : Origin.INSTALL);

            showRootInstallWarningPopup(context);
            compositeSubscription.add(permissionManager.requestDownloadAccess(permissionService)
                    .flatMap(success -> permissionManager.requestExternalStoragePermission(permissionService))
                    .map(success -> new DownloadFactory(marketName).create(displayable.getPojo()
                            .getNodes()
                            .getMeta()
                            .getData(), downloadAction))
                    .flatMapCompletable(download -> {
                        if (!displayable.getAppViewFragment()
                                .isSuggestedShowing()) {
                            displayable.getAppViewFragment()
                                    .showSuggestedApps();
                        }
                        return installManager.install(download)
                                .doOnSubscribe(subscription -> setupEvents(download,
                                        isUpdate ? InstallType.UPDATE : InstallType.INSTALL,
                                        isUpdate ? Origin.UPDATE : Origin.INSTALL))
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnCompleted(() -> {
                                    if (accountManager.isLoggedIn() && ManagerPreferences.isShowPreviewDialog(
                                            sharedPreferences) && isCreateStoreUserPrivacyEnabled) {
                                        SharePreviewDialog sharePreviewDialog =
                                                new SharePreviewDialog(displayable, accountManager, true,
                                                        SharePreviewDialog.SharePreviewOpenMode.SHARE,
                                                        displayable.getTimelineAnalytics(), sharedPreferences);
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
                                    } else if (!accountManager.isLoggedIn()
                                            && (ManagerPreferences.getNotLoggedInInstallClicks(sharedPreferences) == 2
                                            || ManagerPreferences.getNotLoggedInInstallClicks(sharedPreferences) == 4)) {
                                        accountNavigator.navigateToNotLoggedInViewForResult(
                                                AppViewFragment.LOGIN_REQUEST_CODE, app);
                                    }
                                    ShowMessage.asSnack(v, installOrUpgradeMsg);
                                });
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(progress -> {
                    }, err -> {
                        if (err instanceof SecurityException) {
                            ShowMessage.asSnack(v, R.string.needs_permission_to_fs);
                        }
                        crashReport.log(err);
                    }));
        };

        findTrustedVersion(app, appVersions);
        final boolean hasTrustedVersion = trustedVersion != null;

        final View.OnClickListener onSearchTrustedAppHandler = v -> {
            if (hasTrustedVersion) {
                appViewNavigator.navigateToAppView(trustedVersion.getId(), trustedVersion.getPackageName(),
                        "");
                return;
            }
            appViewNavigator.navigateToSearch(app.getName(), true);
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
                        onSearchTrustedAppHandler, marketName).getDialog()
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
                }, throwable -> crashReport.log(throwable));
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
                                .doOnSubscribe(() -> setupEvents(download, getInstallType(download.getAction()),
                                        getOrigin(download.getAction()))))
                        .subscribe(downloadProgress -> Logger.d(TAG, "Installing"),
                                err -> crashReport.log(err)));
            });
        }
    }

    private Origin getOrigin(int action) {
        switch (action) {
            default:
            case Download.ACTION_INSTALL:
                return Origin.INSTALL;
            case Download.ACTION_UPDATE:
                return Origin.UPDATE;
            case Download.ACTION_DOWNGRADE:
                return Origin.DOWNGRADE;
        }
    }

    private InstallType getInstallType(int action) {
        switch (action) {
            default:
            case Download.ACTION_INSTALL:
                return InstallType.INSTALL;
            case Download.ACTION_UPDATE:
                return InstallType.UPDATE;
            case Download.ACTION_DOWNGRADE:
                return InstallType.DOWNGRADE;
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

    private AppViewNavigator getAppViewNavigator() {
        return new AppViewNavigator(getFragmentNavigator(), getActivityNavigator(), isMultiStoreSearch,
                defaultStoreName);
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
