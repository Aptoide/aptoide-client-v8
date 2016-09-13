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
import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RollbackDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import java.text.DateFormat;
import rx.subscriptions.CompositeSubscription;

import static android.text.format.DateFormat.getTimeFormat;

/**
 * Created by sithengineer on 14/06/16.
 */
public class RollbackWidget extends Widget<RollbackDisplayable> {

	private static final String TAG = RollbackWidget.class.getSimpleName();

	private ImageView appIcon;
	private TextView appName;
	private TextView appUpdateVersion;
	private TextView appState;
	private TextView rollbackAction;
	private CompositeSubscription compositeSubscription;

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

		if (compositeSubscription == null || compositeSubscription.isUnsubscribed()) {
			compositeSubscription = new CompositeSubscription();
		}

		ImageLoader.load(pojo.getIcon(), appIcon);
		appName.setText(pojo.getAppName());
		appUpdateVersion.setText(pojo.getVersionName());

		StringBuilder builder = new StringBuilder();
		switch (Rollback.Action.valueOf(pojo.getAction())) {
			case UPDATE:
				builder.append(getContext().getString(R.string.rollback_updated));
				rollbackAction.setText(R.string.downgrade);
				break;
			case DOWNGRADE:
				builder.append(getContext().getString(R.string.rollback_downgraded));
				rollbackAction.setText(R.string.update);
				break;
			case UNINSTALL:
				builder.append(getContext().getString(R.string.rollback_uninstalled));
				rollbackAction.setText(R.string.install);
				break;
			case INSTALL:
				builder.append(getContext().getString(R.string.rollback_installed));
				rollbackAction.setText(R.string.uninstall);
				break;
		}
		DateFormat timeFormat = getTimeFormat(getContext());
		builder.append(" ");
		builder.append(String.format(getContext().getString(R.string.at_time),
				timeFormat.format(pojo.getTimestamp())));
		appState.setText(builder.toString());

		rollbackAction.setOnClickListener( view -> {

			//			@Cleanup
			//			Realm realm = Database.get();
			//			Database.RollbackQ.upadteRollbackWithAction(realm, pojo, Rollback.Action.UPDATE);

			final Context context = view.getContext();
			ContextWrapper contextWrapper = (ContextWrapper) context;
			final PermissionRequest permissionRequest = ((PermissionRequest) contextWrapper.getBaseContext());

			permissionRequest.requestAccessToExternalFileSystem(() -> {
				Rollback.Action action = Rollback.Action.valueOf(pojo.getAction());
				switch (action) {
					case DOWNGRADE:
						displayable.update((FragmentShower) getContext());
						break;
					case INSTALL:
						//only if the app is installed
						//ShowMessage.asSnack(view, R.string.uninstall_msg);
						ShowMessage.asSnack(view, R.string.uninstall);
						displayable.uninstall(getContext(), displayable.getDownloadFromPojo())
								.subscribe(uninstalled -> {
								}, throwable -> throwable.printStackTrace());
						break;

					case UNINSTALL:
						displayable.install((FragmentShower) getContext());
						break;

					case UPDATE:
						displayable.update((FragmentShower) getContext());
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
		if (compositeSubscription != null) {
			compositeSubscription.unsubscribe();
		}
	}
}
