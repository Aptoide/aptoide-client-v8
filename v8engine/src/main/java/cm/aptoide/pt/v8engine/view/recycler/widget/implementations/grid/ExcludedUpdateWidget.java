/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 15/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.imageloader.ImageLoader;
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
	private TextView packageName;
	private CheckBox isExcluded;

	public ExcludedUpdateWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		icon = (ImageView) itemView.findViewById(R.id.icon);
		name = (TextView) itemView.findViewById(R.id.name);
		versionCode = (TextView) itemView.findViewById(R.id.version_code);
		packageName = (TextView) itemView.findViewById(R.id.apk_id);
		isExcluded = (CheckBox) itemView.findViewById(R.id.is_excluded);
	}

	@Override
	public void bindView(final ExcludedUpdateDisplayable displayable) {
		final Update excludedUpdate = displayable.getPojo();

		ImageLoader.load(excludedUpdate.getIcon(), icon);
		name.setText(excludedUpdate.getLabel());
		versionCode.setText(excludedUpdate.getUpdateVersionName());
		packageName.setText(excludedUpdate.getPackageName());

		isExcluded.setOnCheckedChangeListener((buttonView, isChecked) -> {
			displayable.setSelected(isChecked);
		});

		isExcluded.setChecked(displayable.isSelected());
	}

	@Override
	public void onViewAttached() {

	}

	@Override
	public void onViewDetached() {

	}
}
