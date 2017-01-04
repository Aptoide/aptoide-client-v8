package cm.aptoide.pt.v8engine.fragment.implementations.storetab;

import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.v8engine.repository.AdsRepository;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridAdDisplayable;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;

/**
 * Created by neuro on 26-12-2016.
 */

public class GetAdsFragment extends StoreTabGridRecyclerFragment {

  private static final AdsRepository adsRepository = new AdsRepository();

  @Override protected Observable<List<Displayable>> buildDisplayables(boolean refresh, String url) {
    return adsRepository.getAdsFromHomepageMore().map(minimalAds -> {
      List<Displayable> displayables = new LinkedList<>();
      for (MinimalAd minimalAd : minimalAds) {
        displayables.add(new GridAdDisplayable(minimalAd, tag));
      }

      return displayables;
    });
  }
}
