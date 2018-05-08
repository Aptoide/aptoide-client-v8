package cm.aptoide.pt.store.view;

import android.support.v4.app.Fragment;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStore;
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

  @Override protected Observable<List<Displayable>> buildDisplayables(boolean refresh, String url,
      boolean bypassServerCache) {
    return getStoreObservable(refresh, url, bypassServerCache).observeOn(Schedulers.io())
        .flatMap(getStore -> parseDisplayables(getStore.getNodes()
            .getWidgets()));
  }

  private Observable<GetStore> getStoreObservable(boolean refresh, String url,
      boolean bypassServerCache) {
    if (name == Event.Name.getUser) {
      return requestFactoryCdnPool.newGetUser(url)
          .observe(refresh, bypassServerCache);
    }

    return requestFactoryCdnPool.newStore(url)
        .observe(refresh, bypassServerCache);
  }
}
