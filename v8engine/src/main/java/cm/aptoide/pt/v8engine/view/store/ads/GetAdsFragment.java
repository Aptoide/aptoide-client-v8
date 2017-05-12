package cm.aptoide.pt.v8engine.view.store.ads;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.ads.AdsRepository;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayableGroup;
import cm.aptoide.pt.v8engine.view.recycler.displayable.GridAdDisplayable;
import cm.aptoide.pt.v8engine.view.store.StoreTabGridRecyclerFragment;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
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
    final OkHttpClient httpClient =
        ((V8Engine) getContext().getApplicationContext()).getDefaultClient();
    final Converter.Factory converterFactory = WebService.getDefaultConverter();
    adsRepository =
        new AdsRepository(((V8Engine) getContext().getApplicationContext()).getIdsRepository(),
            accountManager, httpClient, converterFactory);
  }

  @Override protected Observable<List<Displayable>> buildDisplayables(boolean refresh, String url) {
    return adsRepository.getAdsFromHomepageMore(refresh)
        .map(minimalAds -> {
          List<Displayable> displayables = new LinkedList<>();
          for (MinimalAd minimalAd : minimalAds) {
            displayables.add(new GridAdDisplayable(minimalAd, tag));
          }

          return Collections.singletonList(new DisplayableGroup(displayables));
        });
  }
}
