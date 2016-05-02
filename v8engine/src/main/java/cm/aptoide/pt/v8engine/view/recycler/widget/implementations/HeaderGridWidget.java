/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 30/04/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.HeaderGridDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 29/04/16.
 */
@Displayables({HeaderGridDisplayable.class})
public class HeaderGridWidget extends Widget<HeaderGridDisplayable> {

	private TextView title;
	private Button more;
	//private RelativeLayout moreLayout;

	public HeaderGridWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		title = (TextView )itemView.findViewById(R.id.title);
		more = (Button )itemView.findViewById(R.id.more);
		//moreLayout = (RelativeLayout )itemView.findViewById(R.id.more_layout);
	}

	@Override
	public void bindView(HeaderGridDisplayable displayable) {
		// TODO

		Object pojo = displayable.getPojo();
		// ...
	}
}
