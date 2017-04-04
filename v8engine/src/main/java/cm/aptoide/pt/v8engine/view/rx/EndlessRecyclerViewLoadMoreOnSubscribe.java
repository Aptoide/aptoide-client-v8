package cm.aptoide.pt.v8engine.view.rx;

import android.support.v7.widget.RecyclerView;
import cm.aptoide.pt.v8engine.view.recycler.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.EndlessRecyclerOnScrollListener;
import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

import static rx.android.MainThreadSubscription.verifyMainThread;

/**
 * Created by marcelobenites on 6/27/16.
 */
public class EndlessRecyclerViewLoadMoreOnSubscribe implements Observable.OnSubscribe<Integer> {

  private final RecyclerView recyclerView;
  private final BaseAdapter adapter;

  public EndlessRecyclerViewLoadMoreOnSubscribe(RecyclerView recyclerView, BaseAdapter adapter) {
    this.recyclerView = recyclerView;
    this.adapter = adapter;
  }

  @Override public void call(Subscriber<? super Integer> subscriber) {
    verifyMainThread();

    final EndlessRecyclerOnScrollListener listener = new EndlessRecyclerOnScrollListener(adapter) {
      @Override public void onLoadMore(boolean bypassCache) {
        if (!subscriber.isUnsubscribed()) {
          subscriber.onNext(adapter.getItemCount());
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
