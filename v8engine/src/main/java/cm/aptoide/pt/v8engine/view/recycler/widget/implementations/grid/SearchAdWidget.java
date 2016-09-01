/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 24/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

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
	private TextView sponsored;
	private TextView description;

	public SearchAdWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		name = (TextView) itemView.findViewById(R.id.name);
		icon = (ImageView) itemView.findViewById(R.id.icon);
		description = (TextView) itemView.findViewById(R.id.description);
		sponsored = (TextView) itemView.findViewById(R.id.sponsored_label);
	}

	@Override
	public void bindView(SearchAdDisplayable displayable) {
		GetAdsResponse.Ad ad = displayable.getPojo();

		name.setText(ad.getData().getName());
		description.setText(Html.fromHtml(ad.getData().getDescription()));
		sponsored.setTypeface(null, Typeface.BOLD);
		sponsored.setText((getContext().getResources().getText(R.string.sponsored) + "").toUpperCase());
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
