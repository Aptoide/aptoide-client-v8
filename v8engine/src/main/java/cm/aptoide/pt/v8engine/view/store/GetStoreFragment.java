package cm.aptoide.pt.v8engine.view.store;

import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.model.v7.store.GetStore;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.store.home.AdultRowDisplayable;
import cm.aptoide.pt.v8engine.view.store.home.HomeFragment;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 26-12-2016.
 */

public class GetStoreFragment extends StoreTabWidgetsGridRecyclerFragment {

  @Override protected Observable<List<Displayable>> buildDisplayables(boolean refresh, String url) {
    Observable<GetStore> getStoreObservable;
    if (name == Event.Name.getUser) {
      getStoreObservable = requestFactory.newGetUser(url)
          .observe(refresh);
    } else {
      getStoreObservable = requestFactory.newStore(url)
          .observe(refresh);
    }
    return getStoreObservable.observeOn(Schedulers.io())
        .flatMap(getStore -> loadGetStoreWidgets(getStore.getNodes()
            .getWidgets(), refresh, url))
        .doOnNext(displayables -> {
          // We only want Adult Switch in Home Fragment.
          if (getParentFragment() != null && getParentFragment() instanceof HomeFragment) {
            displayables.add(new AdultRowDisplayable(GetStoreFragment.this));
          }
        });
  }
}
