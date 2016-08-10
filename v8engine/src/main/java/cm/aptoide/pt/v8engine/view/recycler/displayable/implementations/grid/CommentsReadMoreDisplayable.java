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
import lombok.Getter;

/**
 * Created by trinkes on 8/5/16.
 */
public class CommentsReadMoreDisplayable extends DisplayablePojo<Review> {

	@Getter private RateAndReviewsFragment.CommentAdder commentAdder;
	@Getter private int next;

	public CommentsReadMoreDisplayable() {
	}

	public CommentsReadMoreDisplayable(Review review, int next, RateAndReviewsFragment.CommentAdder commentAdder) {
		super(review);
		this.commentAdder = commentAdder;
		this.next = next;
	}

	@Override
	public Type getType() {
		return Type.READ_MORE_COMMENTS;
	}

	@Override
	public int getViewLayout() {
		return R.layout.comments_read_more_layout;
	}
}
