/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 02/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.SearchDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by neuro on 01-06-2016.
 */
public class SearchWidget extends Widget<SearchDisplayable> {

	private TextView name;
	private ImageView icon;
	private TextView downloads;
	private RatingBar ratingBar;
	private ImageView overflow;
	private TextView time;
	private TextView store;
	private ImageView icTrusted;
	private View bottomView;

	public SearchWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {

	}

	@Override
	public void bindView(SearchDisplayable displayable) {
		name = (TextView) itemView.findViewById(R.id.name);
		icon = (ImageView) itemView.findViewById(R.id.icon);
		downloads = (TextView) itemView.findViewById(R.id.downloads);
		ratingBar = (RatingBar) itemView.findViewById(R.id.ratingbar);
		overflow = (ImageView) itemView.findViewById(R.id.overflow);
		time = (TextView) itemView.findViewById(R.id.search_time);
		store = (TextView) itemView.findViewById(R.id.search_store);
		icTrusted = (ImageView) itemView.findViewById(R.id.ic_trusted_search);
		bottomView = itemView.findViewById(R.id.bottom_view);
	}
}
