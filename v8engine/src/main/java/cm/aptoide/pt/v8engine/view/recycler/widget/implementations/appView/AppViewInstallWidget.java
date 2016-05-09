/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 09/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView
		.AppViewInstallDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 06/05/16.
 */
@Displayables({AppViewInstallDisplayable.class})
public class AppViewInstallWidget extends Widget<AppViewInstallDisplayable> {

	//
	// downloading views
	//
	private RelativeLayout downloadProgressLayout;
	private CheckBox btinstallshare;
	private ProgressBar downloadProgress;
	private TextView textProgress;
	private LinearLayout actionContainer;
	private ImageView actionResume;
	private ImageView actionCancel;

	//
	// to (un)install views
	//

	private LinearLayout installAndLatestVersionLayout;
	private LinearLayout latestVersionLayout;
	private Button getLatestButton;
	private Button uninstallButton;
	private Button installButton;

	//
	// ctors
	//

	public AppViewInstallWidget(View itemView) {
		super(itemView);
	}

	//
	// instance methods
	//

	@Override
	protected void assignViews(View itemView) {
		// bind "download" views
		downloadProgressLayout =
				(RelativeLayout) itemView.findViewById(R.id.download_progress_layout);
		btinstallshare = (CheckBox) itemView.findViewById(R.id.btinstallshare);
		downloadProgress = (ProgressBar) itemView.findViewById(R.id.downloading_progress);
		textProgress = (TextView) itemView.findViewById(R.id.text_progress);
		actionContainer = (LinearLayout) itemView.findViewById(R.id.actionContainer);
		actionResume = (ImageView) itemView.findViewById(R.id.ic_action_resume);
		actionCancel = (ImageView) itemView.findViewById(R.id.ic_action_cancel);

		// bind "install and latest versions" views
		installAndLatestVersionLayout = (LinearLayout) itemView.findViewById(R.id.install_and_latest_version_layout);
		latestVersionLayout = (LinearLayout) itemView.findViewById(R.id.latestversion_layout);
		getLatestButton = (Button) itemView.findViewById(R.id.btn_get_latest);
		uninstallButton = (Button) itemView.findViewById(R.id.btn_uninstall);
		installButton = (Button) itemView.findViewById(R.id.btn_install);

	}

	@Override
	public void bindView(AppViewInstallDisplayable displayable) {

		GetAppMeta.App pojo = displayable.getPojo();

		// TODO
	}

	/*
	private void populateDownloadUI() {
		setShareTimeLineButton();

		getView().findViewById(R.id.ic_action_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (downloadService != null) {
					FlurryAgent.logEvent("App_View_Canceled_Download");
					downloadService.stopDownload(downloadId);
				}

			}
		});

		getView().findViewById(R.id.ic_action_resume).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (downloadService != null) {
					downloadService.resumeDownload(downloadId);
				}
			}
		});

		if (downloadService != null && downloadService.getDownload(downloadId).getDownload() != null) {
			onDownloadUpdate(downloadService.getDownload(downloadId).getDownload());
		}

		if(refreshOnResume) {
			spiceManager.removeDataFromCache(GetApkInfoJson.class);
			refreshOnResume = false;
		}

	}
	*/

	/**
	 * Hides the layout with the install button and shows the download progress bar.
	 * Creates a {@link Download} object and send it to the {@link DownloadService}
	 *
	 */
	/*
	private void download() {
		this.showRootDialog();

		Download download = new Download();
		download.setId(downloadId);
		download.setName(appName);
		download.setVersion(versionName);
		download.setIcon(iconUrl);
		download.setPackageName(packageName);
		download.setMd5(md5sum);
		download.setPaid(isPaidApp());

		if (!isUpdate) {
			download.setCpiUrl(getActivity().getIntent().getStringExtra("cpi"));
		}
		try {
			//TODO Main thread synchronization
			downloadServiceLatch.await();
		} catch (InterruptedException e) {
			Logger.printException(e);
		}
		if (downloadService == null) {
			return;
		}
		downloadService.downloadFromV7WithObb(path, altPath, md5sum, fileSize, appName, packageName, versionName, iconUrl, appId, pay != null, obb, download, permissions);

		isFromActivityResult = false;
		autoDownload = false;

		populateDownloadUI();
	}
	*/

	/*
	private void onDownloadUpdate(Download download) {
		if (download != null && download.getId() == downloadId) {
			mInstallAndLatestVersionLayout.setVisibility(View.GONE);
			mDownloadProgressLayout.setVisibility(View.VISIBLE);

			switch (download.getDownloadState()) {
				case ACTIVE:
					mActionResume.setVisibility(View.GONE);
					mDownloadingProgress.setIndeterminate(false);
					mDownloadingProgress.setProgress(download.getProgress());
					mProgressText.setText(download.getProgress() + "% - " + AptoideUtils.StringUtils.formatBits((long) download.getSpeed()) + "/s");
					break;
				case INACTIVE:
					break;
				case COMPLETE:
					mInstallAndLatestVersionLayout.setVisibility(View.VISIBLE);
					mDownloadProgressLayout.setVisibility(View.GONE);
					break;
				case PENDING:
					mActionResume.setVisibility(View.GONE);
					mDownloadingProgress.setIndeterminate(false);
					mDownloadingProgress.setProgress(download.getProgress());
					mProgressText.setText(getString(R.string.download_pending));
					break;
				case ERROR:
					mActionResume.setVisibility(View.VISIBLE);
					mProgressText.setText(download.getDownloadState().name());
					mDownloadingProgress.setIndeterminate(false);
					mDownloadingProgress.setProgress(download.getProgress());
					break;
			}
		}
	}
	*/
}
