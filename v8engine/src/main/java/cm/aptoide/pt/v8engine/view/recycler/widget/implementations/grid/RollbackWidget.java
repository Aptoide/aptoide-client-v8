/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 23/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RollbackDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 14/06/16.
 */
public class RollbackWidget extends Widget<RollbackDisplayable> {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());

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
		Rollback pojo = displayable.getPojo();

		ImageLoader.load(pojo.getIcon(), appIcon);
		appName.setText(pojo.getName());
		appUpdateVersion.setText(pojo.getVersionName());
		appState.setText(
			String.format(
				getContext().getString(R.string.rollback_updated_at),
				DATE_FORMAT.format(new Date(pojo.getTimestamp()))
			)
		);

		rollbackAction.setOnClickListener( view -> {
			// TODO
			ShowMessage.asSnack(view, "TO DO");
		});
	}

	@Override
	public void onViewAttached() {

	}

	@Override
	public void onViewDetached() {

	}
}
