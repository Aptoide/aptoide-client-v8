/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 08/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.ExcludedUpdate;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.implementations.AppViewFragment;
import cm.aptoide.pt.v8engine.util.FragmentUtils;
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
		updateButtonLayout.setOnClickListener(v -> {
			// TODO: 24-05-2016 neuro implementar
			ShowMessage.toast(getContext(), "Update app " + pojo.getLabel());
		});
		itemView.setLongClickable(true);
		itemView.setOnLongClickListener(v -> {
			AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
			View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.exclude_update_uninstall_menu,
					null);
			builder.setView(dialogView);
			final AlertDialog alertDialog = builder.create();
			dialogView.findViewById(R.id.confirmIgnoreUpdateUninstall).setOnClickListener(v1 -> {

				// Add to excluded
				Database.save(new ExcludedUpdate(pojo), realm);

				// Remove the update
				Database.delete(Database.UpdatesQ.get(pojo.getPackageName(), realm), realm);

				ShowMessage.show(itemView, R.string.ignored);

				alertDialog.dismiss();
			});
			alertDialog.show();
			return true;
		});
		itemView.setOnClickListener(v -> {
			FragmentUtils.replaceFragmentV4(getContext(), AppViewFragment.newInstance(updateDisplayable.getPojo()
					.getAppId()));
		});
	}
}
