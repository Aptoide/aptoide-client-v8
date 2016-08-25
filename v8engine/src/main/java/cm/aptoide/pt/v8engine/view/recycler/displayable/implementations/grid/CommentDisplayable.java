/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 25/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by trinkes on 8/4/16.
 */
public class CommentDisplayable extends DisplayablePojo<Comment> {
	
	public CommentDisplayable(Comment pojo) {
		super(pojo);
	}

	public CommentDisplayable() {
	}

	@Override
	public Type getType() {
		return Type.APP_COMMENT_TO_REVIEW;
	}

	@Override
	public int getViewLayout() {
		return R.layout.comment_layout;
	}
}
