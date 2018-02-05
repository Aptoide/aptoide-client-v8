package cm.aptoide.pt.store.view.ads;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.WindowManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.store.view.StoreTabGridRecyclerFragment;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import cm.aptoide.pt.view.recycler.displayable.DisplayableGroup;
import cm.aptoide.pt.view.recycler.displayable.GridAdDisplayable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;

/**
 * Created by neuro on 26-12-2016.
 */

public class GetAdsFragment extends StoreTabGridRecyclerFragment {

  private AdsRepository adsRepository;

  public static Fragment newInstance() {
    return new GetAdsFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    adsRepository = ((AptoideApplication) getContext().getApplicationContext()).getAdsRepository();
  }

  @Override protected Observable<List<Displayable>> buildDisplayables(boolean refresh, String url,
      boolean bypassServerCache) {
    return adsRepository.getAdsFromHomepageMore(refresh)
        .map(minimalAds -> {
          List<Displayable> displayables = new LinkedList<>();
          for (MinimalAd minimalAd : minimalAds) {
            displayables.add(new GridAdDisplayable(minimalAd, tag, navigationTracker));
          }

          return Collections.singletonList(new DisplayableGroup(displayables,
              (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE),
              getContext().getResources()));
        });
  }
}
