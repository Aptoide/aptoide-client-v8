/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 25/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.UpdateDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import io.realm.Realm;
import lombok.Cleanup;

/**
 * Created by neuro on 17-05-2016.
 */
public class UpdateWidget extends Widget<UpdateDisplayable> {

	private TextView labelTextView;
	private ImageView iconImageView;
	private TextView installedVernameTextView;
	private TextView updateVernameTextView;
	private ViewGroup updateButtonLayout;

	public UpdateWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		labelTextView = (TextView) itemView.findViewById(R.id.name);
		iconImageView = (ImageView) itemView.findViewById(R.id.icon);
		installedVernameTextView = (TextView) itemView.findViewById(R.id.app_installed_version);
		updateVernameTextView = (TextView) itemView.findViewById(R.id.app_update_version);
		updateButtonLayout = (ViewGroup) itemView.findViewById(R.id.updateButtonLayout);
	}

	@Override
	public void bindView(UpdateDisplayable updateDisplayable) {
		Update pojo = updateDisplayable.getPojo();

		@Cleanup Realm realm = Database.get();

		labelTextView.setText(pojo.getLabel());
		installedVernameTextView.setText(Database.InstalledQ.get(pojo.getPackageName(), realm).getVersionName());
		updateVernameTextView.setText(pojo.getUpdateVersionName());
		ImageLoader.load(pojo.getIcon(), iconImageView);
		updateButtonLayout.setOnClickListener(view -> {
			// TODO: 24-05-2016 neuro implementar
			ShowMessage.show(view, "TO DO");
		});
	}
}
