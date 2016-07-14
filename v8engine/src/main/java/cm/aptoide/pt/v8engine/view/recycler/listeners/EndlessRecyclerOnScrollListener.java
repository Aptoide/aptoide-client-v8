/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 21/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.listeners;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.BaseV7EndlessResponse;
import cm.aptoide.pt.networkclient.interfaces.ErrorRequestListener;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.ProgressBarDisplayable;
import rx.functions.Action1;

public class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

	public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();

	private final BaseAdapter adapter;
	private final V7<? extends BaseV7EndlessResponse, ? extends Endless> v7request;
	private final Action1 successRequestListener;

	private boolean loading;
	private int previousTotal = 0; // The total number of items in the dataset after the last load
	private int visibleThreshold; // The minimum amount of items to have below your current scroll position before
	private boolean bypassCache;
	private int offset;
	private int totalCountResponse;
	private ErrorRequestListener errorRequestListener;

	public <T extends BaseV7EndlessResponse> EndlessRecyclerOnScrollListener(BaseAdapter baseAdapter, V7<T, ?
			extends
			Endless> v7request, Action1<T> successRequestListener, ErrorRequestListener errorRequestListener, boolean bypassCache) {
		this(baseAdapter, v7request, successRequestListener, errorRequestListener, 6, bypassCache);
	}

	public <T extends BaseV7EndlessResponse> EndlessRecyclerOnScrollListener(BaseAdapter baseAdapter, V7<T, ?
			extends
			Endless> v7request, Action1<T> successRequestListener, ErrorRequestListener errorRequestListener, int visibleThreshold, boolean bypassCache) {
		this.adapter = baseAdapter;
		this.v7request = v7request;
		this.successRequestListener = successRequestListener;
		this.errorRequestListener = errorRequestListener;
		this.visibleThreshold = visibleThreshold;
		this.bypassCache = bypassCache;
	}

	@Override
	public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
		super.onScrolled(recyclerView, dx, dy);

		LinearLayoutManager mLinearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

		int visibleItemCount = recyclerView.getChildCount();
		int totalItemCount = mLinearLayoutManager.getItemCount();
		int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

		if (loading) {
			if (totalItemCount > previousTotal) {
				previousTotal = totalItemCount;
			}
		}
		if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
			if (offset < totalCountResponse) {
				// End has been reached, load more items
				onLoadMore(bypassCache);
			}
		}
	}

	// Protected against in the constructor, hopefully..
	@SuppressWarnings("unchecked")
	public void onLoadMore(boolean bypassCache) {
		loading = true;
		adapter.addDisplayable(new ProgressBarDisplayable());

		v7request.execute(response -> {
			if (adapter.getItemCount() > 0) {
				adapter.popDisplayable();
			}

			if (response.getDatalist() != null) {
				v7request.getBody().setOffset(response.getDatalist().getNext());
			}
			successRequestListener.call(response);

			loading = false;
		}, errorRequestListener, bypassCache);
	}
}