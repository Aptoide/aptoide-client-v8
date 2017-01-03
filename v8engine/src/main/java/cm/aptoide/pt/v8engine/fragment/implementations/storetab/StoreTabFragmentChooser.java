package cm.aptoide.pt.v8engine.fragment.implementations.storetab;

import android.support.v4.app.Fragment;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.configuration.FragmentProvider;

/**
 * Created by neuro on 03-01-2017.
 */

public class StoreTabFragmentChooser {

  private static FragmentProvider fragmentProvider = V8Engine.getFragmentProvider();

  static Fragment choose(Event.Name name) {
    switch (name) {
      case listApps:
        return fragmentProvider.newListAppsFragment();
      case getStore:
        return fragmentProvider.newGetStoreFragment();
      case getStoresRecommended:
      case getMyStoresSubscribed:
        return fragmentProvider.newMyStoresSubscribedFragment();
      case myStores:
        return fragmentProvider.newMyStoresFragment();
      case getStoreWidgets:
        return fragmentProvider.newGetStoreWidgetsFragment();
      case listReviews:
        return fragmentProvider.newListReviewsFragment();
      case getAds:
        return fragmentProvider.newGetAdsFragment();
      case listStores:
        return fragmentProvider.newListStoresFragment();
      default:
        throw new RuntimeException("Fragment " + name + " not implemented!");
    }
  }

  public static boolean validateAcceptedName(Event.Name name) {
    if (name != null) {
      switch (name) {
        case myStores:
        case getMyStoresSubscribed:
        case getStoresRecommended:
        case listApps:
        case getStore:
        case getStoreWidgets:
        case getReviews:
          //case getApkComments:
        case getAds:
        case listStores:
        case listComments:
        case listReviews:
          return true;
      }
    }

    return false;
  }
}
