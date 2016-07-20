/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 17/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.InstalledAppDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by neuro on 17-05-2016.
 */
@Displayables({InstalledAppDisplayable.class})
public class InstalledAppWidget extends Widget<InstalledAppDisplayable> {

	private TextView labelTextView;
	private TextView verNameTextView;
	private ImageView iconImageView;
	private View installedItemFrame;
	private ViewGroup createReviewLayout;

	public InstalledAppWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		labelTextView = (TextView) itemView.findViewById(R.id.name);
		iconImageView = (ImageView) itemView.findViewById(R.id.icon);
		verNameTextView = (TextView) itemView.findViewById(R.id.app_version);
		installedItemFrame = itemView.findViewById(R.id.installedItemFrame);
		createReviewLayout = (ViewGroup) itemView.findViewById(R.id.reviewButtonLayout);
	}

	@Override
	public void bindView(InstalledAppDisplayable displayable) {
		Installed pojo = displayable.getPojo();

		labelTextView.setText(pojo.getName());
		verNameTextView.setText(pojo.getVersionName());
		ImageLoader.load(pojo.getIcon(), iconImageView);

		installedItemFrame.setOnClickListener(v -> {
			// TODO: 25-05-2016 neuro apagar em principio
		});

		createReviewLayout.setOnClickListener(v -> {
			// TODO: 25-05-2016 neuro create review
			ShowMessage.asToast(getContext(), "Create Review");
		});
	}

	@Override
	public void unbindView() {

	}
}
