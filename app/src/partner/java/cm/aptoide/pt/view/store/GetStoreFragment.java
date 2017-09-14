package cm.aptoide.pt.view.store;

import cm.aptoide.pt.PartnerApplication;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStore;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import cm.aptoide.pt.view.store.home.AdultRowDisplayable;
import cm.aptoide.pt.view.store.home.HomeFragment;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by diogoloureiro on 13/09/2017.
 */

public class GetStoreFragment extends StoreTabWidgetsGridRecyclerFragment {

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
        .flatMap(getStore -> loadGetStoreWidgets(getStore.getNodes()
            .getWidgets(), refresh, url))
        .doOnNext(displayables -> {
          // We only want Adult Switch in Home Fragment.
          if (getParentFragment() != null && getParentFragment() instanceof HomeFragment) {
            if (((PartnerApplication) getContext().getApplicationContext()).getBootConfig()
                .getPartner()
                .getSwitches()
                .getMature()
                .isEnable()) {
              displayables.add(new AdultRowDisplayable(GetStoreFragment.this));
            }
          }
        });
  }
}

