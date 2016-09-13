/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 18/08/2016.
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
	private int visibleThreshold; // The minimum amount of items to have below your current scroll position before load
	private boolean bypassCache;
	private ErrorRequestListener errorRequestListener;
	private int total;
	private int offset;

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
		if (shouldLoadMore((LinearLayoutManager) recyclerView.getLayoutManager())) {
			// End has been reached, load more items
			onLoadMore(bypassCache);
		}
	}

	private boolean shouldLoadMore(LinearLayoutManager linearLayoutManager) {
		int totalItemCount = linearLayoutManager.getItemCount();
		int lastVisibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();

		boolean hasMoreElements = offset <= total;
		boolean isOverLastPosition = (lastVisibleItemPosition >= (totalItemCount - 1));
		boolean isOverVisibleThreshold = ((lastVisibleItemPosition + visibleThreshold) == (totalItemCount - 1));

		return !loading && (hasMoreElements || offset == 0) && (isOverLastPosition || isOverVisibleThreshold);
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

			if (response.hasData()) {
				if(response.hasStableTotal()) {
					total = response.getTotal();
					offset = response.getNextSize();
				}else {
					total += response.getTotal();
					offset += response.getNextSize();
				}
				v7request.getBody().setOffset(offset);
			}

			// FIXME: 17/08/16 sithengineer use response.getList() instead

			successRequestListener.call(response);

			loading = false;
		}, errorRequestListener, bypassCache);
	}
}
