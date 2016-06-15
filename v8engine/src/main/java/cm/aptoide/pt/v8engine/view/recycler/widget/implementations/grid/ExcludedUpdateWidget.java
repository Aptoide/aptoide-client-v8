/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 15/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ExcludedUpdateDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 15/06/16.
 */
@Displayables({ExcludedUpdateDisplayable.class})
public class ExcludedUpdateWidget extends Widget<ExcludedUpdateDisplayable> {

	private ImageView icon;
	private TextView name;
	private TextView versionCode;
	private TextView apkId;
	private CheckBox isExcluded;

	public ExcludedUpdateWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		icon = (ImageView) itemView.findViewById(R.id.icon);
		name = (TextView) itemView.findViewById(R.id.name);
		versionCode = (TextView) itemView.findViewById(R.id.version_code);
		apkId = (TextView) itemView.findViewById(R.id.apk_id);
		isExcluded = (CheckBox) itemView.findViewById(R.id.is_excluded);
	}

	@Override
	public void bindView(ExcludedUpdateDisplayable displayable) {
		final GetApp pojo = displayable.getPojo();
		GetAppMeta.App app = pojo.getNodes().getMeta().getData();

		ImageLoader.load(app.getIcon(), icon);
		name.setText(app.getName());
		versionCode.setText(app.getFile().getVercode());
		apkId.setText(app.getObb().getMain().getFilename()); // FIXME ??
		isExcluded.setOnCheckedChangeListener((buttonView, isChecked) -> {
			// TODO
			ShowMessage.show(buttonView, "TO DO");
		});
	}
}
