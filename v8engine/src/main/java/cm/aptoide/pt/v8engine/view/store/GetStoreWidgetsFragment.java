package cm.aptoide.pt.v8engine.view.store;

import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 26-12-2016.
 */

public class GetStoreWidgetsFragment extends StoreTabWidgetsGridRecyclerFragment {

  @Override protected Observable<List<Displayable>> buildDisplayables(boolean refresh, String url) {
    return requestFactory.newStoreWidgets(url)
        .observe(refresh)
        .observeOn(Schedulers.io())
        .flatMap(getStoreWidgets -> loadGetStoreWidgets(getStoreWidgets, refresh, url));
  }
}
