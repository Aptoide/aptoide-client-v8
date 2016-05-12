/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 12/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.utils.ScreenUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.adapters.ScreenshotsAdapter;
import cm.aptoide.pt.v8engine.view.custom.DividerItemDecoration;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView
		.AppViewImagesDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 11/05/16.
 */
@Displayables({AppViewImagesDisplayable.class})
public class AppViewImagesWidget extends Widget<AppViewImagesDisplayable> {

	private RecyclerView mediaList;

	public AppViewImagesWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		mediaList = (RecyclerView) itemView.findViewById(R.id.screenshots_list);
	}

	@Override
	public void bindView(AppViewImagesDisplayable displayable) {
		final GetAppMeta.App app = displayable.getPojo();

		mediaList.addItemDecoration(new DividerItemDecoration(ScreenUtils.getPixels(V8Engine
				.getContext(), 5))
		);
		mediaList.setLayoutManager(new LinearLayoutManager(
				itemView.getContext(), LinearLayoutManager.HORIZONTAL, false)
		);
		mediaList.setNestedScrollingEnabled(false); // because otherwise the AppBar won't be collapsed
		mediaList.setAdapter(
				new ScreenshotsAdapter(app.getMedia())
		);
	}
}
