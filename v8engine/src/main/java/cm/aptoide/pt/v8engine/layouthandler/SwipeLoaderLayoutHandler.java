/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 12/05/2016.
 */

package cm.aptoide.pt.v8engine.layouthandler;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.interfaces.ReloadInterface;

/**
 * Handler for Swipe Loader Layouts. Needs five identified views in the corresponding layout:<br>
 * <br>&#9{@link R.id#progress_bar} <br>&#9{@link R.id#generic_error} <br>&#9{@link
 * R.id#no_network_connection} <br>&#9{@link R.id#retry} <br>&#9{@link R.id#swipe_container}
 */
public class SwipeLoaderLayoutHandler extends LoaderLayoutHandler {

	protected SwipeRefreshLayout swipeContainer;

	public SwipeLoaderLayoutHandler(int baseViewId, ReloadInterface reloadInterface) {
		super(baseViewId, reloadInterface);
	}

	@Override
	public void bindViews(View view) {
		super.bindViews(view);
		swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
		swipeContainer.setColorSchemeResources(R.color.default_progress_bar_color, R.color
				.default_color, R.color.default_progress_bar_color, R.color.default_color);
		swipeContainer.setOnRefreshListener(((ReloadInterface) loadInterface)::reload);
	}

	@Override
	public void onFinishLoading(Throwable throwable) {
		super.onFinishLoading(throwable);
		swipeContainer.setRefreshing(false);
		swipeContainer.setEnabled(false);
	}

	@Override
	protected void onFinishLoading() {
		super.onFinishLoading();
		swipeContainer.setRefreshing(false);
	}

	@Override
	public void restoreState() {
		super.restoreState();
		swipeContainer.setEnabled(true);
	}
}
