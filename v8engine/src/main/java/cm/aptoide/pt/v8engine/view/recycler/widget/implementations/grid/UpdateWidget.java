/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.database.Database;
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
	private TextView installedVernameTextView;
	private TextView updateVernameTextView;
	private ViewGroup updateButtonLayout;
	private Subscription subscription;
	private UpdateDisplayable displayable;
	private LinearLayout updateLayout;

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
	}

	@Override
	public void bindView(UpdateDisplayable updateDisplayable) {
		@Cleanup Realm realm = Database.get();
		this.displayable = updateDisplayable;
		final String packageName = updateDisplayable.getPackageName();
		Installed installed = Database.InstalledQ.get(packageName, realm);

		labelTextView.setText(updateDisplayable.getLabel());
		installedVernameTextView.setText(installed.getVersionName());
		updateVernameTextView.setText(updateDisplayable.getUpdateVersionName());
		ImageLoader.load(updateDisplayable.getIcon(), iconImageView);

		updateRowRelativeLayout.setOnClickListener(v -> FragmentUtils.replaceFragmentV4(getContext(), AppViewFragment.newInstance(updateDisplayable.getAppId())));
		updateRowRelativeLayout.setOnLongClickListener(v -> {
			AlertDialog.Builder builder = new AlertDialog.Builder(updateRowRelativeLayout.getContext());
			builder.setTitle(R.string.ignore_update)
					.setCancelable(true)
					.setNegativeButton(R.string.no, null)
					.setPositiveButton(R.string.yes, (dialog, which) -> {
						if (which == DialogInterface.BUTTON_POSITIVE) {
							@Cleanup
							Realm realm1 = Database.get();
							Database.UpdatesQ.setExcluded(packageName, true, realm1);
							updateRowRelativeLayout.setVisibility(View.GONE);
						}
						dialog.dismiss();
					});

			builder.create().show();

			return true;
		});

		updateLayout.setOnClickListener(v -> new DownloadServiceHelper(AptoideDownloadManager.getInstance(), new PermissionManager()).startDownload(
				(PermissionRequest) getContext(), new DownloadFactory()
				.create(displayable))
				.filter(download -> download.getOverallDownloadStatus() == Download.COMPLETED)
				.flatMap(download -> displayable.getInstallManager().install(getContext(), (PermissionRequest) getContext(), download.getAppId()))
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
