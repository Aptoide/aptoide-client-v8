/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 24/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.implementations.AppViewFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.SearchAdDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by neuro on 05-11-2015.
 */
public class SearchAdWidget extends Widget<SearchAdDisplayable> {

	private TextView name;
	private ImageView icon;
	private TextView description;
	private TextView store;
	private TextView downloads;
	private TextView versionName;

	public SearchAdWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		name = (TextView) itemView.findViewById(R.id.name);
		icon = (ImageView) itemView.findViewById(R.id.icon);
		downloads = (TextView) itemView.findViewById(R.id.downloads_number);
		versionName = (TextView) itemView.findViewById(R.id.versionName);
		description = (TextView) itemView.findViewById(R.id.description);
		store = (TextView) itemView.findViewById(R.id.search_store);
	}

	@Override
	public void bindView(SearchAdDisplayable displayable) {
		GetAdsResponse.Ad ad = displayable.getPojo();

		name.setText(ad.getData().getName());
		versionName.setText(ad.getData().getVername());
		downloads.setText(AptoideUtils.StringU.withSuffix(ad.getData().getDownloads()));
		description.setText(Html.fromHtml(ad.getData().getDescription()));
		store.setText(ad.getData().getRepo());
		ImageLoader.load(AptoideUtils.IconSizeU.parseIcon(ad.getData().getIcon()), icon);

		itemView.setOnClickListener(view -> {
			//	        AptoideUtils.FlurryAppviewOrigin.addAppviewOrigin("Suggested_Search Result");
			((FragmentShower) view.getContext()).pushFragmentV4(AppViewFragment.newInstance(ad));
		});
	}

	@Override
	public void onViewAttached() {

	}

	@Override
	public void onViewDetached() {

	}
}
