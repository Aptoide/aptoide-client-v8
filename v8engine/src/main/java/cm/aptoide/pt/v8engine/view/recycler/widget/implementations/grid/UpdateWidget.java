/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.database.accessors.DeprecatedDatabase;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.implementations.AppViewFragment;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import cm.aptoide.pt.v8engine.util.FragmentUtils;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.UpdateDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import io.realm.Realm;
import lombok.Cleanup;
import rx.Subscription;

/**
 * Created by neuro on 17-05-2016.
 */
@Displayables({UpdateDisplayable.class})
public class UpdateWidget extends Widget<UpdateDisplayable> {

	private View updateRowRelativeLayout;
	private TextView labelTextView;
	private ImageView iconImageView;
	private ImageView imgUpdateLayout;
	private TextView installedVernameTextView;
	private TextView updateVernameTextView;
	private TextView textUpdateLayout;
	private ViewGroup updateButtonLayout;
	private Subscription subscription;
	private UpdateDisplayable displayable;
	private LinearLayout updateLayout;
	private ProgressBar progressBar;

	public UpdateWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		updateRowRelativeLayout = itemView.findViewById(R.id.updateRowRelativeLayout);
		labelTextView = (TextView) itemView.findViewById(R.id.name);
		iconImageView = (ImageView) itemView.findViewById(R.id.icon);
		installedVernameTextView = (TextView) itemView.findViewById(R.id.app_installed_version);
		updateVernameTextView = (TextView) itemView.findViewById(R.id.app_update_version);
		updateButtonLayout = (ViewGroup) itemView.findViewById(R.id.updateButtonLayout);
		updateLayout = (LinearLayout) itemView.findViewById(R.id.update_layout);
		imgUpdateLayout = (ImageView) itemView.findViewById(R.id.img_update_layout);
		textUpdateLayout = (TextView) itemView.findViewById(R.id.text_update_layout);
		progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);

	}

	@Override
	public void bindView(UpdateDisplayable updateDisplayable) {
		@Cleanup Realm realm = DeprecatedDatabase.get();
		this.displayable = updateDisplayable;
		final String packageName = updateDisplayable.getPackageName();
		Installed installed = DeprecatedDatabase.InstalledQ.get(packageName, realm);

		labelTextView.setText(updateDisplayable.getLabel());
		installedVernameTextView.setText(installed.getVersionName());
		updateVernameTextView.setText(updateDisplayable.getUpdateVersionName());
		ImageLoader.load(updateDisplayable.getIcon(), iconImageView);


		DownloadServiceHelper downloadManager = new DownloadServiceHelper(AptoideDownloadManager.getInstance(), new PermissionManager());
		// TODO: 8/23/16 trinkes try to change to worker thread
		downloadManager.getAllDownloads()
				.flatMapIterable(downloads -> downloads)
				.filter(download -> download.getAppId() == displayable.getAppId())
				.map(download -> download.getOverallDownloadStatus() == Download.PROGRESS || download.getOverallDownloadStatus() == Download.IN_QUEUE ||
						download
								.getOverallDownloadStatus() == Download.PENDING)
				.subscribe(showProgress -> {
					if (showProgress) {
						textUpdateLayout.setVisibility(View.GONE);
						imgUpdateLayout.setVisibility(View.GONE);
						progressBar.setVisibility(View.VISIBLE);
					} else {
						textUpdateLayout.setVisibility(View.VISIBLE);
						imgUpdateLayout.setVisibility(View.VISIBLE);
						progressBar.setVisibility(View.GONE);
					}
				});

		updateRowRelativeLayout.setOnClickListener(v -> FragmentUtils.replaceFragmentV4(getContext(), AppViewFragment.newInstance(updateDisplayable.getAppId())));
		updateRowRelativeLayout.setOnLongClickListener(v -> {
			AlertDialog.Builder builder = new AlertDialog.Builder(updateRowRelativeLayout.getContext());
			builder.setTitle(R.string.ignore_update)
					.setCancelable(true)
					.setNegativeButton(R.string.no, null)
					.setPositiveButton(R.string.yes, (dialog, which) -> {
						if (which == DialogInterface.BUTTON_POSITIVE) {
							downloadManager.removeDownload(displayable.getAppId());
							textUpdateLayout.setVisibility(View.VISIBLE);
							imgUpdateLayout.setVisibility(View.VISIBLE);
							progressBar.setVisibility(View.GONE);
							//updateRowRelativeLayout.setVisibility(View.GONE);
							DeprecatedDatabase.UpdatesQ.setExcluded(packageName, true, realm);
						}
						dialog.dismiss();
					});

			builder.create().show();

			return true;
		});

		updateLayout.setOnClickListener(v -> downloadManager.startDownload((PermissionRequest) UpdateWidget.this.getContext(), new DownloadFactory().create
				(displayable))
				.filter(download -> download.getOverallDownloadStatus() == Download.COMPLETED)
				.flatMap(download -> displayable.getInstallManager()
						.install(UpdateWidget.this.getContext(), (PermissionRequest) UpdateWidget.this.getContext(), download.getAppId()))
				.onErrorReturn(throwable -> null)
				.subscribe());
	}

	@Override
	public void onViewAttached() {
		if (subscription == null) {
			Context context = getContext();
			PermissionRequest permissionRequest = (PermissionRequest) context;

			subscription = RxView.clicks(updateButtonLayout).flatMap(click -> displayable.downloadAndInstall(context, permissionRequest))
					.retry()
					.subscribe();
		}
	}

	@Override
	public void onViewDetached() {
		if (subscription != null) {
			subscription.unsubscribe();
			subscription = null;
		}
	}
}
