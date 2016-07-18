/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 15/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.view.View;

import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RateAndReviewCommentDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 14/07/16.
 */
@Displayables({RateAndReviewCommentDisplayable.class})
public class RateAndReviewCommentWidget extends Widget<RateAndReviewCommentDisplayable> {
	
	public RateAndReviewCommentWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		// TODO
	}

	@Override
	public void bindView(RateAndReviewCommentDisplayable displayable) {
		Comment comment = displayable.getPojo();

		// TODO
	}
}
