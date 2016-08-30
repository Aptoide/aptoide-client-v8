/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.FullReview;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * created by SithEngineer
 */
public class RowReviewDisplayable extends DisplayablePojo<FullReview> {

	public RowReviewDisplayable() { }

	public RowReviewDisplayable(FullReview pojo) {
		super(pojo);
	}

	public RowReviewDisplayable(FullReview pojo, boolean fixedPerLineCount) {
		super(pojo, fixedPerLineCount);
	}

	@Override
	public Type getType() {
		return Type.REVIEWS_GROUP;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_row_review;
	}
}
