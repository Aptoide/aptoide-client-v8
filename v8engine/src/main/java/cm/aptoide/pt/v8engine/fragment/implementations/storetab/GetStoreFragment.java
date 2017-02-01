package cm.aptoide.pt.v8engine.fragment.implementations.storetab;

import cm.aptoide.pt.v8engine.fragment.implementations.HomeFragment;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.AdultRowDisplayable;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 26-12-2016.
 */

public class GetStoreFragment extends StoreTabWidgetsGridRecyclerFragment {

  @Override protected Observable<List<Displayable>> buildDisplayables(boolean refresh, String url) {
    return requestFactory.newStore(url)
        .observe(refresh)
        .observeOn(Schedulers.io())
        .flatMap(getStore -> loadGetStoreWidgets(getStore.getNodes().getWidgets(), refresh, url))
        .doOnNext(displayables -> {
          // We only want Adult Switch in Home Fragment.
          if (getParentFragment() != null && getParentFragment() instanceof HomeFragment) {
            displayables.add(new AdultRowDisplayable());
          }
        });
  }
}
