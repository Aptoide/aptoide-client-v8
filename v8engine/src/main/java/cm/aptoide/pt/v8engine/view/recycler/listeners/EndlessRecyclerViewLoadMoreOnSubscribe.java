package cm.aptoide.pt.v8engine.view.recycler.listeners;

import android.support.v7.widget.RecyclerView;

import com.jakewharton.rxbinding.internal.Preconditions;

import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;
import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

/**
 * Created by marcelobenites on 6/27/16.
 */
public class EndlessRecyclerViewLoadMoreOnSubscribe implements Observable.OnSubscribe<Void> {

	private final RecyclerView recyclerView;
	private final BaseAdapter adapter;

	public EndlessRecyclerViewLoadMoreOnSubscribe(RecyclerView recyclerView, BaseAdapter adapter) {
		this.recyclerView = recyclerView;
		this.adapter = adapter;
	}

	@Override
	public void call(Subscriber<? super Void> subscriber) {
		Preconditions.checkUiThread();

		final EndlessRecyclerOnScrollListener listener = new EndlessRecyclerOnScrollListener(adapter, null, null, null, 0, false) {
			@Override
			public void onLoadMore(boolean bypassCache) {
				if (!subscriber.isUnsubscribed()) {
					subscriber.onNext(null);
				}
			}
		};

		recyclerView.addOnScrollListener(listener);

		subscriber.add(new MainThreadSubscription() {
			@Override protected void onUnsubscribe() {
				recyclerView.removeOnScrollListener(listener);
			}
		});
	}
}
