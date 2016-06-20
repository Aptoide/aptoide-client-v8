/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 08/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.listeners;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import cm.aptoide.pt.dataprovider.ws.v7.OffsetInterface;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.BaseV7EndlessResponse;
import cm.aptoide.pt.networkclient.interfaces.ErrorRequestListener;
import cm.aptoide.pt.v8engine.fragment.BaseRecyclerViewFragment;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.ProgressBarDisplayable;
import rx.functions.Action1;

public class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

	public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();

	private final BaseRecyclerViewFragment baseRecyclerViewFragment;
	private final V7<? extends BaseV7EndlessResponse, ? extends OffsetInterface<?>> v7request;
	private final Action1 successRequestListener;

	private boolean loading;
	private int previousTotal = 0; // The total number of items in the dataset after the last load
	private int visibleThreshold; // The minimum amount of items to have below your current scroll position before
	private ErrorRequestListener errorRequestListener;
	// loading more.

	public <T extends BaseV7EndlessResponse> EndlessRecyclerOnScrollListener(BaseRecyclerViewFragment
			                                                                         baseRecyclerViewFragment, V7<T, ?
			extends
			OffsetInterface<?>> v7request, Action1<T> successRequestListener, ErrorRequestListener
			errorRequestListener) {
		this(baseRecyclerViewFragment, v7request, successRequestListener, errorRequestListener, 6);
	}

	public <T extends BaseV7EndlessResponse> EndlessRecyclerOnScrollListener(BaseRecyclerViewFragment
			                                                                         baseRecyclerViewFragment, V7<T, ?
			extends
			OffsetInterface<?>> v7request, Action1<T> successRequestListener, ErrorRequestListener
			errorRequestListener, int visibleThreshold) {
		this.baseRecyclerViewFragment = baseRecyclerViewFragment;
		this.v7request = v7request;
		this.successRequestListener = successRequestListener;
		this.errorRequestListener = errorRequestListener;
		this.visibleThreshold = visibleThreshold;

		onLoadMore();
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
			// End has been reached, load more items
			onLoadMore();
		}
	}

	// Protected against in the constructor, hopefully..
	@SuppressWarnings("unchecked")
	public void onLoadMore() {
		loading = true;
		baseRecyclerViewFragment.getAdapter().addDisplayable(new ProgressBarDisplayable());

		v7request.execute(response -> {
			v7request.getBody().setOffset(response.getDatalist().getNext());
			if (baseRecyclerViewFragment.getAdapter().getDisplayables().size() > 0) {
				baseRecyclerViewFragment.getAdapter().popDisplayable();
			}

			successRequestListener.call(response);

			loading = false;
		}, errorRequestListener);
	}
}