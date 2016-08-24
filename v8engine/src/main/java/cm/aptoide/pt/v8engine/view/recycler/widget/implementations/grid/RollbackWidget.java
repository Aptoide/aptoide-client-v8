/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 28/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RollbackDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 14/06/16.
 */
public class RollbackWidget extends Widget<RollbackDisplayable> {

	private static final AptoideUtils.DateTimeU DATE_TIME_U = AptoideUtils.DateTimeU.getInstance();

	private static final String TAG = RollbackWidget.class.getSimpleName();

	private ImageView appIcon;
	private TextView appName;
	private TextView appUpdateVersion;
	private TextView appState;
	private TextView rollbackAction;

	public RollbackWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		appIcon = (ImageView) itemView.findViewById(R.id.app_icon);
		appName = (TextView) itemView.findViewById(R.id.app_name);
		appState = (TextView) itemView.findViewById(R.id.app_state);
		appUpdateVersion = (TextView) itemView.findViewById(R.id.app_update_version);
		rollbackAction = (TextView) itemView.findViewById(R.id.ic_action);
	}

	@Override
	public void bindView(RollbackDisplayable displayable) {
		final Rollback pojo = displayable.getPojo();

		ImageLoader.load(pojo.getIcon(), appIcon);
		appName.setText(pojo.getAppName());
		appUpdateVersion.setText(pojo.getVersionName());
		appState.setText(
			String.format(
				getContext().getString(R.string.rollback_updated_at), DATE_TIME_U.getTimeDiffString(getContext(), pojo.getTimestamp())
			)
		);

		rollbackAction.setOnClickListener( view -> {

			//			@Cleanup
			//			Realm realm = Database.get();
			//			Database.RollbackQ.upadteRollbackWithAction(realm, pojo, Rollback.Action.UPDATE);

			final Context context = view.getContext();
			ContextWrapper contextWrapper = (ContextWrapper) context;
			final PermissionRequest permissionRequest = ((PermissionRequest) contextWrapper.getBaseContext());

			permissionRequest.requestAccessToExternalFileSystem(() -> {
				final DownloadServiceHelper downloadServiceHelper = new DownloadServiceHelper(AptoideDownloadManager.getInstance(), new PermissionManager());
				final Download appDownload = displayable.getDownloadFromPojo();
				Rollback.Action action = Rollback.Action.valueOf(pojo.getAction());
				switch (action) {
					case DOWNGRADE:
						// find app update and download it, uninstall current and install update
						// TODO: 28/07/16 sithengineer

						ShowMessage.asSnack(view, R.string.updating_msg);
						downloadServiceHelper.startDownload(permissionRequest, appDownload).subscribe(download -> {
							if (download.getOverallDownloadStatus() == Download.COMPLETED) {
								//final String packageName = app.getPackageName();
								//final FileToDownload downloadedFile = download.getFilesToDownload().get(0);
								//displayable.upgrade(context).subscribe();
							}
						});

						break;

					case INSTALL:
						//only if the app is installed
						//ShowMessage.asSnack(view, R.string.uninstall_msg);
						ShowMessage.asSnack(view, "R.string.uninstall_msg");
						displayable.uninstall(getContext(), appDownload).subscribe();
						break;

					case UNINSTALL:
						ShowMessage.asSnack(view, R.string.installing_msg);
						downloadServiceHelper.startDownload(permissionRequest, appDownload).subscribe(download -> {
							if (download.getOverallDownloadStatus() == Download.COMPLETED) {
								//final String packageName = app.getPackageName();
								//final FileToDownload downloadedFile = download.getFilesToDownload().get(0);
								displayable.install(context, (PermissionRequest) context, appDownload.getAppId()).subscribe();
							}
						});
						break;

					case UPDATE:
						// find current installed app. download previous, uninstall current and install previous
						// TODO: 28/07/16 sithengineer

						ShowMessage.asSnack(view,R.string.downgrading_msg);
						downloadServiceHelper.startDownload(permissionRequest, appDownload).subscribe(download -> {
							if (download.getOverallDownloadStatus() == Download.COMPLETED) {
								//final String packageName = app.getPackageName();
								//final FileToDownload downloadedFile = download.getFilesToDownload().get(0);
								displayable.downgrade(context, permissionRequest, download, pojo.getAppId()).subscribe();
							}
						});
						break;
				}
			}, () -> {
				Logger.e(TAG, "unable to access to external FS");
			});
		});
	}

	@Override
	public void onViewAttached() {

	}

	@Override
	public void onViewDetached() {

	}
}
