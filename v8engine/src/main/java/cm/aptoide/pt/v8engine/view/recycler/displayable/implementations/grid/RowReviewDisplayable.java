/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 22/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.Review;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * created by SithEngineer
 */
public class RowReviewDisplayable extends DisplayablePojo<Review> {

	public RowReviewDisplayable() { }

	public RowReviewDisplayable(Review pojo) {
		super(pojo);
	}

	public RowReviewDisplayable(Review pojo, boolean fixedPerLineCount) {
		super(pojo, fixedPerLineCount);
	}

	@Override
	public Type getType() {
		return Type.ROW_REVIEW;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_row_review;
	}
}
