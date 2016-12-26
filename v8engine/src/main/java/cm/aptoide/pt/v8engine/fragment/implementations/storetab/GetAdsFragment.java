package cm.aptoide.pt.v8engine.fragment.implementations.storetab;

import android.os.Bundle;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.v8engine.repository.AdsRepository;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridAdDisplayable;
import java.util.LinkedList;

/**
 * Created by neuro on 26-12-2016.
 */

public class GetAdsFragment extends StoreTabGridRecyclerFragment {

  private static final AdsRepository adsRepository = new AdsRepository();

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);

    adsRepository.getAdsFromHomepageMore().subscribe(minimalAds -> {
      displayables = new LinkedList<>();
      for (MinimalAd minimalAd : minimalAds) {
        displayables.add(new GridAdDisplayable(minimalAd, tag));
      }

      addDisplayables(displayables);
    }, e -> finishLoading());
  }
}
