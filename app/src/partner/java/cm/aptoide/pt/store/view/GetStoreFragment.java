package cm.aptoide.pt.store.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import cm.aptoide.pt.PartnerApplication;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStore;
import cm.aptoide.pt.store.view.home.AdultRowDisplayable;
import cm.aptoide.pt.store.view.home.HomeFragment;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by diogoloureiro on 13/09/2017.
 */

public class GetStoreFragment extends StoreTabWidgetsGridRecyclerFragment {

  public static Fragment newInstance() {
    return new GetStoreFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override protected Observable<List<Displayable>> buildDisplayables(boolean refresh, String url,
      boolean bypassServerCache) {
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
            if (isMatureEnabledInPartners()) {
              displayables.add(new AdultRowDisplayable(GetStoreFragment.this));
            }
          }
        });
  }

  private boolean isMatureEnabledInPartners() {
    return ((PartnerApplication) getContext().getApplicationContext()).getBootConfig()
        .getPartner()
        .getSwitches()
        .getMature()
        .isEnable();
  }
}

