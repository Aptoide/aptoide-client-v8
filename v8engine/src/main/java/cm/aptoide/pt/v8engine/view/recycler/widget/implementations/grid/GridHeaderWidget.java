/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.implementations.StoreGridRecyclerFragment;
import cm.aptoide.pt.v8engine.util.FragmentUtils;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 29/04/16.
 */
@Displayables({GridHeaderDisplayable.class})
public class GridHeaderWidget extends Widget<GridHeaderDisplayable> {

	private TextView title;
	private Button more;
	//private RelativeLayout moreLayout;

	public GridHeaderWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		title = (TextView) itemView.findViewById(R.id.title);
		more = (Button) itemView.findViewById(R.id.more);
		//moreLayout = (RelativeLayout )itemView.findViewById(R.id.more_layout);
	}

	@Override
	public void bindView(GridHeaderDisplayable displayable) {
		final GetStoreWidgets.WSWidget pojo = displayable.getPojo();
		final List<GetStoreWidgets.WSWidget.Action> actions = pojo.getActions();
		title.setText(pojo.getTitle());
		more.setVisibility(actions != null && actions.size() > 0 && actions.get(0)
				.getEvent()
				.getName() != null ? View.VISIBLE : View.GONE);
		more.setOnClickListener((view) -> {
			FragmentUtils.replaceFragmentV4((FragmentActivity) itemView.getContext(), StoreGridRecyclerFragment
					.newInstance(displayable
					.getPojo()
					.getActions()
					.get(0)
					.getEvent(), displayable.getPojo().getTitle(), displayable.getStoreTheme()));
		});
	}
}
