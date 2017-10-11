package cm.aptoide.pt.view.store;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStore;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import cm.aptoide.pt.view.store.home.AdultRowDisplayable;
import cm.aptoide.pt.view.store.home.HomeFragment;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 26-12-2016.
 */

public class GetStoreFragment extends StoreTabWidgetsGridRecyclerFragment {

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override protected Observable<List<Displayable>> buildDisplayables(boolean refresh, String url) {
    Observable<GetStore> getStoreObservable;
    if (name == Event.Name.getUser) {
      getStoreObservable = requestFactoryCdnPool.newGetUser(url)
          .observe(refresh);
    } else {
      getStoreObservable = requestFactoryCdnPool.newStore(url)
          .observe(refresh);
    }
    return getStoreObservable.observeOn(Schedulers.io())
        .flatMap(getStore -> parseDisplayables(getStore.getNodes()
            .getWidgets()))
        .doOnNext(displayables -> {
          // We only want Adult Switch in Home Fragment.
          if (getParentFragment() != null && getParentFragment() instanceof HomeFragment) {
            displayables.add(new AdultRowDisplayable(GetStoreFragment.this));
          }
        });
  }

  public static Fragment newInstance() {
    return new GetStoreFragment();
  }
}
