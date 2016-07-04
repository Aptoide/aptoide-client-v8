/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.view.View;
import android.widget.Button;

import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewRateAndCommentsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 30/06/16.
 */
@Displayables({AppViewRateAndCommentsDisplayable.class})
public class AppViewRateAndCommentsWidget extends Widget<AppViewRateAndCommentsDisplayable> {

	private Button rateThisButton;
	private Button readAllButton;

	public AppViewRateAndCommentsWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		rateThisButton = (Button) itemView.findViewById(R.id.rate_this_button);
		readAllButton = (Button) itemView.findViewById(R.id.read_all_button);
	}

	@Override
	public void bindView(AppViewRateAndCommentsDisplayable displayable) {
		GetApp pojo = displayable.getPojo();

		// TODO
		rateThisButton.setOnClickListener(v -> {
			ShowMessage.asSnack(v, "TO DO: rate this app");
		});

		readAllButton.setOnClickListener(v -> {
			ShowMessage.asSnack(v, "TO DO: go to all comments");
		});
	}
}
