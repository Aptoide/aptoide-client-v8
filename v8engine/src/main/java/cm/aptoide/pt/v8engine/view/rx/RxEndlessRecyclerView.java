package cm.aptoide.pt.v8engine.view.rx;

import android.support.v7.widget.RecyclerView;
import cm.aptoide.pt.v8engine.view.recycler.BaseAdapter;
import rx.Observable;

/**
 * Created by marcelobenites on 6/27/16.
 */
public final class RxEndlessRecyclerView {

  private RxEndlessRecyclerView() {
    new AssertionError("No instances!");
  }

  public static Observable<Integer> loadMore(RecyclerView recyclerView, BaseAdapter adapter) {
    return Observable.create(new EndlessRecyclerViewLoadMoreOnSubscribe(recyclerView, adapter));
  }
}
