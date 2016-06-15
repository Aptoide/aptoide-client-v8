/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 14/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ScheduleDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 14/06/16.
 */@Displayables({ScheduleDownloadDisplayable.class})
public class ScheduledDownloadWidget extends Widget<ScheduleDownloadDisplayable> {

	private static final String TAG = ScheduledDownloadWidget.class.getSimpleName();

	private TextView appName;
	private TextView appVersion;
	private ImageView appIcon;
	private CheckBox checkBox;

	public ScheduledDownloadWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		appIcon = (ImageView) itemView.findViewById(R.id.icon);
		appName = (TextView) itemView.findViewById(R.id.name);
		appVersion = (TextView) itemView.findViewById(R.id.version);
		checkBox = (CheckBox) itemView.findViewById(R.id.check_box);
	}

	@Override
	public void bindView(ScheduleDownloadDisplayable displayable) {
		try{
			final GetAppMeta.App app = displayable.getPojo().getNodes().getMeta().getData();

			ImageLoader.load(app.getIcon(), appIcon);
			appName.setText(app.getName());
			appVersion.setText(app.getFile().getVername());
			checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
				// TODO
				ShowMessage.show(buttonView, "TO DO");
			});

		}catch (Exception ex) {
			Logger.e(TAG, "unable to bind view", ex);
		}
	}
}
