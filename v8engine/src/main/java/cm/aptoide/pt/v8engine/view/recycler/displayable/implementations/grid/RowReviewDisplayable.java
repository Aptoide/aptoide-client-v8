/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 25/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.support.v4.util.Pair;

import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.model.v7.Review;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * created by SithEngineer
 */
public class RowReviewDisplayable extends DisplayablePojo<Pair<Review,GetAppMeta.App>> {

	public RowReviewDisplayable() { }

	public RowReviewDisplayable(Pair<Review,GetAppMeta.App> pojo) {
		super(pojo);
	}

	public RowReviewDisplayable(Pair<Review,GetAppMeta.App> pojo, boolean fixedPerLineCount) {
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
