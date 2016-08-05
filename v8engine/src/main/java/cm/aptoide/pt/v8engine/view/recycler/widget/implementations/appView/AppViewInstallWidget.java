/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 05/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
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
import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.dataprovider.model.MinimalAd;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.model.v3.GetApkInfoJson;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.model.v7.Malware;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.listapp.ListAppVersions;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.dialog.InstallWarningDialog;
import cm.aptoide.pt.v8engine.fragment.implementations.AppViewFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.OtherVersionsFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.SearchFragment;
import cm.aptoide.pt.v8engine.interfaces.AppMenuOptions;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.receivers.AppBoughtReceiver;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewInstallDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import io.realm.Realm;
import lombok.Cleanup;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by sithengineer on 06/05/16.
 */
@Displayables({AppViewInstallDisplayable.class})
public class AppViewInstallWidget extends Widget<AppViewInstallDisplayable> {

	private RelativeLayout downloadProgressLayout;
	private RelativeLayout installAndLatestVersionLayout;

	//
	// downloading views
	//
	private CheckBox shareInTimeline; // FIXME: 27/07/16 sithengineer what does this flag do ??
	private ProgressBar downloadProgress;
	private TextView textProgress;
	private ImageView actionPauseResume;
	private ImageView actionCancel;

	// get app, upgrade and downgrade button
	private Button actionButton;

	// app info
	private TextView versionName;
	private TextView latestAvailableLabel;
	private TextView otherVersions;
	private MinimalAd minimalAd;

	private App trustedVersion;

	public AppViewInstallWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		downloadProgressLayout = (RelativeLayout) itemView.findViewById(R.id.download_progress_layout);
		installAndLatestVersionLayout = (RelativeLayout) itemView.findViewById(R.id.install_and_latest_version_layout);
		shareInTimeline = (CheckBox) itemView.findViewById(R.id.share_in_timeline);
		downloadProgress = (ProgressBar) itemView.findViewById(R.id.download_progress);
		textProgress = (TextView) itemView.findViewById(R.id.text_progress);
		actionPauseResume = (ImageView) itemView.findViewById(R.id.ic_action_pause_resume);
		actionCancel = (ImageView) itemView.findViewById(R.id.ic_action_cancel);
		actionButton = (Button) itemView.findViewById(R.id.action_btn);
		versionName = (TextView) itemView.findViewById(R.id.store_version_name);
		otherVersions = (TextView) itemView.findViewById(R.id.other_versions);
		latestAvailableLabel = (TextView) itemView.findViewById(R.id.latest_available_label);
	}

	@Override
	public void bindView(AppViewInstallDisplayable displayable) {

		minimalAd = displayable.getMinimalAd();
		GetApp getApp = displayable.getPojo();
		GetAppMeta.App app = getApp.getNodes().getMeta().getData();
		final FragmentShower fragmentShower = ((FragmentShower) getContext());

		versionName.setText(app.getFile().getVername());
		otherVersions.setOnClickListener(v -> {
			OtherVersionsFragment fragment = OtherVersionsFragment.newInstance(app.getName(), app.getIcon(), app.getPackageName());
			fragmentShower.pushFragmentV4(fragment);
		});

		@Cleanup
		Realm realm = Database.get();
		String packageName = app.getPackageName();
		Installed installed = Database.InstalledQ.get(packageName, realm);

		//check if the app is installed
		if (installed == null) {
			// app not installed
			setupInstallButton(getApp, displayable);
			((AppMenuOptions) fragmentShower.getLastV4()).setUnInstallMenuOptionVisible(null);
		} else {
			// app installed

			// setup un-install button in menu
			//			((AppMenuOptions) fragmentShower.getLastV4()).setUnInstallMenuOptionVisible(new Listeners().newUninstallListener(app, itemView,
			// installed
			//					.getPackageName(), displayable));
			((AppMenuOptions) fragmentShower.getLastV4()).setUnInstallMenuOptionVisible(() -> {
				displayable.uninstall(getContext(), app).subscribe(aVoid -> {
				});
			});

			// is it an upgrade, downgrade or open app?
			setupUpgradeDowngradeOpenActions(getApp, installed, displayable);
		}
	}

	@Override
	public void onViewAttached() {
	}

	@Override
	public void onViewDetached() {
	}

	private void setupUpgradeDowngradeOpenActions(GetApp getApp, Installed installed, AppViewInstallDisplayable displayable) {

		GetAppMeta.App app = getApp.getNodes().getMeta().getData();

		if (!isLatestAvailable(app, getApp.getNodes().getVersions()) || app.getFile().getVercode() > installed.getVersionCode()) {
			actionButton.setText(R.string.update);
			actionButton.setOnClickListener(installOrUpgradeListener(true, app, getApp.getNodes().getVersions(), displayable));
		} else if (app.getFile().getVercode() < installed.getVersionCode()) {
			actionButton.setText(R.string.downgrade);
			actionButton.setOnClickListener(downgradeListener(app, displayable));
		} else {
			actionButton.setText(R.string.open);
			latestAvailableLabel.setVisibility(View.VISIBLE);
			actionButton.setOnClickListener(v -> AptoideUtils.SystemU.openApp(app.getPackageName()));
		}
	}

	public void setupInstallButton(GetApp getApp, AppViewInstallDisplayable displayable) {

		GetAppMeta.App app = getApp.getNodes().getMeta().getData();

		GetApkInfoJson.Payment payment = displayable.getPayment();
		//check if the app is paid
		if (payment != null && payment.isPaidApp()) {
			// TODO: 05/08/16 sithengineer replace that for placeholders in resources as soon as we are able to add new strings for translation
			actionButton.setText(getContext().getString(R.string.buy) + " (" + payment.symbol + " " + payment.amount + ")");
			actionButton.setOnClickListener(v -> {
				displayable.buyApp(getContext(), app).subscribe();
			});
			new AppBoughtReceiver() {
				@Override
				public void appBought(long appId) {
					if (app.getId() == appId) {
						installOrUpgradeListener(true, app, getApp.getNodes().getVersions(), displayable);
					}
				}
			};
		} else {
			actionButton.setText(R.string.install);
			actionButton.setOnClickListener(installOrUpgradeListener(false, app, getApp.getNodes().getVersions(), displayable));
			if (displayable.isShouldInstall()) {
				actionButton.postDelayed(() -> {
					if (displayable.isVisible()) {
						actionButton.performClick();
					}
				}, 1000);
			}
		}
	}

	private boolean isLatestAvailable(GetAppMeta.App app, @Nullable ListAppVersions appVersions) {
		boolean canCompare = appVersions != null && appVersions.getList() != null && !appVersions.getList().isEmpty();
		return !canCompare || (app.getFile().getVercode() >= appVersions.getList().get(0).getFile().getVercode());
	}

	//	private View.OnClickListener getLatestListener(GetApp getApp) {
	//		return v -> {
	//			long latestAppId = getApp.getNodes().getVersions().getList().get(0).getId();
	//			FragmentUtils.replaceFragmentV4(getContext(), AppViewFragment.newInstance(latestAppId));
	//		};
	//	}

	private View.OnClickListener downgradeListener(final GetAppMeta.App app, AppViewInstallDisplayable displayable) {
		return view -> {
			final Context context = view.getContext();
			ContextWrapper contextWrapper = (ContextWrapper) context;
			final PermissionRequest permissionRequest = ((PermissionRequest) contextWrapper.getBaseContext());

			permissionRequest.requestAccessToExternalFileSystem(() -> {
				ShowMessage.asSnack(view, R.string.downgrading_msg);

				DownloadFactory factory = new DownloadFactory();
				Download appDownload = factory.create(app);
				DownloadServiceHelper downloadServiceHelper = new DownloadServiceHelper(AptoideDownloadManager.getInstance(), new PermissionManager());
				downloadServiceHelper.startDownload(permissionRequest, appDownload).subscribe(download -> {
					if (download.getOverallDownloadStatus() == Download.COMPLETED) {
						//final String packageName = app.getPackageName();
						//final FileToDownload downloadedFile = download.getFilesToDownload().get(0);
						displayable.downgrade(getContext(), app).subscribe();
					}
				});
			}, () -> {
				ShowMessage.asSnack(view, R.string.needs_permission_to_fs);
			});
		};
	}

	public View.OnClickListener installOrUpgradeListener(boolean isUpdate, GetAppMeta.App app, ListAppVersions appVersions, AppViewInstallDisplayable
			displayable) {

		final Context context = getContext();

		@StringRes
		final int installOrUpgradeMsg = isUpdate ? R.string.updating_msg : R.string.installing_msg;
		final View.OnClickListener installHandler = v -> {
			ContextWrapper ctx = (ContextWrapper) v.getContext();
			final PermissionRequest permissionRequest = ((PermissionRequest) ctx.getBaseContext());

			permissionRequest.requestAccessToExternalFileSystem(() -> {

				ShowMessage.asSnack(v, installOrUpgradeMsg);

				DownloadFactory factory = new DownloadFactory();
				Download appDownload = factory.create(app);
				DownloadServiceHelper downloadServiceHelper = new DownloadServiceHelper(AptoideDownloadManager.getInstance(), new PermissionManager());

				actionPauseResume.setOnClickListener(view -> {
					downloadServiceHelper.pauseDownload(app.getId());

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
						actionPauseResume.setBackgroundColor(context.getColor(R.color.default_color));
					} else {
						actionPauseResume.setBackgroundColor(context.getResources().getColor(R.color.default_color));
					}
				});

				actionCancel.setOnClickListener(view -> {
					//download.cancel();
					//downloadServiceHelper.cancelDownload(app.getId());
					downloadServiceHelper.pauseDownload(app.getId());

					installAndLatestVersionLayout.setVisibility(View.VISIBLE);
					downloadProgressLayout.setVisibility(View.GONE);
				});

				installAndLatestVersionLayout.setVisibility(View.GONE);
				downloadProgressLayout.setVisibility(View.VISIBLE);
				actionPauseResume.setImageResource(R.drawable.ic_pause);

				downloadServiceHelper.startDownload(permissionRequest, appDownload).subscribe(download -> {

					// TODO: 19/07/16 sithengineer logic to show / hide pause / resume download and show download progress

					switch (download.getOverallDownloadStatus()) {

						case Download.PAUSED: {
							actionPauseResume.setOnClickListener(view -> {

								downloadServiceHelper.startDownload(permissionRequest, download);

								//actionPauseResume.setImageResource(R.drawable.ic_); // missing the changing of the drawable
								if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
									actionPauseResume.setBackgroundColor(context.getColor(android.R.color.transparent));
								} else {
									actionPauseResume.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
								}
							});
							break;
						}

						case Download.PROGRESS: {
							downloadProgress.setProgress(download.getOverallProgress());
							//textProgress.setText(download.getOverallProgress() + "% - " + AptoideUtils.StringU.formatBits((long) download.getSpeed()) +
							// "/s");
							textProgress.setText(download.getOverallProgress() + "%");
							break;
						}

						case Download.ERROR: {
							installAndLatestVersionLayout.setVisibility(View.VISIBLE);
							downloadProgressLayout.setVisibility(View.GONE);
							break;
						}

						case Download.COMPLETED: {
							installAndLatestVersionLayout.setVisibility(View.VISIBLE);
							downloadProgressLayout.setVisibility(View.GONE);

							displayable.install(context, app).observeOn(AndroidSchedulers.mainThread()).doOnNext(success -> {
								if (minimalAd != null && minimalAd.getCpdUrl() != null) {
									DataproviderUtils.AdNetworksUtils.knockCpd(minimalAd);
								}
							}).subscribe(success -> {
								if (actionButton.getVisibility() == View.VISIBLE) {
									actionButton.setText(R.string.open);
									// FIXME: 20/07/16 sithengineer refactor this ugly code
									if (displayable.isVisible()) {
										((AppMenuOptions) ((FragmentShower) context).getLastV4()).setUnInstallMenuOptionVisible(() -> {
											displayable.uninstall(ctx, app).subscribe();
										});
									}
								}
							});
							break;
						}
					}
				}, Throwable::printStackTrace);
			}, () -> {
				ShowMessage.asSnack(v, R.string.needs_permission_to_fs);
			});
		};

		findTrustedVersion(app, appVersions);
		final boolean hasTrustedVersion = trustedVersion != null;

		final View.OnClickListener onSearchHandler = v -> {
			Fragment fragment;
			if (hasTrustedVersion) {
				// go to app view of the trusted version
				fragment = AppViewFragment.newInstance(trustedVersion.getId());
			} else {
				// search for a trusted version
				fragment = SearchFragment.newInstance(app.getName(), true);
			}
			((FragmentShower) context).pushFragmentV4(fragment);
		};

		return v -> {
			final Malware.Rank rank = app.getFile().getMalware().getRank();
			if (!Malware.Rank.TRUSTED.equals(rank)) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				View alertView = LayoutInflater.from(context).inflate(R.layout.dialog_install_warning, null);
				builder.setView(alertView);
				new InstallWarningDialog(rank, hasTrustedVersion, context, installHandler, onSearchHandler).getDialog().show();
			}
		};
	}

	private void findTrustedVersion(GetAppMeta.App app, ListAppVersions appVersions) {

		if (app.getFile() != null && app.getFile().getMalware() != null && !Malware.Rank.TRUSTED.equals(app.getFile().getMalware().getRank())) {

			for (App version : appVersions.getList()) {
				if (app.getId() != version.getId() && version.getFile() != null && version.getFile().getMalware() != null && Malware.Rank.TRUSTED.equals
						(version

								.getFile().getMalware().getRank())) {
					trustedVersion = version;
				}
			}
		}
	}
}
