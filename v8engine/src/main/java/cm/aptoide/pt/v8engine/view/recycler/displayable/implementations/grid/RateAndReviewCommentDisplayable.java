/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 20/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.FullReview;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by sithengineer on 14/07/16.
 */
public class RateAndReviewCommentDisplayable extends DisplayablePojo<FullReview> {

	public RateAndReviewCommentDisplayable() {
	}

	public RateAndReviewCommentDisplayable(FullReview pojo) {
		super(pojo);
	}

	public RateAndReviewCommentDisplayable(FullReview pojo, boolean fixedPerLineCount) {
		super(pojo, fixedPerLineCount);
	}

	@Override
	public Type getType() {
		return Type.RATE_AND_REVIEW;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_rate_and_review;
	}
}
