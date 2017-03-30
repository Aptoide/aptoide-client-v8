package cm.aptoide.pt.v8engine.fragment.implementations.storetab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.repository.AdsRepository;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayableGroup;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridAdDisplayable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;

/**
 * Created by neuro on 26-12-2016.
 */

public class GetAdsFragment extends StoreTabGridRecyclerFragment {

  private AdsRepository adsRepository;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final AptoideAccountManager accountManager =
        ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    adsRepository = new AdsRepository(
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(), getContext()),
        accountManager);
  }

  @Override protected Observable<List<Displayable>> buildDisplayables(boolean refresh, String url) {
    return adsRepository.getAdsFromHomepageMore(refresh).map(minimalAds -> {
      List<Displayable> displayables = new LinkedList<>();
      for (MinimalAd minimalAd : minimalAds) {
        displayables.add(new GridAdDisplayable(minimalAd, tag));
      }

      return Collections.singletonList(new DisplayableGroup(displayables));
    });
  }
}
