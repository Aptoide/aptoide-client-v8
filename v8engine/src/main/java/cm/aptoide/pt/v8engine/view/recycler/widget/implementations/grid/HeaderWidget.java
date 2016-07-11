/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 27/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.HeaderDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 27/06/16.
 */
public class HeaderWidget extends Widget<HeaderDisplayable> {

	private Button more;
	private TextView title;

	public HeaderWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		title = (TextView) itemView.findViewById(R.id.title);
		more = (Button) itemView.findViewById(R.id.more);
	}

	@Override
	public void bindView(HeaderDisplayable displayable) {
		String pojo = displayable.getPojo();
		title.setText(pojo);

		more.setVisibility(View.GONE);
		title.setVisibility(View.VISIBLE);
	}
}
