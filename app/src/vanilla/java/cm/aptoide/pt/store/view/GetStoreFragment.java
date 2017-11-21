package cm.aptoide.pt.store.view;

import android.support.v4.app.Fragment;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStore;
import cm.aptoide.pt.store.view.home.AdultRowDisplayable;
import cm.aptoide.pt.store.view.home.HomeFragment;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 26-12-2016.
 */

public class GetStoreFragment extends StoreTabWidgetsGridRecyclerFragment {

  public static Fragment newInstance() {
    return new GetStoreFragment();
  }

  @Override protected Observable<List<Displayable>> buildDisplayables(boolean refresh, String url) {
    return getStoreObservable(refresh, url).observeOn(Schedulers.io())
        .flatMap(getStore -> parseDisplayables(getStore.getNodes()
            .getWidgets()))
        .doOnNext(displayables -> {
          // We only want one Adult Switch in Home Fragment.
          if (getParentFragment() != null
              && getParentFragment() instanceof HomeFragment) {
            displayables.add(new AdultRowDisplayable(GetStoreFragment.this));
          }
        });
  }

  private Observable<GetStore> getStoreObservable(boolean refresh, String url) {
    if (name == Event.Name.getUser) {
      return requestFactoryCdnPool.newGetUser(url)
          .observe(refresh);
    }

    return requestFactoryCdnPool.newStore(url)
        .observe(refresh);
  }
}
