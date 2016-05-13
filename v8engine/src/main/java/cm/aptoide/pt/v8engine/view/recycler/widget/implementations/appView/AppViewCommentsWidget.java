/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.view.View;

import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView
		.AppViewCommentsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 04/05/16.
 */
@Displayables({AppViewCommentsDisplayable.class})
public class AppViewCommentsWidget extends Widget<AppViewCommentsDisplayable> {

	private View writeCommentView;

	public AppViewCommentsWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		writeCommentView = itemView.findViewById(R.id.write_comment);
	}

	@Override
	public void bindView(AppViewCommentsDisplayable displayable) {
		final GetAppMeta.App app = displayable.getPojo();

		// TODO

		writeCommentView.setOnClickListener(v -> ShowMessage.show(v, "TO DO"));
	}
}
