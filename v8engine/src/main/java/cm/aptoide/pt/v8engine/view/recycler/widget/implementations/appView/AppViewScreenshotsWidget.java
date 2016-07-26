/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.adapters.ScreenshotsAdapter;
import cm.aptoide.pt.v8engine.view.custom.DividerItemDecoration;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewScreenshotsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 11/05/16.
 */
@Displayables({AppViewScreenshotsDisplayable.class})
public class AppViewScreenshotsWidget extends Widget<AppViewScreenshotsDisplayable> {

	private RecyclerView mediaList;

	public AppViewScreenshotsWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		mediaList = (RecyclerView) itemView.findViewById(R.id.screenshots_list);
	}

	@Override
	public void bindView(AppViewScreenshotsDisplayable displayable) {
		final GetAppMeta.Media media = displayable.getPojo().getMedia();
		if(!isMediaAvailable(media)) {
			mediaList = null;
			itemView.setVisibility(View.GONE);
			return;
		}

		mediaList.addItemDecoration(new DividerItemDecoration(AptoideUtils.ScreenU.getPixels(6), (DividerItemDecoration.RIGHT | DividerItemDecoration.BOTTOM))
		);
		mediaList.setLayoutManager(new LinearLayoutManager(
				itemView.getContext(), LinearLayoutManager.HORIZONTAL, false)
		);
		mediaList.setNestedScrollingEnabled(false); // because otherwise the AppBar won't be collapsed
		mediaList.setAdapter(
				new ScreenshotsAdapter(media)
		);
	}

	@Override
	public void onViewAttached() {

	}

	@Override
	public void onViewDetached() {

	}

	private boolean isMediaAvailable(GetAppMeta.Media media) {
		if(media!=null) {
			List<GetAppMeta.Media.Screenshot> screenshots = media.getScreenshots();
			List<GetAppMeta.Media.Video> videos = media.getVideos();
			boolean hasScreenShots = screenshots!=null && screenshots.size()>0;
			boolean hasVideos = videos!=null && videos.size()>0;
			return hasScreenShots || hasVideos;
		}
		return false;
	}
}
