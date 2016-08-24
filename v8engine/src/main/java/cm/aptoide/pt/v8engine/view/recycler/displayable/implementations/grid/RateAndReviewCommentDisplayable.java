/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 09/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.Review;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.implementations.RateAndReviewsFragment;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * Created by sithengineer on 14/07/16.
 */
public class RateAndReviewCommentDisplayable extends DisplayablePojo<RateAndReviewCommentDisplayable.ReviewWithAppName> {

	@Getter private RateAndReviewsFragment.CommentAdder commentAdder;

	public RateAndReviewCommentDisplayable() {
	}

	public RateAndReviewCommentDisplayable(RateAndReviewCommentDisplayable.ReviewWithAppName pojo) {
		super(pojo);
	}

	public RateAndReviewCommentDisplayable(RateAndReviewCommentDisplayable.ReviewWithAppName pojo, boolean fixedPerLineCount) {
		super(pojo, fixedPerLineCount);
	}

	public RateAndReviewCommentDisplayable(RateAndReviewCommentDisplayable.ReviewWithAppName pojo, RateAndReviewsFragment.CommentAdder commentAdder) {
		super(pojo);
		this.commentAdder = commentAdder;
	}

	@Override
	public Type getType() {
		return Type.RATE_AND_REVIEW;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_rate_and_review;
	}

	@AllArgsConstructor
	@Data
	public static final class ReviewWithAppName {
		private final String appName;
		private final Review review;
	}
}
