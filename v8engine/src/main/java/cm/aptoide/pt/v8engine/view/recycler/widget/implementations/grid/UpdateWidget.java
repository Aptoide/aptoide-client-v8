/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.implementations.AppViewFragment;
import cm.aptoide.pt.v8engine.util.FragmentUtils;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.UpdateDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import io.realm.Realm;
import lombok.Cleanup;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by neuro on 17-05-2016.
 */
@Displayables({UpdateDisplayable.class})
public class UpdateWidget extends Widget<UpdateDisplayable> {

	private static final String TAG = UpdateWidget.class.getSimpleName();
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
	private Subscription showSpinnerSubscription;

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
		this.displayable = updateDisplayable;
		final String packageName = updateDisplayable.getPackageName();
		Installed installed = Database.InstalledQ.get(packageName, displayable.getRealm());
		displayable.setPauseAction(this::onViewDetached);
		displayable.setResumeAction(this::onViewAttached);
		labelTextView.setText(updateDisplayable.getLabel());
		installedVernameTextView.setText(installed.getVersionName());
		updateVernameTextView.setText(updateDisplayable.getUpdateVersionName());
		ImageLoader.load(updateDisplayable.getIcon(), iconImageView);
		displayable.getDownloadManager()
				.getDownload(displayable.getAppId())
				.first()
				.map(this::shouldDisplayProgress)
				.subscribe(this::showProgress, noDownloadFound -> Logger.d(TAG, "not updating yet"));
		updateRowRelativeLayout.setOnClickListener(v -> FragmentUtils.replaceFragmentV4(getContext(), AppViewFragment.newInstance(updateDisplayable.getAppId()
		)));
		updateRowRelativeLayout.setOnLongClickListener(v -> {
			AlertDialog.Builder builder = new AlertDialog.Builder(updateRowRelativeLayout.getContext());
			builder.setTitle(R.string.ignore_update)
					.setCancelable(true)
					.setNegativeButton(R.string.no, null)
					.setPositiveButton(R.string.yes, (dialog, which) -> {
						if (which == DialogInterface.BUTTON_POSITIVE) {
							@Cleanup Realm realm1 = Database.get();
							Database.UpdatesQ.setExcluded(packageName, true, realm1);
							updateRowRelativeLayout.setVisibility(View.GONE);
						}
						dialog.dismiss();
					});

			builder.create().show();

			return true;
		});
	}

	@Override
	public void onViewAttached() {
		if (subscription == null || subscription.isUnsubscribed()) {
			subscription = RxView.clicks(updateButtonLayout).flatMap(click -> {
				displayable.downloadAndInstall(getContext()).subscribe();
				return null;
			}).retry().subscribe();
		}
		if (showSpinnerSubscription == null || showSpinnerSubscription.isUnsubscribed()) {
			DownloadServiceHelper downloadManager = new DownloadServiceHelper(AptoideDownloadManager.getInstance(), new PermissionManager());
			showSpinnerSubscription = downloadManager.getAllDownloads()
					.sample(1, TimeUnit.SECONDS)
					.flatMapIterable(downloads -> downloads)
					.filter(download -> download.getAppId() == displayable.getAppId())
					.map(this::shouldDisplayProgress)
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(this::showProgress, Throwable::printStackTrace);
		}
	}

	@Override
	public void onViewDetached() {
		if (subscription != null) {
			subscription.unsubscribe();
			subscription = null;
		}
		if (showSpinnerSubscription != null && !showSpinnerSubscription.isUnsubscribed()) {
			showSpinnerSubscription.unsubscribe();
			showSpinnerSubscription = null;
		}
	}

	private boolean shouldDisplayProgress(Download download) {
		return download.getOverallDownloadStatus() == Download.PROGRESS || download.getOverallDownloadStatus() == Download.IN_QUEUE ||
				download.getOverallDownloadStatus() == Download.PENDING;
	}

	private void showProgress(Boolean showProgress) {
		if (showProgress) {
			textUpdateLayout.setVisibility(View.GONE);
			imgUpdateLayout.setVisibility(View.GONE);
			progressBar.setVisibility(View.VISIBLE);
		} else {
			textUpdateLayout.setVisibility(View.VISIBLE);
			imgUpdateLayout.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
		}
	}
}
